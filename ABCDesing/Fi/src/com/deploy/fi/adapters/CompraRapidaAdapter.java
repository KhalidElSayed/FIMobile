package com.deploy.fi.adapters;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.deploy.fi.CompraRapidaActivity;
import com.deploy.fi.ProgressActivity;
import com.deploy.fi.R;
import com.deploy.fi.services.struct.CompraRapidaItem;
import com.deploy.fi.services.struct.ICompraRapidaItem;
import com.deploy.fi.tools.SortArray;

public class CompraRapidaAdapter extends ArrayAdapter<CompraRapidaItem> {

	private Context mContext = null;
	private int mResourceId = 0;
	
	/**
	 * Constructor
	 **/
	public CompraRapidaAdapter(Context context, int resourceId, List<CompraRapidaItem> objects) {
		super(context, resourceId, objects);
		mContext = context;
		mResourceId = resourceId;
	}
	
	/**
	 * Constructor
	 **/
	public CompraRapidaAdapter(Context context, int resourceId, CompraRapidaItem[] objects) {
		super(context, resourceId, objects);
		mContext = context;
		mResourceId = resourceId;
	}
	
	public ICompraRapidaItem iCompraRapidaItem = null;
	public String[] indiceTallas = null;
	
	private LayoutInflater inflater = null;
	
	public static boolean isNumeric(String str) {
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
	
	/**
	 * This method so executed when a show a item in the list 
	 **/
	public View getView(final int position, View convertView, ViewGroup parent) {
		 View row = convertView;
		 
		 if(convertView == null){
			 inflater = ((ProgressActivity)mContext).getLayoutInflater();
		     row = inflater.inflate(mResourceId, parent, false);
		 }
		 
		 CompraRapidaItem item = getItem(position);
	     if(row != null && item != null){
	    	 ImageView imageViewdelete = (ImageView)row.findViewById(R.id.imageViewdelete);
	    	 imageViewdelete.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if(iCompraRapidaItem != null) iCompraRapidaItem.removeFila(position);
				}
	    	 });
	    	 
	    	 final EditText editTextReferencia = (EditText)row.findViewById(R.id.editTextReferencia);
	    	 
	    	 String Referencia = item.getReferencia();
	    	 if(Referencia != null && Referencia.length() > 0) editTextReferencia.setText(Referencia);
	    	 else editTextReferencia.setText("");
	    	 
	    	 editTextReferencia.setOnEditorActionListener(new OnEditorActionListener(){
				@Override
				public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
					if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
						((CompraRapidaActivity)mContext).showProgressDialog();
						String s = editTextReferencia.getText().toString().trim();
						if(s != null && s.length()>0){
							if(iCompraRapidaItem != null) iCompraRapidaItem.updateReferencia(s, position);
							return true;
						}
						else {
							((CompraRapidaActivity)mContext).hideProgressDialog();
							return false;
						}
			        }
					else if(event == null){
						((CompraRapidaActivity)mContext).showProgressDialog();
						String s = editTextReferencia.getText().toString().trim();
						if(s != null && s.length()>0){
							if(iCompraRapidaItem != null) iCompraRapidaItem.updateReferencia(s, position);
							return true;
						}
						else {
							((CompraRapidaActivity)mContext).hideProgressDialog();
							return false;
						}
					}
			        return false;
				}
	    		 
	    	 });
	    	 
	    	 String CodigosColor = item.getSelectCodigosColor();
	    	 String Color = item.getSelectColor();
	    	 
	    	 final EditText editTextColor = (EditText)row.findViewById(R.id.editTextColor);
	    	 
	    	 if(CodigosColor != null && CodigosColor.length() > 0 && Color != null && Color.length() > 0) {
	    		 if(CodigosColor != "..." && Color != "...") editTextColor.setText(CodigosColor + " - " + Color);
	    		 else editTextColor.setText("...");
	    	 }
	    	 else editTextColor.setText("");
	    	 
	    	 editTextColor.setOnClickListener(new OnClickListener(){
	    		@Override
				public void onClick(View v) {
	    			((CompraRapidaActivity)mContext).showProgressDialog();
	    			String s = editTextColor.getText().toString().trim();
	    			if(s.length()>0 && iCompraRapidaItem != null) iCompraRapidaItem.focusColor(position);
	    			else ((CompraRapidaActivity)mContext).hideProgressDialog();
				}
	    	 });
	    	 
	    	 final LinearLayout linearLayoutContainer = (LinearLayout)row.findViewById(R.id.linearLayoutContainer);
	    	 if(linearLayoutContainer.getChildCount()>0) linearLayoutContainer.removeAllViews();
	    	 
	    	 String[] headersTallas = item.getAviableTalla();
	    	 if(headersTallas != null && headersTallas.length>0){
	    		 if(item.refViewTallas == null){
	    			 item.refViewTallas = new View[headersTallas.length];
		    		 for(int i=0; i<headersTallas.length; i++){
		    			 boolean ceratedTalla = item.isCreatedFieldTalla(headersTallas[i]);
		    			 if(!ceratedTalla){
		    				 LinearLayout tallaInflate = (LinearLayout)inflater.inflate(R.layout.inflate_talla, parent, false);
		    				 item.refViewTallas[i] = tallaInflate;
		    				 item.refViewTallas[i].setContentDescription(headersTallas[i]);
		    				 TextView textViewTalla = (TextView)tallaInflate.findViewById(R.id.textViewTalla);
		    				 textViewTalla.setContentDescription(headersTallas[i]);
		    				 textViewTalla.setText(headersTallas[i] + ":");
		    				 
		    				 final EditText editTextTalla = (EditText)tallaInflate.findViewById(R.id.editTextTalla);
		    				 editTextTalla.setContentDescription(headersTallas[i]);
		    				 if(item.isAviableTalla(headersTallas[i])){
		    					 int count = item.getCountTalla(headersTallas[i]);
		    					 editTextTalla.setText(String.valueOf(count));
		    				 }
		    				 editTextTalla.setOnEditorActionListener(new OnEditorActionListener(){
		    					 @Override
		    					 public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
		    						 if (event == null || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
		    							 if(iCompraRapidaItem != null) {
		    								 String str = editTextTalla.getText().toString().trim();
		    								 int value = 0;
		    								 if(str != null && str.length()>0 && isNumeric(str)) {
		    									 ((CompraRapidaActivity)mContext).showProgressDialog();
		    									 value = Integer.parseInt(str);
		    									 iCompraRapidaItem.updateTalla(editTextTalla.getContentDescription().toString(), value, position);
		    								 }
		    								 else editTextTalla.setText("");
		    							 }
		    							 return true;
		    						 }
		    						 return false;
		    					 }	 
		    				 });
		    			 }
		    			 item.setCreatedFieldTalla(headersTallas[i], true);
		    		 }
		    		 
		    		 item.refViewTallas = sortTallas(item.refViewTallas, headersTallas);
	    		 }
	    		 
	    		 
	    		 
	    		 if(item.refViewTallas != null && item.refViewTallas.length>0){
	    			 for(int i=0; i<item.refViewTallas.length; i++) linearLayoutContainer.addView(item.refViewTallas[i]);
	    		 }
	    		 
	    	 }
	    	 
	    	//***********************************************************************************
	    	 
	     }
	     return row;
	}
	
	private String[] createArrayOrder(View[] refViewTallas, String[] headersTallas){
		String[] outTallas = null;
		if(refViewTallas != null && refViewTallas.length>0){
			headersTallas = new String[headersTallas.length];
			String[] orderTallas = new String[headersTallas.length];
			for(int i=0; i<headersTallas.length; i++) {
				orderTallas[i] = refViewTallas[i].getContentDescription().toString();
				headersTallas[i] = refViewTallas[i].getContentDescription().toString();
			}
			
			String[] alpha = null, numbers = null;
			if(indiceTallas != null && indiceTallas.length>0){
				SortArray sorter = new SortArray(indiceTallas);
				alpha = sorter.filterAndSort(orderTallas, true);
				numbers = sorter.filterAndSort(orderTallas, false);
			}
			else{
				SortArray sorter = new SortArray();
				numbers = sorter.filterAndSort(orderTallas, false);
			}
			outTallas = SortArray.mixArray(alpha, numbers);
		}
		return outTallas;
	}
	
	private View[] sortTallas(View[] refViewTallas, String[] headersTallas){
		View[] outViewTallas = refViewTallas;//36,32,34
		if(refViewTallas != null && refViewTallas.length>0){
			headersTallas = new String[headersTallas.length];
			for(int i=0; i<headersTallas.length; i++) headersTallas[i] = refViewTallas[i].getContentDescription().toString();
			String[] orderTallas = createArrayOrder(refViewTallas, headersTallas);
			
			Vector<View> vector = new Vector<View>(0);
	         if(orderTallas != null && orderTallas.length>0){
	            for(int i=0; i<orderTallas.length; i++) {
	                for(int j=0; j<headersTallas.length; j++) {
	                    if(orderTallas[i].equals(headersTallas[j])) vector.add(refViewTallas[j]);
	                }
	            }
	        }
	        headersTallas = null;
	        orderTallas = null;
	         
	        if(vector.size()>0){
	        	outViewTallas = new View[vector.size()];
	        	vector.copyInto(outViewTallas);
	        }
		}
		return outViewTallas;
	}
	
}
