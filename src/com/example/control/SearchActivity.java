package com.example.control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/10 0010.
 */
public class SearchActivity extends Activity{
    private Context mContext;
    private ListView mSearchListView;
    private List<Map<String, String>> mSearchList;
    private Handler mHandler;
    private Runnable searchRun;
    private SimpleAdapter simpleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);
        mContext = this;
        mSearchListView = (ListView)findViewById(R.id.lvSearchList);
        init();
    }
    private void init(){
        handlerInit();
        runInit();
        new Thread(searchRun).start();
    }
    private void handlerInit(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        Log.d("shanlihou", "set adapter");
                        simpleAdapter = new SimpleAdapter(mContext, mSearchList, R.layout.user_item,
                                new String[]{"name", "search"},
                                new int[]{R.id.tUserName, R.id.tPassWord});
                        mSearchListView.setAdapter(simpleAdapter);
                        break;
                }
            }
        };
    }
    private void runInit(){
        searchRun = new Runnable() {
            @Override
            public void run() {
                String priKey = RSA.getInstance().getPrivateKey(mContext, R.raw.pri_191be71fff);
                if (mSearchList == null){
                    mSearchList = new ArrayList<>();
                }else{
                    mSearchList.clear();
                }
                int times = 0;
                while(true){
                    String addArg = "";
                    if (times != 0){
                        addArg = "skip=" + times * 200;
                    }
                    List<Map<String, String>> list = ServerHelper.getInstance().getUserSearch(priKey, addArg);
                    times++;
                    if (list.size() == 0){
                        break;
                    }
                    mSearchList.addAll(list);
                }
                Log.d("shanlihou", "size:" + mSearchList.size());
                mHandler.sendEmptyMessage(0);
            }
        };
    }
}
