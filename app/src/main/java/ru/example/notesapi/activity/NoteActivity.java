package ru.example.notesapi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import ru.example.notesapi.R;
import ru.example.notesapi.component.ColorsArrayAdapter;
import ru.example.notesapi.component.TagsArrayAdapter;
import ru.example.notesapi.helper.Request;
import ru.example.notesapi.helper.StaticData;
import ru.example.notesapi.model.Note;
import ru.example.notesapi.model.NoteColor;
import ru.example.notesapi.model.Tag;
import ru.example.notesapi.model.User;

public class NoteActivity extends AppCompatActivity {
    Activity actv;
    Context ctx;
    TextView tvID;
    EditText etName;
    EditText etText;
    ImageView imgv;
    ALDWindow aldw = new ALDWindow();
    ArrayList<Tag> Tags;
    ArrayList<Tag> toSet;
    NoteColor selectedColor;
    boolean saved;
    Note copy;

    //ArrayList<Tag>noteTags;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        toSet = new ArrayList<Tag>();
        saved = true;
        //TODO добавить метод получения текста зетки и доделать код формы
        Tags = StaticData.Tags;
        copy = StaticData.currentNote;
        Tags.remove(0);
        tvID = findViewById(R.id.tvIDNote);
        etName = findViewById(R.id.etNameNote);
        etText = findViewById(R.id.etTextNote);
        imgv = findViewById(R.id.imgvColorNote);
        ctx = this;
        actv = this;
        tvID.setText(String.valueOf(StaticData.currentNote.id));
        etName.setText(StaticData.currentNote.name);
        etText.setText(StaticData.currentNote.text);
        imgv.setBackgroundColor(StaticData.currentNote.ColorToInt(StaticData.currentNote.color));
        findTags();

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                StaticData.currentNote.name = string;
                NotSaved();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
        });
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                StaticData.currentNote.text = string;
                NotSaved();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
        });
    }

    void findTags() {
        for (int i = 0; i < Tags.size(); i++) {
            Tags.get(i).inChoice = false;
            for (int j = 0; j < StaticData.currentNote.tags.size(); j++) {
                if (StaticData.currentNote.tags.get(j).id == Tags.get(i).id) {
                    Tags.get(i).inChoice = true;
                } else {
                    continue;
                }
            }
        }
    }

    void Saved() {
        saved = true;
        tvID.setText(String.valueOf(StaticData.currentNote.id));
    }

    void NotSaved() {
        saved = false;
        tvID.setText(String.valueOf(StaticData.currentNote.id) + "*");
    }

    public void onBtnBackClick(View v) {
        if (saved) {
            Intent i = new Intent(ctx, NoteListActivity.class);
            setResult(RESULT_CANCELED, i);
            finish();
        } else {
            aldw = new ALDWindow() {
                @Override
                public void onPosClick() {
                    StaticData.currentNote = copy;
                    Intent i = new Intent(ctx, NoteListActivity.class);
                    setResult(RESULT_CANCELED, i);
                    finish();
                }
            };
            String mess = getResources().getString(R.string.msgSure2) + "\n" + getResources().getString(R.string.msgSure);
            aldw.msgWarn(ctx, getResources().getString(R.string.txtWarning), mess);
        }

        //super.onBackPressed();
    }

    public void onBackPressed() {
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        return;
//        setResult(RESULT_OK, null);
//        finish();
    }

    /**
     * Method set menu layout
     *
     * @param menu The options menu in which you place your items.
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCtreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    /**
     * Method read type of menu button and executes needed action
     *
     * @param item The menu item that was selected.
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();//id item in menu list
        switch (id) {
            case R.id.itmSave://Create new note
                String newText = StaticData.currentNote.text.replace(System.lineSeparator(), "|~|");
                StaticData.currentNote.text = newText;
                if (SaveNote(User.getToken(), StaticData.currentNote)) {
                    if (SetTagsOnNote()) {
                        Saved();
                        Intent i = new Intent(ctx, NoteListActivity.class);
                        setResult(RESULT_OK, i);
                        finish();
                        return true;
                    } else {
                        aldw.msgInfo(ctx, getResources().getString(R.string.txtError), getResources().getString(R.string.msgRequestFailed));
                        NotSaved();
                        return false;
                    }
                } else {
                    aldw.msgInfo(ctx, getResources().getString(R.string.txtError), getResources().getString(R.string.msgRequestFailed));
                    NotSaved();
                    return false;
                }
            case R.id.itmDel:
                aldw = new ALDWindow() {
                    @Override
                    public void onPosClick() {
                        if (DeleteNote(User.getToken(), StaticData.currentNote.id)) {
                            Intent i = new Intent(ctx, NoteListActivity.class);
                            setResult(RESULT_OK, i);
                            finish();
                        } else {
                            aldw.msgInfo(ctx, getResources().getString(R.string.txtError), getResources().getString(R.string.msgUnexpectedError));
                            return;
                        }

                    }
                };
                aldw.msgWarn(ctx, getResources().getString(R.string.txtWarning), getResources().getString(R.string.msgSure));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnTagsClick(View v) {
        ArrayList<Tag> After = StaticData.currentNote.tags;
        LayoutInflater aldLayout = LayoutInflater.from(ctx);
        View dialogView = aldLayout.inflate(R.layout.tag_select_window, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(dialogView);
        AlertDialog ald = builder.create();
        Context thisCtx = ald.getContext();
        ald.show();
        ListView lvTags = ald.findViewById(R.id.lvTagsTagSelect);
        TagsArrayAdapter adpTags = new TagsArrayAdapter(thisCtx, Tags);
        lvTags.setAdapter(adpTags);
        adpTags.notifyDataSetChanged();
        Button btnokald = ald.findViewById(R.id.btnPosTagSelect);
        btnokald.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.e("test", String.valueOf(adpTags.getBox().size()));
                    StaticData.currentNote.tags.clear();
                    if (adpTags.getBox().size() > 0) {
                        StaticData.currentNote.tags = adpTags.getBox();
                        toSet = adpTags.getBox();

                    } else {
                        //missing
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //missing
                }
                NotSaved();
                ald.dismiss();
            }
        });
    }

    public void onBtnColorsClick(View v) {
        LayoutInflater aldLayout = LayoutInflater.from(ctx);
        View dialogView = aldLayout.inflate(R.layout.color_select_window, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(dialogView);
        AlertDialog ald = builder.create();
        Context thisCtx = ald.getContext();
        ald.show();
        ListView lvColors = ald.findViewById(R.id.lvColorsColorSelect);
        TextView tvSelectedColor = ald.findViewById(R.id.tvSelectedColor);
        ColorsArrayAdapter adpColors = new ColorsArrayAdapter(thisCtx, StaticData.colorList);
        lvColors.setAdapter(adpColors);
        adpColors.notifyDataSetChanged();
        Button btnokald = ald.findViewById(R.id.btnPosColorSelect);
        selectedColor = StaticData.colorList.get(0);
        tvSelectedColor.setText(selectedColor.toString());
        lvColors.setOnItemClickListener((parent, view, position, id) -> {
            selectedColor = (NoteColor) adpColors.getItem(position);
            tvSelectedColor.setText(selectedColor.toString());
            Log.e("Test", selectedColor.colorS);
        });

        btnokald.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                try {
                    StaticData.currentNote.color = selectedColor.StringToColor();
                    Log.e("Test", StaticData.currentNote.ColorToString(StaticData.currentNote.color));
                    imgv.setBackgroundColor(StaticData.currentNote.ColorToInt(StaticData.currentNote.color));
                    //missing
                } catch (Exception e) {
                    e.printStackTrace();
                    //missing
                }
                NotSaved();
                ald.dismiss();
            }
        });
    }

    boolean SetTagsOnNote() {
        boolean result = false;
        if (UnsetTags(User.getToken(), StaticData.currentNote.id, StaticData.Tags)) {
            if (toSet.size() > 0) {
                if (SetTags(User.getToken(), StaticData.currentNote.id, toSet)) {
                    result = true;
                } else {
                    result = false;
                }
            } else {
                result = true;
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Method implements unsetting tag on note by sending request for unsetting tag on note
     *
     * @param token  token, what need for executing request
     * @param NoteID id of note, what need for edit
     * @param tags   list of tags, what need to unset
     */
    public boolean UnsetTags(String token, int NoteID, ArrayList<Tag> tags) {
        boolean result;
        Log.e("test", "Preparing");
        Request unsetTags = new Request(actv) {
            @Override
            public void onFail(String errorMsg) {
                actv.runOnUiThread(() ->
                {
                    String er = getResources().getString(R.string.msgRequestFailed) + "\n" + errorMsg;
                    aldw.msgInfo(actv, getResources().getString(R.string.txtError), er);
                });
            }

            @Override
            public void onSuccess(String res) {
                try {
                    Log.e("test", res.toString());//Call nullReferenceException for to check what required data is complete
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        result = false;
        for (int i = 0; i < tags.size(); i++) {
            try {
                unsetTags.isSuccessful = false;
                JSONObject obj = new JSONObject();
                obj.put("skey", token);
                obj.put("noteid", NoteID);
                obj.put("tagid", tags.get(i).id);
                unsetTags.send(actv, "POST", "/rpc/unset_tag", obj.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                unsetTags.requestSender.join();
                Log.e("test", "Joined");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (unsetTags.isSuccessful) {
                try {
                    unsetTags.onSuccess(unsetTags.output);
                    result = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                return false;
            }
        }
        return result;
    }

    /**
     * Method implements setting tag on note by sending request for setting tag on note
     *
     * @param token  token, what need for executing request
     * @param NoteID id of note, what need for edit
     * @param tags   list of tags, what need to unset
     */
    public boolean SetTags(String token, int NoteID, ArrayList<Tag> tags) {
        boolean result;
        Log.e("test", "Preparing");
        Request setTags = new Request(actv) {
            @Override
            public void onFail(String errorMsg) {
                actv.runOnUiThread(() ->
                {
                    String er = getResources().getString(R.string.msgRequestFailed) + "\n" + errorMsg;
                    aldw.msgInfo(actv, getResources().getString(R.string.txtError), er);
                });
            }

            @Override
            public void onSuccess(String res) {
                try {
                    Log.e("test", res.toString());//Call nullReferenceException for to check what required data is complete
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        result = false;
        for (int i = 0; i < tags.size(); i++) {
            try {
                setTags.isSuccessful = false;
                JSONObject obj = new JSONObject();
                obj.put("skey", token);
                obj.put("noteid", NoteID);
                obj.put("tagid", tags.get(i).id);
                setTags.send(actv, "POST", "/rpc/set_tag", obj.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                setTags.requestSender.join();
                Log.e("test", "Joined");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (setTags.isSuccessful) {
                try {
                    setTags.onSuccess(setTags.output);
                    result = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                return false;
            }
        }
        return result;
    }

    /**
     * Method implements saving changes for note by sending request for saving changes
     *
     * @param token token, what need for executing request
     * @param n     note, what need for save
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean SaveNote(String token, Note n) {
        boolean result;
        Log.e("test", "Preparing");
        Request saveNote = new Request(actv) {
            @Override
            public void onFail(String errorMsg) {
                actv.runOnUiThread(() ->
                {
                    String er = getResources().getString(R.string.msgRequestFailed) + "\n" + errorMsg;
                    aldw.msgInfo(actv, getResources().getString(R.string.txtError), er);
                });
            }

            @Override
            public void onSuccess(String res) {
                try {
                    Log.e("test", res.toString());//Call nullReferenceException for to check what required data is complete
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        result = false;
        try {
            saveNote.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("skey", token);
            obj.put("nid", n.id);
            obj.put("ntitle", n.name);
            obj.put("nbody", n.text);
            obj.put("ncolor", n.ColorToString(n.color));
            saveNote.send(actv, "POST", "/rpc/update_note", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            saveNote.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (saveNote.isSuccessful) {
            try {
                saveNote.onSuccess(saveNote.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            return false;
        }
        return result;
    }

    /**
     * Method implements deleting note from server by sending request for deleting note
     *
     * @param token token, what need for executing request
     * @param ID    id of note, what need for delete
     */
    public boolean DeleteNote(String token, int ID) {
        boolean result;
        Log.e("test", "Preparing");
        Request deleteNote = new Request(actv) {
            @Override
            public void onFail(String errorMsg) {
                actv.runOnUiThread(() ->
                {
                    String er = getResources().getString(R.string.msgRequestFailed) + "\n" + errorMsg;
                    aldw.msgInfo(actv, getResources().getString(R.string.txtError), er);
                });
            }

            @Override
            public void onSuccess(String res) {
                try {
                    Log.e("test", res.toString());//Call nullReferenceException for to check what required data is complete
//                    for (Session s:StaticData.Sessions) {
//                        if(token.equals(s.token)){
//                            StaticData.Sessions.remove(s);
//                        }else{
//                            continue;
//                        }
//                    }
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        result = false;
        try {
            deleteNote.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("skey", token);
            obj.put("nid", ID);
            deleteNote.send(actv, "POST", "/rpc/delete_note", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            deleteNote.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (deleteNote.isSuccessful) {
            try {
                deleteNote.onSuccess(deleteNote.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

        }
        return result;
    }
}
