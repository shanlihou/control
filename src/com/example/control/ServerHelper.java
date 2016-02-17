package com.example.control;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by shily on 16-2-7.
 */
public class ServerHelper {
    private static String SERVER_URL = "https://api.bmob.cn/1/classes/";
    private static ServerHelper instance = null;
    private ServerHelper(){
    }
    public static ServerHelper getInstance(){
        if (instance == null){
            instance = new ServerHelper();
            return instance;
        }else
            return instance;
    }

    public String getData(String dataType){
        String url = SERVER_URL + dataType;
        Map<String, String> header = new HashMap<>();
        header.put("X-Bmob-Application-Id", "f959535a39bb9dec9ac4dab32e5961c5");
        header.put("X-Bmob-REST-API-Key","17342bb32e2df845778bb70391b1c4a6");
        HttpContent req = UrlOpener.getInstance().urlOpen(url, header);
        if (req != null){
            Log.d("shanlihou", req.getContent());
            return req.getContent();
        }
        return null;
    }

    public Map<String, String> getPub(String dataType){
        String url = SERVER_URL + dataType;
        Map<String, String> header = new HashMap<>();
        header.put("X-Bmob-Application-Id", "f959535a39bb9dec9ac4dab32e5961c5");
        header.put("X-Bmob-REST-API-Key","17342bb32e2df845778bb70391b1c4a6");
        HttpContent req = UrlOpener.getInstance().urlOpen(url, header);
        Log.d("shanlihou", req.getContent());
        try{
            JSONTokener jsonTokener = new JSONTokener(req.getContent());
            JSONObject jsonRet = (JSONObject)jsonTokener.nextValue();
            JSONArray jsonResult = (JSONArray) jsonRet.get("results");
            JSONObject jsonPub = (JSONObject) jsonResult.get(0);
            Log.d("shanlihou", jsonPub.getString("pubKey"));
            Map<String, String> mapRet = new HashMap<>();
            mapRet.put("pubKey", jsonPub.getString("pubKey"));
            mapRet.put("key", jsonPub.getString("objectId"));
            return mapRet;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void postData(String dataType, String data){
        String url = SERVER_URL + dataType;
        Map<String, String> header = new HashMap<>();
        header.put("X-Bmob-Application-Id", "f959535a39bb9dec9ac4dab32e5961c5");
        header.put("X-Bmob-REST-API-Key","17342bb32e2df845778bb70391b1c4a6");
        header.put("Content-Type", "application/json");

        HttpContent req = UrlOpener.getInstance().urlPost(url, header, data);
    }
    public void putData(String dataType, String data){
        String url = SERVER_URL + dataType;
        Map<String, String> header = new HashMap<>();
        header.put("X-Bmob-Application-Id", "f959535a39bb9dec9ac4dab32e5961c5");
        header.put("X-Bmob-REST-API-Key","17342bb32e2df845778bb70391b1c4a6");
        header.put("Content-Type", "application/json");

        HttpContent req = UrlOpener.getInstance().urlPut(url, header, data);
    }

    public List<Map<String, String>> getUserInfo(String priKey, String addArg){
        List<Map<String, String>> retList = new ArrayList<>();
        String type = "userInfo?limit=200";
        if (!addArg.equals("")){
            type += "&" + addArg;
        }
        String data = getData(type);
        JSONTokener jsonTokener = new JSONTokener(data);
        try {
            JSONObject jsonRet = (JSONObject)jsonTokener.nextValue();
            JSONArray jsonResult = (JSONArray) jsonRet.get("results");
            int len = jsonResult.length();
            for (int i = 0; i < len; i++){
                Map<String, String> temp = new HashMap<>();
                JSONObject jsonUser = (JSONObject)jsonResult.get(i);
                Log.d("shanlihou", jsonUser.toString());
                String strTemp = jsonUser.getString("name");
                String userName = RSA.getInstance().decrypt(priKey, strTemp);
                temp.put("name", i + ":" + userName);
                Log.d("shanlihou", userName + ":" + jsonUser.toString());
                if (!jsonUser.has("pass")){
                    continue;
                }
                strTemp = jsonUser.getString("pass");
                String passWord = RSA.getInstance().decrypt(priKey, strTemp);
                temp.put("pass", passWord);
                DBHelper.getInstance().insert(userName, passWord);
                //Log.d("shanlihou", "user name is :" + userName);
                retList.add(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return retList;
    }
    public List<Map<String, String>> getUserSearch(String priKey, String addArg){
        List<Map<String, String>> retList = new ArrayList<>();
        String type = "search?limit=200";
        if (!addArg.equals("")){
            type += "&" + addArg;
        }
        String data = getData(type);
        JSONTokener jsonTokener = new JSONTokener(data);
        try {
            JSONObject jsonRet = (JSONObject)jsonTokener.nextValue();
            JSONArray jsonResult = (JSONArray) jsonRet.get("results");
            int len = jsonResult.length();
            for (int i = 0; i < len; i++){
                Map<String, String> temp = new HashMap<>();
                JSONObject jsonUser = (JSONObject)jsonResult.get(i);
                String strTemp = jsonUser.getString("name");
                String userName = RSA.getInstance().decrypt(priKey, strTemp);
                temp.put("name", userName);
                strTemp = jsonUser.getString("code");
                String passWord = RSA.getInstance().decrypt(priKey, strTemp);
                temp.put("search", passWord);
                strTemp = jsonUser.getString("objectId");
                temp.put("objId", strTemp);
                strTemp = jsonUser.getString("createdAt");
                temp.put("date", strTemp);
                DBHelper.getInstance().insert(temp);
                //Log.d("shanlihou", "user name is :" + userName);
                retList.add(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return retList;
    }

}
