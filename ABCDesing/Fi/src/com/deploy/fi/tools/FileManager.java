package com.deploy.fi.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileManager {

	//********************************************************************************************
	
	public static String fixPath(String path){
		if(path != null && path.length()>0){
			if(!path.startsWith(File.separator)) path = File.separator + path;
			if(!path.endsWith(File.separator)) path = path + File.separator;
		}
		else path = "";
		return path;
	}
	
	public static boolean existFolder(Context context, String directoryName) {
		try{
			File file = new File(context.getFilesDir() + directoryName);
			return file.exists();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean createFolder(Context context, String directoryName) {
		try{
			return new File(context.getFilesDir() + directoryName).mkdirs();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean deleteFolder(Context context, String directoryName) {
		try{
			File file = new File(context.getFilesDir() + directoryName);
			if (file.exists()) {
				file.delete();
				return true;
			} 
			else return false;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean deleteContentsFolderCompletly(Context context, String directoryName) {
		try{
			File file = new File(context.getFilesDir() + directoryName);
			if (file.exists()) {
				deleteRecursiveCompletly(file);
				return true;
			} 
			else return false;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	private static void deleteRecursiveCompletly(File dir) {
		try{
			Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
			if (dir.isDirectory()) {
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					File temp = new File(dir, children[i]);
					if (temp.isDirectory()) {
						Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
						deleteRecursiveCompletly(temp);
					} else {
						Log.d("DeleteRecursive", "Delete File" + temp.getPath());
						boolean b = temp.delete();
						if (b == false) {
							Log.d("DeleteRecursive", "DELETE FAIL");
						}
					}
				}
				// dir.delete();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean deleteContentsFolderWithRestrictions(Context context, String directoryName, String [] restrictedDirectories) {
		try{
			if(directoryName != null && directoryName.length()>0){
				if(!directoryName.startsWith(File.separator)) directoryName = File.separator + directoryName;
				if(!directoryName.endsWith(File.separator)) directoryName = directoryName + File.separator;
			}
			else directoryName = "";
			
			File file = new File(context.getFilesDir() + directoryName);
			if (file.exists()) {
				deleteRecursiveWithRestrictions(file,restrictedDirectories);
				return true;
			} 
			else return false;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	private static void deleteRecursiveWithRestrictions(File dir, String[] restrictedDirectories) {
		try{
			Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
			if (dir.isDirectory()) {
				boolean isRestrictedDirectory = false;
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					isRestrictedDirectory = false;
					for(int j = 0; j < restrictedDirectories.length; j++){
						restrictedDirectories[j] = restrictedDirectories[j].replace("/", "");
						if(children[i]
								.contains(restrictedDirectories[j])){
							isRestrictedDirectory = true;
							break;
						}
					}
					if (!isRestrictedDirectory) {
						File temp = new File(dir, children[i]);
						if (temp.isDirectory()) {
							Log.d("DeleteRecursive",
									"Recursive Call" + temp.getPath());
							deleteRecursiveWithRestrictions(temp, restrictedDirectories);
						} else {
							Log.d("DeleteRecursive", "Delete File" + temp.getPath());
							boolean b = temp.delete();
							if (b == false) {
								Log.d("DeleteRecursive", "DELETE FAIL");
							}
						}
					}
				}
				// dir.delete();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//********************************************************************************************
	
	public static boolean deleteFile(Context context, String directoryName, String fileName) {
		try{
			File file = new File(context.getFilesDir() + directoryName + fileName);
			if (file.exists()) {
				file.delete();
				return true;
			} 
			else return false;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean existFile(Context context, String directoryName, String fileName) {
		try{
			File file = new File(context.getFilesDir() + directoryName + fileName);
			return file.exists();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static boolean createFile(Context context, String directoryName, String fileName, String content) {
		try{
			File file = new File(context.getFilesDir() + directoryName);
			if (!file.exists())
				file.mkdirs();
	
			file = new File(context.getFilesDir() + directoryName + fileName);
			if (!file.exists()) {
				try {
					FileWriter fileWriter;
					fileWriter = new FileWriter(file);
					fileWriter.write(content);
					fileWriter.flush();
					fileWriter.close();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static String readFile(Context context, String directoryName, String fileName) {
		try {
			File file = new File(context.getFilesDir() + directoryName + fileName);
			if (file.exists()) {
				InputStream fileInputStream = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(
						fileInputStream);
				BufferedReader buffreader = new BufferedReader(reader);

				String line;
				String dataSession = "";

				while ((line = buffreader.readLine()) != null) {
					dataSession += line;
				}

				fileInputStream.close();

				return dataSession;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static boolean writeBitmap(Context context, String directoryName, String fileName, Bitmap bitmap) {
		try{
			File file = new File(context.getFilesDir() + directoryName);
			if (!file.exists()) file.mkdirs();
			
			file = new File(context.getFilesDir() + directoryName + fileName);
			FileOutputStream fout = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
			fout.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static File getFile(Context context, String directoryName, String fileName){
		return new File(context.getFilesDir() + directoryName + fileName);
	}
	
	public static Bitmap readBitmap(Context context, String directoryName, String fileName) {
		try{
			File file = new File(context.getFilesDir() + directoryName + fileName);
			FileInputStream fin = new FileInputStream (file);
			Bitmap bitmap = BitmapFactory.decodeStream(fin);
			fin.close();
			if(bitmap != null) return bitmap;
		}catch(Exception e){}
		return null;
	}

	public static boolean writeBinary(Context context, String directoryName, String fileName, byte[] array) {
		try {
			File file = new File(context.getFilesDir() + directoryName);
			if (!file.exists()) file.mkdirs();
	
			file = new File(context.getFilesDir() + directoryName + fileName);
			FileOutputStream fout = new FileOutputStream(file);
			fout.write(array);
			fout.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static byte[] readBinary(Context context, String directoryName, String fileName) {
		try {
			File file = new File(context.getFilesDir() + directoryName + fileName);
			FileInputStream fin = new FileInputStream (file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int current;
			while ((current = fin.read()) != -1) {
				bos.write(current);
			}
			fin.close();
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//********************************************************************************************
	
}
