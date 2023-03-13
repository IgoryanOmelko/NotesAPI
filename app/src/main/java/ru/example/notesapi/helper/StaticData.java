package ru.example.notesapi.helper;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import ru.example.notesapi.database.DB;
import ru.example.notesapi.model.Note;
import ru.example.notesapi.model.NoteColor;
import ru.example.notesapi.model.Tag;
public final class StaticData {
    public static DB db;
    public static RequestCode LoginToMenuCode = new RequestCode();
    public static RequestCode RegisterToMenuCode = new RequestCode();
    public static RequestCode LoginToRegisterCode = new RequestCode();
    public static RequestCode MenuToNotesListCode = new RequestCode();
    public static RequestCode MenuToTagsListCode = new RequestCode();
    public static RequestCode NotesListToNoteCode = new RequestCode();
    public static ArrayList<Note> Notes;
    public static ArrayList<Tag> Tags;
    public static Note currentNote;
    public static String[] black = new String[2];
    public static String[] silver =new String[2];
    public static String[] gray =new String[2];
    public static String[] white =new String[2];
    public static String[] maroon =new String[2];
    public static String[] red =new String[2];
    public static String[] purple =new String[2];
    public static String[] fuchsia =new String[2];
    public static String[] green =new String[2];
    public static String[] lime =new String[2];
    public static String[] olive =new String[2];
    public static String[] yellow =new String[2];
    public static String[] navy =new String[2];
    public static String[] blue =new String[2];
    public static String[] teal =new String[2];
    public static String[] aqua =new String[2];
    public static Tag getTagByID(int ID){
        for (Tag tempt:Tags) {
            if(tempt.id==ID){
                return tempt;
            }else{
                continue;
            }
        }
        Tag t=new Tag();
        t.id=-1;
        t.name = "No tags";
        return t;
    }
    public static ArrayList<NoteColor> colorList;
    public static void SetCodes() {
        StaticData.LoginToMenuCode.setCode(001);
        StaticData.RegisterToMenuCode.setCode(021);
        StaticData.LoginToRegisterCode.setCode(002);
        StaticData.MenuToNotesListCode.setCode(013);
        StaticData.MenuToTagsListCode.setCode(014);
        StaticData.NotesListToNoteCode.setCode(045);
    }
    public static NoteColor SetColor(String name, String color){
        NoteColor nc= new NoteColor();
        nc.name=name;
        nc.colorS=color;
        return nc;
    }
    public static void initColor(){
        black = new String[]{"black","#000000"};
        silver =new String[]{"silver","#C0C0C0"};
        gray =new String[]{"gray","#808080"};
        white =new String[]{"white","#FFFFFF"};
        maroon =new String[]{"maroon","#800000"};
        red =new String[]{"red","#FF0000"};
        purple =new String[]{"purple","#800080"};
        fuchsia =new String[]{"fuchsia","#FF00FF"};
        green =new String[]{"green","#008000"};
        lime =new String[]{"lime","#00FF00"};
        olive =new String[]{"olive","#808000"};
        yellow =new String[]{"yellow","#FFFF00"};
        navy =new String[]{"navy","#000080"};
        blue =new String[]{"blue","#0000FF"};
        teal =new String[]{"teal","#008080"};
        aqua =new String[]{"aqua","#00FFFF"};
    }
}

