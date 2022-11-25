package com.geostat.census_2024.architecture.module;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class EncryptModule {

    private static SecretKey key;

    static {
        try {
            byte[] secretBytes = "secret key".getBytes("UTF8");
            DESKeySpec keySpec = new DESKeySpec(secretBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            key = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            Log.e("encrypt", "DatabaseHelper " + e.toString());
        }
    }

    public byte[] encryptPassword(String password) {
        try {
            byte[] cleartext = password.getBytes("UTF8");

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] clearBytes = cipher.doFinal(cleartext);
            return Base64.encodeBase64(clearBytes);
        } catch (Exception e) {
            Log.e("encrypt", "DatabaseHelper " + e.toString());
        }
        return null;
    }

    public String decryptPassword(byte[] password) {
        String pw = "";
        try {
            byte[] encrypedPwdBytes = Base64.decodeBase64(password);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encrypedPwdBytes);
            pw = new String(plainTextPwdBytes, "UTF8");
        } catch (Exception e) {
            Log.e("encrypt", "DatabaseHelper " + e.toString());
        }
        return pw;
    }
}
