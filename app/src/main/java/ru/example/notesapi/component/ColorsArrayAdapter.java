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
import ru.example.notesapi.model.NoteColor;


public class ColorsArrayAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    public ArrayList<NoteColor> objects;

    public ColorsArrayAdapter(Context context, ArrayList<NoteColor> noteColors) {
        ctx = context;
        objects = noteColors;
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
    NoteColor getNoteColor(int pos) {
        return ((NoteColor) getItem(pos));
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
            view = inflater.inflate(R.layout.color_list_item, parent, false);
        }
        NoteColor n = getNoteColor(pos);
        //filling items point in view by data form notes
        ImageView imgv = view.findViewById(R.id.imgvColorColorListItem);
        TextView tvName = view.findViewById(R.id.tvNameColorListItem);
        //TextView tvText = view.findViewById(R.id.tvTextNoteColorListItem);
        imgv.setBackgroundColor(n.ColorToInt(n.StringToColor()));
        tvName.setText(n.toString());
        //tvText.setText(n.text);
        return view;
    }
}
