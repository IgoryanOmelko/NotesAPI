package ru.example.notesapi.helper;


import android.app.Activity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import ru.example.notesapi.model.User;

public class Request {
    public static String base = User.getCSTR();
    public Thread requestSender;
    public String output;
    public Activity actv;
    public boolean isSuccessful;

    //public static String token;
    public Request(Activity a) {
        Log.e("test", "Creating request");
        this.actv = a;
    }

    /**
     * Method execute actions when request succeed
     * For using method, it need to override and add in it own actions
     *
     * @param result result of request execution
     */
    public void onSuccess(String result) {

    }

    public int[] onSuccess2(String result) {
        int[] n = new int[0];
        return n;
    }

    /**
     * Method executes action when request failed
     * For using method, it need to override and add in it own actions
     */
    public void onFail(String errorMsg) {
    }

    /**
     * Method execute send request and get data from server
     *
     * @param actv   activity, where neew out fail message
     * @param method type of request method (PGPD)
     * @param body   JSON object as string width parameters
     */
    public void send(Activity actv, String method, String request, String body) {
        Runnable requestTask = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(base + request);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("accept", "application/json");
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    BufferedOutputStream bos = new BufferedOutputStream(con.getOutputStream());
                    bos.write(body.getBytes(StandardCharsets.UTF_8));
                    bos.flush();
                    con.setRequestMethod(method);
                    InputStream is = con.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[512];
                    String str = "";
                    Log.e("test", "Try out to send");
                    isSuccessful = true;
                    while (true) {
                        int length = bis.read(buffer);
                        if (length < 0) break;
                        str += new String(buffer, 0, length);
                        Log.e("test", "Getting data");
                    }

                    con.disconnect();
                    final String res = str;
                    output = res;
                    isSuccessful = true;
//                    ctx.runOnUiThread(() -> {
//                        try{onSuccess(res);} catch (Exception e){}
//                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFail(ex.toString());

                    isSuccessful = false;
                    Log.e("test", "Fail");
                    //onFail();
                    //requestSender.interrupt();
                    //requestSender=null;
                    output = null;
                }
            }
        };
        requestSender = new Thread(requestTask);
        requestSender.start();
    }
}