package ru.example.notesapi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import ru.example.notesapi.R;
import ru.example.notesapi.database.DB;
import ru.example.notesapi.helper.Request;
import ru.example.notesapi.helper.StaticData;
import ru.example.notesapi.model.NoteColor;
import ru.example.notesapi.model.User;

public class SignInActivity extends AppCompatActivity {
    ALDWindow aldw = new ALDWindow();
    EditText etLogin;
    EditText etPassword;
    Switch swtRemPas;
    String userName;
    String userSecret;
    String tempToken = "";
    String tempCSTR;
    Activity actv;
    Context ctx;
    boolean validToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        etLogin = findViewById(R.id.etLoginSignIn);
        etPassword = findViewById(R.id.etPasswordSignIn);
        swtRemPas = findViewById(R.id.swtRememberSignIn);
        actv = this;
        ctx = this;
        StaticData.SetCodes();
        StaticData.initColor();
        StaticData.colorList = new ArrayList<NoteColor>();
        StaticData.colorList.add(StaticData.SetColor(StaticData.black[0], StaticData.black[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.silver[0], StaticData.silver[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.gray[0], StaticData.gray[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.white[0], StaticData.white[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.maroon[0], StaticData.maroon[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.red[0], StaticData.red[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.purple[0], StaticData.purple[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.fuchsia[0], StaticData.fuchsia[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.green[0], StaticData.green[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.lime[0], StaticData.lime[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.olive[0], StaticData.olive[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.yellow[0], StaticData.yellow[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.navy[0], StaticData.navy[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.blue[0], StaticData.blue[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.teal[0], StaticData.teal[1]));
        StaticData.colorList.add(StaticData.SetColor(StaticData.aqua[0], StaticData.aqua[1]));
        StaticData.db = new DB(ctx, "NoteAPI.db", null, 1);
        if (StaticData.db.GetSettingsID() < 0) {
            StaticData.db.SetSettings(User.getCSTR());
            User.setSettings();
            return;
        } else {
            User.getSettings();
            if ((User.getName().length() == 0 || User.getName().equals("null")) || (User.getPassword().length() == 0 || User.getPassword().equals("null"))) {
                //ALDWindow.msgInfo(ctx,getResources().getString(R.string.txtInfo),"Пользователь не найден");
                swtRemPas.setChecked(false);
                return;
            } else {
                etLogin.setText(User.getName());
                etPassword.setText(User.getPassword());
                swtRemPas.setChecked(true);
                if (User.getToken().length() > 0 && !User.getToken().equals("null")) {
                    tempToken = User.getToken();
                    userName = User.getName();
                    userSecret = User.getPassword();
                    Login();
                } else {
                    aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgInvalidToken));
                }
            }
        }
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
        validToken = false;
        User.getSettings();
        if ((User.getName().length() == 0 || User.getName().equals("null")) || (User.getPassword().length() == 0 || User.getPassword().equals("null"))) {
            //ALDWindow.msgInfo(ctx,getResources().getString(R.string.txtInfo),"Пользователь не найден");
            swtRemPas.setChecked(false);
            etLogin.setText(null);
            etPassword.setText(null);
            return;
        } else {
            etLogin.setText(User.getName());
            etPassword.setText(User.getPassword());
            if (User.getToken().equals("null")) {
                swtRemPas.setChecked(false);
            } else {
                swtRemPas.setChecked(true);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Actions by click on login button
     *
     * @param v
     */
    public void onBtnLoginClick(View v) {
        userName = etLogin.getText().toString();
        userSecret = etPassword.getText().toString();
        if ((userName.length() > 30 || userName.length() < 3) || (userSecret.length() > 30) || userSecret.length() < 3) {
            aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgIncorrectLoginPassword));
            return;
        } else {
            OpenSession(userName, userSecret);
            if (validToken) {
                //ListSession(tempToken,StaticData.Sessions);
                Login();
            } else {
                aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgInvalidToken));
                return;
            }
        }
    }

    /**
     * Actions by click on register button
     *
     * @param v
     */
    public void onBtnRegisterClick(View v) {
        Intent i = new Intent(ctx, SignUpActivity.class);
        startActivityForResult(i, StaticData.LoginToRegisterCode.getCode());
    }

    /**
     * Actions by click on set setting button
     *
     * @param v
     */
    public void onBtnSettingClick(View v) {
        AlertDialog.Builder window = new AlertDialog.Builder(ctx);
        window.setTitle(getResources().getString(R.string.txtCSTR));
        window.setMessage(getResources().getString(R.string.txtValue));
        final EditText etInput = new EditText(ctx);
        etInput.setText(User.getCSTR());
        window.setView(etInput);
        window.setPositiveButton(getResources().getString(R.string.txtApply), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                User.setCSTR(etInput.getText().toString());
                StaticData.db.SetSettingsCSTR(User.getCSTR());
            }
        });
        window.setNegativeButton(getResources().getString(R.string.txtCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });
        window.show();
    }

    /**
     * Method execute authorization
     */
    public void Login() {
        User.setName(userName);
        User.setPassword(userSecret);
        User.setToken(tempToken);
        if (swtRemPas.isChecked()) {
            User.setSettings();
        } else {
        }
        Intent i = new Intent(ctx, MenuActivity.class);
        startActivityForResult(i, StaticData.LoginToMenuCode.getCode());
    }

    /**
     * Method implements getting token from server by sending request for opening session
     *
     * @param userName   login of account
     * @param userSecret password of account
     */
    public void OpenSession(String userName, String userSecret) {
        Log.e("test", "Preparing");
        Request openSession = new Request(actv) {
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

                    if (res.equals("null")) {
                        res = null;
                        Log.e("test", res.toString());//Call nullReferenceException for to check what required data is complete
                    }
                    res = res.substring(1, res.length() - 1);
                    validToken = true;
                    //JSONObject obj = new JSONObject(res);
                    tempToken = res;//getting token
                    Log.e("test", "Success");
                } catch (Exception ex) {
                    validToken = false;
                    ex.printStackTrace();
                }
            }
        };
        try {
            openSession.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("usr", userName);
            obj.put("pass", userSecret);
            openSession.send(actv, "POST", "/rpc/open_session", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        try {
            openSession.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (openSession.isSuccessful) {
            try {
                openSession.onSuccess(openSession.output);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            validToken = false;
            return;
        }
    }
}
