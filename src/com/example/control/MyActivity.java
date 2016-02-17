package com.example.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MyActivity extends Activity {
    private Button btUserList = null;
    private Button btSearchList = null;
    private Button btUpload = null;
    private TextView stateText = null;
    private EditText etSearchServer = null;
    private EditText etVersion = null;
    private EditText etApkUrl = null;
    private Runnable postRun = null;
    private final String SHARED_APP = "app";
    SharedPreferences mShared = null;
    private Handler mHandler;
    private String data;
    Context mContext;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btUserList = (Button)findViewById(R.id.btUserList);
        btSearchList = (Button)findViewById(R.id.btSearchList);
        btUpload = (Button)findViewById(R.id.btUpload);
        stateText = (TextView)findViewById(R.id.stateText);
        etSearchServer = (EditText)findViewById(R.id.searchServer);
        etVersion = (EditText)findViewById(R.id.versionCode);
        etApkUrl = (EditText)findViewById(R.id.apkUrl);
        mShared = getSharedPreferences(SHARED_APP, Context.MODE_PRIVATE);
        mContext = this;
        init();

    }
    public void init(){
        viewInit();
        preferInit();
        handlerInit();
        runInit();
    }
    public void preferInit(){
        String temp = getShared("searchServer");
        if (temp != null){
            etSearchServer.setText(temp);
        }

        temp = getShared("versionCode");
        if (temp != null){
            etVersion.setText(temp);
        }

        temp = getShared("apkUrl");
        if (temp != null){
            etApkUrl.setText(temp);
        }
    }
    public void viewInit(){
        btUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        btSearchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = etSearchServer.getText().toString();
                setShared("searchServer", temp);
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("searchServer", temp);

                    temp = etVersion.getText().toString();
                    setShared("versionCode", temp);
                    jsonObject.put("versionCode", temp);

                    temp = etApkUrl.getText().toString();
                    setShared("apkUrl", temp);
                    jsonObject.put("apkUrl", temp);
                }catch (Exception e){
                    e.printStackTrace();
                }
                data = jsonObject.toString();
                stateText.setText("start post");
                new Thread(postRun).start();
            }
        });
    }
    public void handlerInit(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
               super.handleMessage(msg);
                switch(msg.what){
                    case 0:
                        stateText.setText("post finish");
                        break;
                }
            }
        };
    }
    public void runInit(){
        postRun = new Runnable() {
            @Override
            public void run() {
                String ret = ServerHelper.getInstance().getData("preference");
                if (ret != null){
                    Log.d("shanlihou", ret);
                    JSONTokener jsonTokener = new JSONTokener(ret);
                    try{
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        JSONArray jsonResult = (JSONArray) jsonObject.get("results");
                        JSONObject jsonPrefer = jsonResult.getJSONObject(0);
                        String objId = jsonPrefer.getString("objectId");
                        ServerHelper.getInstance().putData("preference/" + objId, data);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //ServerHelper.getInstance().postData("preference", data);
                mHandler.sendEmptyMessage(0);
            }
        };
    }
    public void setShared(String key, String value){
        SharedPreferences.Editor editor = mShared.edit();
        if (value == null){
            editor.remove(key);
        }
        else{
            editor.putString(key,value);
        }
        editor.commit();
    }
    public String getShared(String key){
        return mShared.getString(key, null);
    }
}
