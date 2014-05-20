package com.deploy.fi.tools;

import java.security.MessageDigest;

import android.util.Log;

public class Hash {

	public static String getDefaultDigest(String value){
		return value;
		//return getMD5(value);
	}
	
	private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
	
	private static String getSHA256(String value){
		StringBuffer hexString = new StringBuffer();
		try{
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
			digest.update(value.getBytes());
			byte messageDigest[] = digest.digest();
			for (byte b : messageDigest) {
				String s = Integer.toHexString((int) (b & 0xff));
				if(s.length() == 1) s = "0" + s;
				hexString.append(s);
			}
		}catch(Exception e){
			Log.e("Exception", e.getMessage());
		}
		return hexString.toString();
	}
	
	private static String getMD5(String value){
		StringBuffer hexString = new StringBuffer();
		try{
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(value.getBytes());
			byte messageDigest[] = digest.digest();
			for (byte b : messageDigest) {
				String s = Integer.toHexString((int) (b & 0xff));
				if(s.length() == 1) s = "0" + s;
				hexString.append(s);
			}
		}catch(Exception e){
			Log.e("Exception", e.getMessage());
		}
        return hexString.toString();
	}
	
}
