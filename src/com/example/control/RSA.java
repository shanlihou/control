package com.example.control;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by shily on 16-2-7.
 */
public class RSA {
    private RSA(){
    }
    private static RSA instance = null;
    public static RSA getInstance(){
        if (instance == null){
            instance = new RSA();
        }
        return instance;
    }
public String encrypt(String pubKey, String strToEnc){
        byte[] encPass = null;
        String basePass = null;
        try
        {
            int index = pubKey.indexOf("-----END PUBLIC KEY-----");
            String tmpStr = pubKey.substring(26, index);
            Log.d("shanlihou", "pub length:" + pubKey.length());
            Log.d("shanlihou", pubKey);
            Log.d("shanlihou", tmpStr);
            String tmpFinal = tmpStr.replaceAll("\n", "");
            Log.d("shanlihou", tmpFinal);

            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(tmpFinal));
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    new  BASE64Decoder().decodeBuffer(tmpFinal));
            Log.d("shanlihou", new BASE64Decoder().decodeBuffer(tmpFinal).toString().length() + "");
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            Log.d("shanlihou", strToEnc.getBytes().length + strToEnc);
            encPass = cipher.doFinal(strToEnc.getBytes());
            basePass = new BASE64Encoder().encodeBuffer(encPass).toString();

            Log.d("shanlihou", "basePass:" + basePass);
            Log.d("shanlihou", "baPass length:" + basePass.length());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return basePass;
    }
    public String decrypt(String privateKey, String strToEnc){
        byte[] encPass = null;
        String basePass = null;
        try
        {
            //Log.d("shanlihou", privateKey);

            int start = new String("-----BEGIN RSA PRIVATE KEY-----").length();
            int index = privateKey.indexOf("-----END RSA PRIVATE KEY-----");
            String tmpStr = privateKey.substring(start, index);
            //Log.d("shanlihou", tmpStr);
            String tmpFinal = tmpStr.replaceAll("\n", "");
            //Log.d("shanlihou", tmpFinal);

            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(tmpFinal));
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    new  BASE64Decoder().decodeBuffer(tmpFinal));

            //printBytes(new BASE64Decoder().decodeBuffer(tmpFinal));
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA", "BC");
            // 取公钥匙对象
            //PrivateKey privateKey1 = keyFactory.generatePrivate(bobPubKeySpec);
            RSAPrivateKey privateKey1 = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey1);
            encPass = cipher.doFinal(new BASE64Decoder().decodeBuffer(strToEnc));
            String ret = getPass(encPass);
            Log.d("shanlihou", "enc is:" + ret);
            return ret;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    public String getPrivateKey(Context context, int ID){
        InputStream in;
        String temp = "";
        try {
            in = context.getResources().openRawResource(ID);
            byte[] buff = new byte[1024];// 缓存
            int rd = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((rd = in.read(buff)) != -1) {
                baos.write(buff, 0, rd);
                temp += new String(baos.toByteArray(), "UTF-8");
            }
            baos.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }
    public void printBytes(byte[] encPass){
        String print = new String();
        for (byte i:encPass){
            print = print + (i & 0xff) + " ";
        }
        Log.d("shanlihou","length:" + encPass.length + "");
        Log.d("shanlihou", "byte:" + print);

    }
    public String getPass(byte[] encPass){
        int len = encPass.length;
        for (int i = len - 1; i >= 0; i--){
            if (encPass[i] == 0){
                byte[] ret = new byte[len - i - 1];
                System.arraycopy(encPass, i + 1, ret, 0, len - i - 1);
                return new String((ret));
            }
        }
        return null;
    }
}
