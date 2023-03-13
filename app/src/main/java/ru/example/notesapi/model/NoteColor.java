package ru.example.notesapi.model;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class NoteColor {
    public String name;
    public String colorS;
    @Override
    public String toString(){
        return name;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Color StringToColor(){
        int color = Color.parseColor(colorS);
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
    public int ColorToInt(Color c){
        int red = (int)c.red();
        int green = (int)c.green();
        int blue = (int)c.blue();
        return Color.rgb(red,green,blue);
    }
}
