package ru.example.notesapi.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.example.notesapi.R;
import ru.example.notesapi.model.Note;

public class NotesArrayAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    public ArrayList<Note> objects;

    public NotesArrayAdapter(Context context, ArrayList<Note> notes) {
        ctx = context;
        objects = notes;
        inflater = (LayoutInflater) ctx.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    //amount of items
    @Override
    public int getCount() {
        return objects.size();
    }

    //item by position
    @Override
    public Object getItem(int pos) {
        return objects.get(pos);
    }

    //note by position
    Note getNote(int pos) {
        return ((Note) getItem(pos));
    }

    //id by position
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    //point in the list
    @SuppressLint("NewApi")
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        //use made, but useless Views
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.note_list_item, parent, false);
        }
        Note n = getNote(pos);
        //filling items point in view by data form notes
        ImageView imgv = view.findViewById(R.id.imgvColorNoteListItem);
        TextView tvName = view.findViewById(R.id.tvNameNoteListitem);
        //TextView tvText = view.findViewById(R.id.tvTextNoteListItem);
        imgv.setBackgroundColor(n.ColorToInt(n.color));
        if (n.name.equals("")) {
            tvName.setText(String.valueOf(n.id) + " New empty note");
        } else {
            tvName.setText(n.toString());
        }

        //tvText.setText(n.text);
        return view;
    }
}
