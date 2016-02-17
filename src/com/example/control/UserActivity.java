package com.example.control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/10 0010.
 */
public class UserActivity extends Activity{
    private ListView mUserListView;
    private Handler mHandler;
    private Runnable userRun;
    private List<Map<String, String>> mUserList;
    private Context mContext;
    private SimpleAdapter simpleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        mContext = this;
        mUserListView = (ListView)findViewById(R.id.lvUserList);
        init();
    }
    private void init(){
        handlerInit();
        runInit();
        new Thread(userRun).start();
    }
    private void handlerInit(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        Log.d("shanlihou", "set adapter");
                        simpleAdapter = new SimpleAdapter(mContext, mUserList, R.layout.user_item,
                                new String[]{"name", "pass"},
                                new int[]{R.id.tUserName, R.id.tPassWord});
                        mUserListView.setAdapter(simpleAdapter);
                        break;
                }
            }
        };
    }
    private void runInit(){
        userRun = new Runnable() {
            @Override
            public void run() {
                String priKey = RSA.getInstance().getPrivateKey(mContext, R.raw.pri_191be71fff);
                if (mUserList == null){
                    mUserList = new ArrayList<>();
                }else{
                    mUserList.clear();
                }
                int times = 0;
                while(true){
                    String addArg = "";
                    if (times != 0){
                        addArg = "skip=" + times * 200;
                    }
                    List<Map<String, String>> list = ServerHelper.getInstance().getUserInfo(priKey, addArg);
                    times++;
                    if (list.size() == 0){
                        break;
                    }
                    mUserList.addAll(list);
                }
                Log.d("shanlihou", "user size:" + mUserList.size());
                mHandler.sendEmptyMessage(0);
            }
        };
    }
}
