package ru.example.notesapi.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;


import ru.example.notesapi.R;

public class ALDWindow {
    public void onPosClick() {

    }

    public void onNegClick() {

    }

    public void msgInfo(Context ctx, String Title, String message) {
        LayoutInflater myLayout = LayoutInflater.from(ctx);
        View dialogView = myLayout.inflate(R.layout.msg_window, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(dialogView);
        AlertDialog ald = builder.create();
        ald.show();
        Button btnPos = ald.findViewById(R.id.btnPosMsgWindow);
        TextView tvTitle = ald.findViewById(R.id.tvTitleMsgWindow);
        TextView tvMsg = ald.findViewById(R.id.tvMsgMsgWindow);
        tvTitle.setText(Title);
        tvMsg.setText(message);
        btnPos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ald.dismiss();
            }
        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                ald.cancel();
//            }
//        });

    }

    public void msgWarn(Context ctx, String Title, String message) {
        LayoutInflater myLayout = LayoutInflater.from(ctx);
        View dialogView = myLayout.inflate(R.layout.ald_window, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(dialogView);
        AlertDialog ald = builder.create();
        ald.show();
        Button btnPos = ald.findViewById(R.id.btnPosAldWindow);
        Button btnNeg = ald.findViewById(R.id.btnNegAldWindow);
        TextView tvTitle = ald.findViewById(R.id.tvTitleAldWindow);
        TextView tvMsg = ald.findViewById(R.id.tvMsgAldWindow);
        tvTitle.setText(Title);
        tvMsg.setText(message);

        btnPos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPosClick();
                ald.dismiss();
            }
        });
        btnNeg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onNegClick();
                ald.cancel();
            }
        });
        //ald.show();
    }
}
