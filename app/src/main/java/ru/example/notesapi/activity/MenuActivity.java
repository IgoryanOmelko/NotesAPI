package ru.example.notesapi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import ru.example.notesapi.R;
import ru.example.notesapi.helper.Request;
import ru.example.notesapi.helper.StaticData;
import ru.example.notesapi.model.User;

public class MenuActivity extends AppCompatActivity {
    TextView tvUserName;
    TextView tvCurrentToken;
    Context ctx;
    Activity actv;
    ALDWindow aldw = new ALDWindow();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        tvUserName = findViewById(R.id.tvLoginTextMenu);
        tvCurrentToken = findViewById(R.id.tvSTTextMenu);
        tvUserName.setText(User.getName());
        tvCurrentToken.setText(User.getToken());
        ctx = this;
        actv = this;
    }

    /**
     * Actions by click on back system UI button
     */
    @Override
    public void onBackPressed() {
        return;
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
        if (data == null) {

        } else {

        }
        ctx = this;
        actv = this;
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Actions by click on logout button
     *
     * @param v
     */
    public void onBtnLogoutClick(View v) {
        if (User.getToken().equals("null")) {
            User.setToken(null);
            StaticData.db.SetSettingsToken(User.getToken());
            setResult(RESULT_CANCELED, null);
            finish();
        } else {
            if (CloseSession(User.getToken())) {
                User.setToken(null);
                StaticData.db.SetSettingsToken(User.getToken());
                setResult(RESULT_OK, null);
                finish();
            } else {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        }
    }

    /**
     * Actions by click on sessions button
     *
     * @param v
     */
    public void onBtnNotesClick(View v) {
        Intent i = new Intent(ctx, NoteListActivity.class);
        startActivityForResult(i, StaticData.MenuToNotesListCode.getCode());
    }

    /**
     * Actions by click on graphs button
     *
     * @param v
     */
    public void onBtnTagsClick(View v) {
        Intent i = new Intent(ctx, TagListActivity.class);
        startActivityForResult(i, StaticData.MenuToTagsListCode.getCode());
    }

    private void Initialize() {
        tvUserName.setText("");
        tvCurrentToken.setText("");
    }

    /**
     * Method implements deleting token from server by sending request for closing session
     *
     * @param token token, what need for executing request
     */
    public boolean CloseSession(String token) {
        boolean result;
        Log.e("test", "Preparing");
        Request closeSession = new Request(actv) {
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
                    User.setToken(null);
                    User.setSettings();
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        result = false;
        try {
            closeSession.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("skey", token);
            closeSession.send(actv, "POST", "/rpc/close_session", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            closeSession.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (closeSession.isSuccessful) {
            try {
                closeSession.onSuccess(closeSession.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

        }
        return result;
    }
}
