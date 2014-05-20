package com.deploy.fi.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.deploy.fi.ProgressActivity;
import com.deploy.fi.R;
import com.deploy.fi.services.struct.PedidoCongelado;
import com.deploy.fi.tools.SQLiteTools;

public class PedidosCongeladosAdapter extends ArrayAdapter<PedidoCongelado>{

	private Context mContext = null;
	private int mResourceId = 0;
	
	/**
	 * Constructor
	 **/
	public PedidosCongeladosAdapter(Context context, int resourceId, List<PedidoCongelado> objects) {
		super(context, resourceId, objects);
		mContext = context;
		mResourceId = resourceId;
	}
	
	/**
	 * Constructor
	 **/
	public PedidosCongeladosAdapter(Context context, int resourceId, PedidoCongelado[] objects) {
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
			 inflater = ((ProgressActivity)mContext).getLayoutInflater();
		     row = inflater.inflate(mResourceId, parent, false);
		 }
		 
		 PedidoCongelado item = getItem(position);
	     
	     if(row != null && item != null){
	    	 TextView textViewAlmacen = (TextView)row.findViewById(R.id.textViewAlmacen);
	    	 TextView textViewPedidoFecha = (TextView)row.findViewById(R.id.textViewPedidoFecha);
	    	 TextView textViewPedidoID = (TextView)row.findViewById(R.id.textViewPedidoID);
	    	 
	    	 if(item.NombreAlmacen != null && item.NombreAlmacen.length()>0) textViewAlmacen.setText("Almacen: "+item.NombreAlmacen);
	    	 else textViewAlmacen.setText("Almacen:");
	    	 
	    	 if(item.FechaPedido != null) textViewPedidoFecha.setText("Fecha Pedido: "+SQLiteTools.toString(item.FechaPedido));
	    	 else textViewPedidoFecha.setText("Fecha Pedido: ");
	    	 
	    	 textViewPedidoID.setText("ID Pedido: "+String.valueOf(item.idPedido));
	     }
	     return row;
	}
	
}
