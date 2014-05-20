package com.deploy.fi.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.util.Log;

public class SQLiteTools {

	private static String DATE_PATTERN = "yyyy-MM-dd";
	
	public static java.util.Date getCurrentDate(){
		return new java.util.Date();
	}
	
	public static String toString(long time){
		return toString(parseDate(time));
	}
	
	public static String toString(java.util.Date date){
		String str = null;
		try{
			SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
			str = df.format(date);
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		return str;
	}
	
	public static java.util.Date parseDate(long time){
		java.util.Date date = null;
		try{
			date = new java.util.Date(time);
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		return date;
	}
	
	public static java.util.Date addDays(java.util.Date date, int days){
		java.util.Date dateResult = null;
		if(days <= 0) days = 1;
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, days);
			dateResult = parseDate(dateFormat.format(c.getTime()));
		}catch(Exception e){}
		return dateResult;
	}
	
	public static long parseDate(java.util.Date date){
		long time = 0;
		try{
			time = date.getTime();
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		return time;
	}
	
	public static java.util.Date parseDate(String value){
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
		java.util.Date date = null;
		try{
			date = dateFormat.parse(value);
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		return date;
	}
	
}
