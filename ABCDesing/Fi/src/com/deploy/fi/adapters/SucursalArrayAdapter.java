package com.deploy.fi.adapters;

import java.util.List;

import com.deploy.fi.ProgressFragmentActivity;
import com.deploy.fi.R;
import com.deploy.fi.services.struct.Sucursal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SucursalArrayAdapter extends ArrayAdapter<Sucursal> {

	private Context mContext = null;
	private int mResourceId = 0;
	
	/**
	 * Constructor
	 **/
	public SucursalArrayAdapter(Context context, int resourceId, List<Sucursal> objects) {
		super(context, resourceId, objects);
		mContext = context;
		mResourceId = resourceId;
	}
	
	/**
	 * Constructor
	 **/
	public SucursalArrayAdapter(Context context, int resourceId, Sucursal[] objects) {
		super(context, resourceId, objects);
		mContext = context;
		mResourceId = resourceId;
	}
	
	private LayoutInflater inflater = null;
	
	/**
	 * This method so executed when a show a item in the list 
	 **/
	public View getView(int position, View convertView, ViewGroup parent) {
		 View row = convertView;
		 
		 if(convertView == null){
			 inflater = ((ProgressFragmentActivity)mContext).getLayoutInflater();
		     row = inflater.inflate(mResourceId, parent, false);
		 }
		 
		 Sucursal item = getItem(position);
	     
	     if(row != null && item != null){
	    	 TextView lblName = (TextView)row.findViewById(R.id.lblName);
	    	 lblName.setText(item.getNombreAlmacen());
	     }
	     return row;
	}

	
}
