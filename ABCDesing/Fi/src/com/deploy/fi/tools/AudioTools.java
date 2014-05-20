package com.deploy.fi.tools;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioTools {
	
	/*
	 * Usage:
	 * 
	 * AudioTools.playSound(this, R.raw.sound);
	 * */
	public static void playSound(Activity activity, int id){
		try{
			MediaPlayer mp = MediaPlayer.create(activity, id);
		    mp.start();
		}catch(Exception e){
			e.printStackTrace();
			Log.e("Exception", e.toString());
		}
	}
	
}
