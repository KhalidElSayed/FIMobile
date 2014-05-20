package com.deploy.fi.fragments;

import java.util.Hashtable;

import com.deploy.fi.BuscarClienteActivity;
import com.deploy.fi.R;
import com.deploy.fi.services.struct.Sucursal;
import com.deploy.fi.services.struct.old.Ciudad;
import com.deploy.fi.services.struct.old.Cliente;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class BuscarClienteFragment extends Fragment {

	private Button btnBuscar;
	
	public BuscarClienteFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_prospecto, container, false);
        
        final EditText txtNombre = (EditText) rootView.findViewById(R.id.txtBusquedaCuenta);
        final EditText txtNit = (EditText) rootView.findViewById(R.id.txtBusquedaNit);
        final EditText txtRazonSocial = (EditText) rootView.findViewById(R.id.txtBusquedaRazonSocial);
        final EditText txtCiudad = (EditText) rootView.findViewById(R.id.txtBusquedaCiudad);
        final EditText txtAlmacen = (EditText) rootView.findViewById(R.id.txtBusquedaAlmacen);
        final EditText txtVentasDesde = (EditText) rootView.findViewById(R.id.txtBusquedaVentasDesde);
        final EditText txtVentasHasta = (EditText) rootView.findViewById(R.id.txtBusquedaVemtasHasta);
        
        btnBuscar = (Button) rootView.findViewById(R.id.btnBuscar);
        
        btnBuscar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				BuscarClienteActivity buscarActivity = (BuscarClienteActivity) getActivity();
				
				String NOMBRE = txtNombre.getText().toString().trim();
				String NIT = txtNit.getText().toString().trim();
				String RAZON_SOCIAL = txtRazonSocial.getText().toString().trim();
				String CIUDAD = txtCiudad.getText().toString().trim();
				String ALMACEN = txtAlmacen.getText().toString().trim();
				String VENTAS_DESDE = txtVentasDesde.getText().toString().trim();
				String VENTAS_HASTA = txtVentasHasta.getText().toString().trim();
				
				Hashtable<String,String> params = new Hashtable<String, String>();
				if(NOMBRE != null && NOMBRE.length()>0) params.put("NOMBRE", NOMBRE);
				if(NIT != null && NIT.length()>0) params.put("NIT", NIT);
				if(RAZON_SOCIAL != null && RAZON_SOCIAL.length()>0) params.put("RAZON_SOCIAL", RAZON_SOCIAL);
				if(CIUDAD != null && CIUDAD.length()>0) params.put("CIUDAD", CIUDAD);
				if(ALMACEN != null && ALMACEN.length()>0) params.put("ALMACEN", ALMACEN);
				if(VENTAS_DESDE != null && VENTAS_DESDE.length()>0) params.put("VENTAS_DESDE", VENTAS_DESDE);
				if(VENTAS_HASTA != null && VENTAS_HASTA.length()>0) params.put("VENTAS_HASTA", VENTAS_HASTA);
				
				buscarActivity.buscarClientes(params);
			}
		});
        
        return rootView;
    }
	
}
