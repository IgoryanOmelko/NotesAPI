package ru.example.notesapi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.SplittableRandom;

import ru.example.notesapi.R;
import ru.example.notesapi.component.NotesArrayAdapter;
import ru.example.notesapi.helper.Request;
import ru.example.notesapi.helper.StaticData;
import ru.example.notesapi.model.Note;
import ru.example.notesapi.model.NoteColor;
import ru.example.notesapi.model.Tag;
import ru.example.notesapi.model.User;

public class NoteListActivity extends AppCompatActivity {
    ListView lvNotes;
    TextView tvSelectedNote;
    TextView tvSNT;
    TextView tvIDNotes;
    NotesArrayAdapter adpNote;
    ArrayAdapter<Tag> adpTag;
    Context ctx;
    Activity actv;
    ALDWindow aldw = new ALDWindow();
    Note selectedNote;
    Tag selectedTag;
    String newName;
    Spinner spnTags;
    int selectedID;
    ArrayList<Note> displayNoteList;

    boolean selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ctx = this;
        actv = this;
        StaticData.currentNote = new Note();
        lvNotes = findViewById(R.id.lvNotes);
        spnTags = findViewById(R.id.spnTagsNoteList);
        tvSelectedNote = findViewById(R.id.tvSelectedNoteNoteList);
        tvSNT = findViewById(R.id.tvSNTNoteList);
        StaticData.Tags = new ArrayList<Tag>();
        StaticData.Notes = new ArrayList<Note>();
        displayNoteList = new ArrayList<Note>();
        adpTag = new ArrayAdapter<Tag>(ctx, android.R.layout.simple_expandable_list_item_1, StaticData.Tags);
        adpNote = new NotesArrayAdapter(ctx, displayNoteList);
        //lvNotes.setAdapter(adpNote);
        //adpNote.notifyDataSetChanged();
        spnTags.setAdapter(adpTag);
        adpTag.notifyDataSetChanged();
        UpdateTagList();
        selectedTag = StaticData.getTagByID(-1);
        UpdateNoteList();
        displayNoteList = TagFilter(StaticData.Notes, selectedTag);
        lvNotes.setOnItemClickListener((parent, view, position, id) -> {
            selectedNote = (Note) adpNote.getItem(position);
            selectedID = selectedNote.id;
            selected = true;
            tvSelectedNote.setText(selectedNote.toString());
            Log.e("Test", selectedNote.toString());
        });
        spnTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setTagFilter(position);
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
        lvNotes.setAdapter(adpNote);
        lvNotes.invalidate();
        adpNote.notifyDataSetChanged();
    }

    void setTagFilter(int position) {
        selectedTag = adpTag.getItem(position);
        Log.e("Test", selectedTag.toString());
        displayNoteList = TagFilter(StaticData.Notes, selectedTag);
        adpNote.objects = displayNoteList;
        Log.e("Test", String.valueOf(displayNoteList.size()));
        adpNote.notifyDataSetChanged();
        tvSelectedNote.setText("");
    }

    /**
     * Method executes actions by call this activity when calling this activity from another activity
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        UpdateTagList();
        UpdateNoteList();
        setTagFilter(0);
        if (requestCode == StaticData.NotesListToNoteCode.getCode() && resultCode == RESULT_OK) {


        }
        super.onActivityResult(requestCode, resultCode, data);
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
            case R.id.itmNew://Create new note
                StaticData.currentNote.name = getResources().getString(R.string.txtNewNameNote);
                StaticData.currentNote.text = "";
                StaticData.currentNote.tags = new ArrayList<Tag>();
                NoteColor nc = StaticData.colorList.get(0);
                StaticData.currentNote.color = nc.StringToColor();
                if (NewNote(User.getToken(), StaticData.currentNote)) {
                    UpdateNoteList();
                    setTagFilter(0);
                    Intent i = new Intent(ctx, NoteActivity.class);
                    startActivityForResult(i, StaticData.NotesListToNoteCode.getCode());
                } else {
                    return false;
                }

                return true;

            case R.id.itmDelete://delete the note
                if (selected) {
                    aldw = new ALDWindow() {
                        @Override
                        public void onPosClick() {
                            if (DeleteNote(User.getToken(), selectedID)) {
                                UpdateNoteList();
                                setTagFilter(0);
                            } else {
                                return;
                            }
                        }
                    };
                    aldw.msgWarn(ctx, getResources().getString(R.string.txtWarning), getResources().getString(R.string.msgSure));

                } else {
                    aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgNothingSelected));
                    return false;
                }
                return true;
            case R.id.itmEdit://delete the note
                if (selected) {
                    StaticData.currentNote = new Note(selectedNote.id, selectedNote.name, selectedNote.ColorToString(selectedNote.color));
                    StaticData.currentNote.tags = selectedNote.tags;
                    if (NoteText(User.getToken(), StaticData.currentNote)) {
                        String newText = StaticData.currentNote.text.replace("|~|", System.lineSeparator());
                        StaticData.currentNote.text = newText;

                        Intent i = new Intent(ctx, NoteActivity.class);
                        startActivityForResult(i, StaticData.NotesListToNoteCode.getCode());
                    } else {
                        return false;
                    }

                } else {
                    aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgNothingSelected));
                    return false;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnBackClick(View v) {
        Intent i = new Intent(ctx, MenuActivity.class);

        setResult(RESULT_OK, i);
        finish();
        //super.onBackPressed();
    }


    /**
     * Method executes update content of Notes list
     */
    public void UpdateNoteList() {
        StaticData.Notes.clear();
        displayNoteList.clear();
        ListNote(User.getToken(), StaticData.Notes);
        SetTagsOnNotes(User.getToken(), StaticData.Notes);
        displayNoteList = StaticData.Notes;
        displayNoteList = TagFilter(StaticData.Notes, selectedTag);
        StaticData.Notes.sort(Comparator.comparing(Note::getID));
        displayNoteList.sort(Comparator.comparing(Note::getID));
        tvSelectedNote.setText("");
        adpNote.notifyDataSetChanged();
    }

    public void UpdateTagList() {
        StaticData.Tags.clear();
        Tag t = new Tag();
        t.id = -1;
        t.name = "No tags";
        StaticData.Tags.add(t);
        ListTag(User.getToken(), StaticData.Tags);
        StaticData.Tags.sort(Comparator.comparing(Tag::getID));
        //selectedTag = t;
        adpTag.notifyDataSetChanged();
    }

    public ArrayList<Note> TagFilter(ArrayList<Note> notes, Tag t) {
        ArrayList<Note> newNotes = new ArrayList<Note>();
        for (int i = 0; i < notes.size(); i++) {
            Note n = notes.get(i);
            if (n.hasTag(t)) {
                newNotes.add(n);
            } else {
                continue;
            }
        }
        notes = new ArrayList<Note>();
        notes = newNotes;
        return notes;
    }

    public void SetTagsOnNotes(String token, ArrayList<Note> notes) {
        for (Note n : notes) {
            int[] tags = new int[0];
            ArrayList<Tag> noteTags = new ArrayList<Tag>();
            tags = ListTagForNote(token, n.id);
            for (int i = 0; i < tags.length; i++) {
                noteTags.add(StaticData.getTagByID(tags[i]));
            }
            for (Tag t : noteTags) {
                t.inChoice = true;
            }
            n.tags = noteTags;
            Log.e("test", String.valueOf(n.tags.size()));
        }
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

    /**
     * Method implements adding new note on server by sending request for adding new note
     *
     * @param token token, what need for executing request
     * @param n     note, what need to set new ID
     */
    public boolean NewNote(String token, Note n) {
        boolean result;
        Log.e("test", "Preparing");
        Request newNote = new Request(actv) {
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
                    n.id = Integer.valueOf(res);
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        result = false;
        try {
            newNote.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("skey", token);
            newNote.send(actv, "POST", "/rpc/add_note", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            newNote.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (newNote.isSuccessful) {
            try {
                newNote.onSuccess(newNote.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

        }
        return result;
    }

    /**
     * Method implements getting list of notes from server by sending request for getting note list
     *
     * @param token    token, what need for executing request
     * @param noteList list of notes what need to filling
     */
    public void ListNote(String token, ArrayList<Note> noteList) {
        Log.e("test", "Preparing");
        Request listNote = new Request(actv) {
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
                        Note n = new Note(jsonObject.getInt("nid"), jsonObject.getString("ntitle"), jsonObject.getString("ncolor"));
                        //n.text=jsonObject.getString("nbody");
                        noteList.add(n);
                    }
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        try {
            listNote.isSuccessful = false;
            Log.e("Test", User.getToken());
            JSONObject obj = new JSONObject();
            obj.put("skey", User.getToken());
            listNote.send(actv, "POST", "/rpc/list_notes", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        try {
            listNote.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (listNote.isSuccessful) {

            try {
                listNote.onSuccess(listNote.output);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            return;
        }
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

    /**
     * Method implements
     * getting list of tags for note from server by sending request for getting tag list
     *
     * @param token token, what need for executing request
     * @param ID    id of note, what need to get tag list
     */
    public int[] ListTagForNote(String token, int ID) {
        Log.e("test", "Preparing");
        int[] tags = new int[0];
        Request listTagForNote = new Request(actv) {
            @Override
            public void onFail(String errorMsg) {
                actv.runOnUiThread(() ->
                {
                    String message = getResources().getString(R.string.msgRequestFailed) + "\n" + errorMsg;
                    aldw.msgInfo(actv, getResources().getString(R.string.txtError), message);
                });
            }

            @Override
            public int[] onSuccess2(String res) {
                try {
                    Log.e("test", res.toString());//Call nullReferenceException for to check what required data is complete

                    JSONArray jsonArray = new JSONArray(res);
                    int[] tagarray = new int[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        tagarray[i] = jsonObject.getInt("tid");
                    }
                    Log.e("test", "Success");
                    return tagarray;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        };
        try {
            listTagForNote.isSuccessful = false;
            Log.e("Test", User.getToken());
            JSONObject obj = new JSONObject();
            obj.put("skey", User.getToken());
            obj.put("nid", ID);
            listTagForNote.send(actv, "POST", "/rpc/get_note_tags", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return new int[0];
        }
        try {
            listTagForNote.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (listTagForNote.isSuccessful) {
            try {
                tags = listTagForNote.onSuccess2(listTagForNote.output);
                return tags;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            return new int[0];
        }
        return tags;
    }

    /**
     * Method implements getting note text from server by sending request for getting note text
     *
     * @param token token, what need for executing request
     * @param n     note what need to filling
     */
    public boolean NoteText(String token, Note n) {
        boolean result;
        Log.e("test", "Preparing");
        Request noteText = new Request(actv) {
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
                    res = res.substring(1, res.length() - 1);
                    n.text = res;
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        result = false;
        try {
            noteText.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("skey", token);
            obj.put("nid", n.id);
            noteText.send(actv, "POST", "/rpc/get_contents", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            noteText.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (noteText.isSuccessful) {
            try {
                noteText.onSuccess(noteText.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

        }
        return result;
    }
}
