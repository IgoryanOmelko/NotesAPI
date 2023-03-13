package ru.example.notesapi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

import ru.example.notesapi.R;
import ru.example.notesapi.component.NotesArrayAdapter;
import ru.example.notesapi.helper.Request;
import ru.example.notesapi.helper.StaticData;
import ru.example.notesapi.model.Note;
import ru.example.notesapi.model.Tag;
import ru.example.notesapi.model.User;

public class TagListActivity extends AppCompatActivity {
    ListView lvTags;
    TextView tvSelectedTag;
    TextView tvSTT;
    TextView tvIDNotes;
    ArrayAdapter<Tag> adpTag;
    Context ctx;
    Activity actv;
    ALDWindow aldw = new ALDWindow();
    Tag selectedTag;
    int selectedID;
    ArrayList<Note> displayNoteList;
    boolean selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
        ctx = this;
        actv = this;
        lvTags = findViewById(R.id.lvTags);
        tvSelectedTag = findViewById(R.id.tvSelectedTagTagList);
        tvSTT = findViewById(R.id.tvSTTTagList);
        StaticData.Tags = new ArrayList<Tag>();
        adpTag = new ArrayAdapter<Tag>(ctx, android.R.layout.simple_expandable_list_item_1, StaticData.Tags);
        adpTag.notifyDataSetChanged();
        UpdateTagList();
        lvTags.setOnItemClickListener((parent, view, position, id) -> {
            selectedTag = adpTag.getItem(position);
            selectedID = selectedTag.id;
            selected = true;
            tvSelectedTag.setText(selectedTag.name);
            Log.e("Test", selectedTag.toString());
        });
        lvTags.setAdapter(adpTag);
        lvTags.invalidate();
        adpTag.notifyDataSetChanged();
        tvSelectedTag.setText("");
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
        getMenuInflater().inflate(R.menu.menu_edit_up, menu);
        return true;
    }

    public void onBtnBackClick(View v) {
        Intent i = new Intent(ctx, MenuActivity.class);

        setResult(RESULT_OK, i);
        finish();
        //super.onBackPressed();
    }

    /**
     * Method read type of menu button and executes needed action
     *
     * @param item The menu item that was selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();//id item in menu list
        switch (id) {
            case R.id.itmNew://Create new tag
                AlertDialog.Builder window = new AlertDialog.Builder(ctx);
                window.setTitle(getResources().getString(R.string.txtNewPassword));
                window.setMessage(getResources().getString(R.string.txtValue));
                final EditText etInput = new EditText(ctx);
                window.setView(etInput);
                window.setPositiveButton(getResources().getString(R.string.txtApply), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (etInput.getText().toString().length() >= 2 && etInput.getText().toString().length() <= 30) {
                            if (NewTag(User.getToken(), etInput.getText().toString())) {
                                UpdateTagList();
                                for (Tag t : StaticData.Tags) {
                                    if (selectedID == t.getID()) {
                                        selectedTag = t;
                                        selected = true;
                                    } else {
                                        continue;
                                    }
                                }
                            } else {
                                return;
                            }
                        } else {
                            aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgIncorrectName));
                        }
                    }
                });
                window.setNegativeButton(getResources().getString(R.string.txtCancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }
                });
                window.show();
                return true;

            case R.id.itmDelete://delete the tag
                aldw = new ALDWindow() {
                    @Override
                    public void onPosClick() {
                        if (selected) {
                            if (DeleteTag(User.getToken(), selectedID)) {
                                UpdateTagList();
                                selected = false;
                                tvSelectedTag.setText("");
                            } else {
                                aldw.msgInfo(ctx, getResources().getString(R.string.txtError), getResources().getString(R.string.msgUnexpectedError));
                                return;
                            }
                        } else {
                            aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgNothingSelected));
                        }
                    }
                };
                aldw.msgWarn(ctx, getResources().getString(R.string.txtWarning), getResources().getString(R.string.msgSure));
                return true;
            case R.id.itmEdit://edit the tag
                if (selected) {
                    AlertDialog.Builder window2 = new AlertDialog.Builder(ctx);
                    window2.setTitle(getResources().getString(R.string.txtNewName));
                    window2.setMessage(getResources().getString(R.string.txtValue));
                    final EditText etInput2 = new EditText(ctx);
                    etInput2.setText(selectedTag.name);
                    window2.setView(etInput2);
                    window2.setPositiveButton(getResources().getString(R.string.txtApply), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (etInput2.getText().toString().length() >= 2 && etInput2.getText().toString().length() <= 30) {
                                if (ChangeTag(User.getToken(), etInput2.getText().toString(), selectedID)) {
                                    UpdateTagList();
                                    selected = false;
                                    tvSelectedTag.setText("");
                                } else {
                                    selected = false;
                                    return;
                                }
                            } else {
                                aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgIncorrectName));
                                selected = false;

                            }
                        }
                    });
                    window2.setNegativeButton(getResources().getString(R.string.txtCancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    });
                    window2.show();
                    tvSelectedTag.setText("");
                } else {
                    aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgNothingSelected));
                    return false;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method executes update content of tag list
     */
    public void UpdateTagList() {
        StaticData.Tags.clear();
        ListTag(User.getToken(), StaticData.Tags);
        StaticData.Tags.sort(Comparator.comparing(Tag::getID));
        adpTag.notifyDataSetChanged();
    }

    public boolean DeleteTag(String token, int ID) {
        boolean result;
        Log.e("test", "Preparing");
        Request deleteTag = new Request(actv) {
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
            deleteTag.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("skey", token);
            obj.put("nid", ID);
            deleteTag.send(actv, "POST", "/rpc/delete_tag", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            deleteTag.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (deleteTag.isSuccessful) {
            try {
                deleteTag.onSuccess(deleteTag.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

        }
        return result;
    }

    /**
     * Method implements editing tag from server by sending request for editing tag
     *
     * @param token token, what need for executing request
     * @param ID    id of note, what need for edit
     * @param name  name of note, what need for edit
     */
    public boolean ChangeTag(String token, String name, int ID) {
        boolean result;
        Log.e("test", "Preparing");
        Request changeTag = new Request(actv) {
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
            changeTag.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("skey", token);
            obj.put("nid", ID);
            obj.put("nname", name);
            changeTag.send(actv, "POST", "/rpc/update_tag", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            changeTag.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (changeTag.isSuccessful) {
            try {
                changeTag.onSuccess(changeTag.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

        }
        return result;
    }

    /**
     * Method implements editing tag from server by sending request for editing tag
     *
     * @param token token, what need for executing request
     * @param name  name of note, what need for edit
     */
    public boolean NewTag(String token, String name) {
        boolean result;
        Log.e("test", "Preparing");
        Request newTag = new Request(actv) {
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
            newTag.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("skey", token);
            obj.put("nname", name);
            newTag.send(actv, "POST", "/rpc/add_tag", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            newTag.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (newTag.isSuccessful) {
            try {
                newTag.onSuccess(newTag.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

        }
        return result;
    }

    /**
     * Method implements getting list of tags from server by sending request for getting tag list
     *
     * @param token   token, what need for executing request
     * @param tagList list of tags what need to filling
     */
    public void ListTag(String token, ArrayList<Tag> tagList) {
        Log.e("test", "Preparing");
        Request listTag = new Request(actv) {
            @Override
            public void onFail(String errorMsg) {
                actv.runOnUiThread(() ->
                {
                    String message = getResources().getString(R.string.msgRequestFailed) + "\n" + errorMsg;
                    aldw.msgInfo(actv, getResources().getString(R.string.txtError), message);
                });
            }

            @Override
            public void onSuccess(String res) {
                try {
                    Log.e("test", res.toString());//Call nullReferenceException for to check what required data is complete
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Tag t = new Tag();
                        t.id = jsonObject.getInt("nid");
                        t.name = jsonObject.getString("nname");
                        //n.text=jsonObject.getString("nbody");
                        tagList.add(t);
                    }
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        try {
            listTag.isSuccessful = false;
            Log.e("Test", User.getToken());
            JSONObject obj = new JSONObject();
            obj.put("skey", User.getToken());
            listTag.send(actv, "POST", "/rpc/list_tags", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        try {
            listTag.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (listTag.isSuccessful) {

            try {
                listTag.onSuccess(listTag.output);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            return;
        }
    }
}
