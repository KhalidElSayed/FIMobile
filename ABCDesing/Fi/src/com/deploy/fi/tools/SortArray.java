package com.deploy.fi.tools;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

public class SortArray {

	public String[] arrayOrder = null;

	public SortArray(){}
	
	public SortArray(String[] arrayOrder){
		if(arrayOrder != null && arrayOrder.length>0) this.arrayOrder = arrayOrder;
	}

	public static String[] toArray(Vector vector){
		String[] list = null;
		if(vector != null && vector.size()>0){
			list = new String[vector.size()];
			vector.copyInto(list);
		}
		return list;
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {}
		return false;
	}

	public static String[] getFilterArray(String[] input, boolean alpha){
		Vector vector = new Vector(0);
		if(input != null && input.length>0){
			for(int i=0; i<input.length; i++){
				if(input[i] != null && input[i].length()>0){
					if(alpha && !isInteger(input[i]) && !vector.contains(input[i])) vector.add(input[i]);
					else if(!alpha && isInteger(input[i]) && !vector.contains(input[i])) vector.add(input[i]);
				}
			}
		}
		return toArray(vector);
	}

	public String[] sortArray(String[] input, boolean alpha){
		Vector vector = new Vector(0);
		if(input != null && input.length>0){
			if(alpha){
				Arrays.sort(input, new Comparator<String>() {
					@Override
					public int compare(String value, String compare) {
						int indexValue = -1, indexCompare = -1;
						if(arrayOrder != null && arrayOrder.length>0){
							for(int j=0; j<arrayOrder.length; j++){
								if(arrayOrder[j].equals(compare)) indexCompare = j;
								if(arrayOrder[j].equals(value)) indexValue = j;
							}
							if(indexValue < indexCompare) return -1;
							else if(indexValue > indexCompare) return 1;
							else return 0;
						}
						else return 0;
					}
				});

				for(int i=0; i<input.length; i++){
					if(!vector.contains(input[i])) vector.add(input[i]);
				}
			}
			else{
				for(int s=0;s<=input.length-1;s++){
					for(int k=0;k<=input.length-2;k++){
						if(Integer.parseInt(input[k]) > Integer.parseInt(input[k+1])){
							int temp=0;  
							temp = Integer.parseInt(input[k]);
							input[k] = Integer.parseInt(input[k+1]) + "";
							input[k+1] = temp + "";
						}
					}
				}
				for(int i=0; i<input.length; i++){
					if(!vector.contains(input[i])) vector.add(input[i]);
				}
			}
		}
		return input;
	}

	public String[] filterAndSort(String[] input, boolean alpha){
		String[] output = null;
		String[] filter = getFilterArray(input, alpha);
		if(filter != null && filter.length>0) output = sortArray(filter, alpha);
		return output;
	}

	public static String[] mixArray(String[] alpha, String[] numbers){
		Vector vector = new Vector(0);
		if(alpha != null && alpha.length>0){
			for(int i=0; i<alpha.length; i++) vector.add(alpha[i]);
		}
		if(numbers != null && numbers.length>0){
			for(int i=0; i<numbers.length; i++) vector.add(numbers[i]);
		}
		return toArray(vector);
	}

}
