package ru.example.notesapi.model;


import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class Note {
    public int id;
    public String name;
    public String text;
    public Color color = new Color();
    public ArrayList<Tag> tags;
    public Note(int id, String name, String color){
        this.id=id;
        this.name=name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.color=StringToColor(color);
        }
    }
    public Note(){

    }
    @Override
    public String toString(){
        return id + " " + name;
    }
    public int getID() {
        return id;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Color StringToColor(String s){
        int color = Color.parseColor(s);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
//        int resultRed = Integer.valueOf(s.substring(0, 2), 16);//convert Hex value (fe F) to decimal value (15) or 11(16)=17(10)
//        int resultGreen = Integer.valueOf(s.substring(2, 4), 16);
//        int resultBlue = Integer.valueOf(s.substring(4, 6), 16);
        //Color c = Color.rgb(11,11,11);
        return Color.valueOf(red*1.0f,green*1.0f,blue*1.0f);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String ColorToString(Color c){
        //int color = Color.parseColor(c.toString());
        int red = (int)c.red();
        int green = (int)c.green();
        int blue = (int)c.blue();
        String sr=Integer.toHexString(red);
        String sg=Integer.toHexString(green);
        String sb=Integer.toHexString(blue);
        if(sr.length()<2){
            sr="0"+sr;
        }
        if(sg.length()<2){
            sg="0"+sg;
        }
        if(sb.length()<2){
            sb="0"+sb;
        }
        String result = "#"+sr+sg+sb;
        return result;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int ColorToInt(Color c){
        int red = (int)c.red();
        int green = (int)c.green();
        int blue = (int)c.blue();
        return Color.rgb(red,green,blue);
    }
    public boolean hasTag(Tag tag){
        if(tag.id<0){
            return true;
        }else{
            for (Tag t:tags) {
                if(t.id==tag.id&&t.name.equals(tag.name)){
                    return true;
                }else{
                    continue;
                }
            }
            return false;
        }
    }
}
