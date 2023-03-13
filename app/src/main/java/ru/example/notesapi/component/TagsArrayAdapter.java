package ru.example.notesapi.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.example.notesapi.R;
import ru.example.notesapi.model.Tag;

public class TagsArrayAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    public ArrayList<Tag> objects;

    public TagsArrayAdapter(Context context, ArrayList<Tag> tags) {
        ctx = context;
        objects = tags;
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
    Tag getTag(int pos) {
        return ((Tag) getItem(pos));
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
            view = inflater.inflate(R.layout.tag_list_item, parent, false);
        }
        Tag n = getTag(pos);
        //filling items point in view by data form notes
        CheckBox chbxTag = view.findViewById(R.id.chbxTagTagListItem);
        // присваиваем чекбоксу обработчик
        chbxTag.setOnCheckedChangeListener(myCheckChangeList);
        chbxTag.setTag(pos);
        // заполняем данными из товаров: в корзине или нет
        chbxTag.setChecked(n.inChoice);
        if (n.inChoice) {
            chbxTag.setChecked(true);
        } else {
            chbxTag.setChecked(false);
        }
        chbxTag.setText(n.toString());
        //tvText.setText(n.text);
        return view;
    }

    // содержимое корзины
    public ArrayList<Tag> getBox() {
        ArrayList<Tag> box = new ArrayList<Tag>();
        for (Tag t : objects) {
            // если в корзине
            if (t.inChoice)
                box.add(t);
        }
        return box;
    }

    // обработчик для чекбоксов
    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // меняем данные товара (в корзине или нет)
            getTag((Integer) buttonView.getTag()).inChoice = isChecked;
        }
    };
}
