package ru.example.notesapi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import ru.example.notesapi.R;
import ru.example.notesapi.helper.Request;
import ru.example.notesapi.helper.StaticData;
import ru.example.notesapi.model.User;

public class SignUpActivity extends AppCompatActivity {
    EditText etLogin;
    EditText etPassword;
    String userName;
    String userSecret;
    String tempToken;
    Context ctx;
    Activity actv;
    Switch swtRemPas;
    boolean validToken = false;
    ALDWindow aldw = new ALDWindow();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ctx = this;
        actv = this;
        etLogin = findViewById(R.id.etLoginSignUp);
        etPassword = findViewById(R.id.etPasswordSignUp);
        swtRemPas = findViewById(R.id.swtRememberSignUp);
        swtRemPas.setChecked(true);
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
        //validToken = false;
        ctx = this;
        actv = this;
        if (requestCode == StaticData.RegisterToMenuCode.getCode()) {
            setResult(RESULT_OK, null);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * Actions by click on ok button
     *
     * @param v
     */
    public void onBtnOkClick(View v) {
        userName = etLogin.getText().toString();
        userSecret = etPassword.getText().toString();
        if ((userName.length() > 30 || userName.length() < 3) || (userSecret.length() > 30) || userSecret.length() < 3) {
            aldw.msgInfo(ctx, getResources().getString(R.string.txtInfo), getResources().getString(R.string.msgIncorrectLoginPassword));
            return;
        } else {
            if (CreateAccount(userName, userSecret)) {
                OpenSession(userName, userSecret);
                if (validToken) {
                    //Authorization
                    if (swtRemPas.isChecked()) {
                        User.setName(userName);
                        User.setPassword(userSecret);
                        User.setToken(tempToken);
                    } else {
                        User.setName(null);
                        User.setPassword(null);
                        User.setToken(tempToken);
                    }
                    User.setSettings();
                    Intent i = new Intent(ctx, MenuActivity.class);
                    startActivityForResult(i, StaticData.RegisterToMenuCode.getCode());
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /**
     * Actions by click on cancel button
     *
     * @param v
     */
    public void onBtnCancelClick(View v) {
        Intent i = new Intent(ctx, SignInActivity.class);
        setResult(RESULT_OK, i);
        finish();
        //super.onBackPressed();
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
                    Log.e("test", "Success");
                    if (res.equals("null")) {
                        res = null;
                        Log.e("test", res.toString());//Call nullReferenceException for to check what required data is complete
                    }
                    validToken = true;
                    //JSONObject obj = new JSONObject(res);
                    tempToken = res;//getting token
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

    /**
     * Method implements creating account by sending request for creating account
     *
     * @param userName   login of account
     * @param userSecret password of account
     */
    public boolean CreateAccount(String userName, String userSecret) {
        boolean result;
        Log.e("test", "Preparing");
        Request createAccount = new Request(actv) {
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
        try {
            createAccount.isSuccessful = false;
            JSONObject obj = new JSONObject();
            obj.put("usr", userName);
            obj.put("pass", userSecret);
            createAccount.send(actv, "POST", "/rpc/reg_account", obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            result = false;
        }
        try {
            createAccount.requestSender.join();
            Log.e("test", "Joined");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (createAccount.isSuccessful) {
            try {
                createAccount.onSuccess(createAccount.output);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }
}
