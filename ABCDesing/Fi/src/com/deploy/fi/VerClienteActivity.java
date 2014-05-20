package com.deploy.fi;

import com.deploy.fi.services.struct.Sucursal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class VerClienteActivity extends ProgressActivity {

	private Sucursal sucursal = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_ver_clientes);
		
		showProgressDialog();
		
		Bundle extras = getIntent().getExtras(); 

		if (extras != null) sucursal = (Sucursal) extras.getSerializable("sucursal");
		
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		
		EditText txtRazonSocial = (EditText) findViewById(R.id.txtVerClienteRazonSocial);
		EditText txtNit = (EditText) findViewById(R.id.txtVerClienteNit);
		EditText txtTelefono = (EditText) findViewById(R.id.txtVerClienteTelefono);
		EditText txtCelular = (EditText) findViewById(R.id.txtVerClienteCelular);
		EditText txtCorreo = (EditText) findViewById(R.id.txtVerClienteCorreo);
		EditText txtDireccion = (EditText) findViewById(R.id.txtVerClienteDireccion);
		EditText txtCiudad = (EditText) findViewById(R.id.txtVerClienteCiudad);
		EditText txtZona = (EditText) findViewById(R.id.txtVerClienteZona);
		
		/*EditText txtNombre = (EditText) findViewById(R.id.txtVerClienteCuenta);
		txtNombre.setVisibility(View.GONE);
		EditText txtDepartamento = (EditText) findViewById(R.id.txtVerClienteDepartamento);
		txtDepartamento.setVisibility(View.GONE);
		EditText txtPais = (EditText) findViewById(R.id.txtVerClientePais);
		txtPais.setVisibility(View.GONE);
		EditText txtClima = (EditText) findViewById(R.id.txtVerClienteClima);
		txtClima.setVisibility(View.GONE);
		EditText txtContactos = (EditText) findViewById(R.id.txtVerClienteContactos);
		txtContactos.setVisibility(View.GONE);*/
        
		
		Button btnCompraRapida = (Button) findViewById(R.id.btnVerClienteCompraRapida);
        btnCompraRapida.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VerClienteActivity.this,CompraRapidaActivity.class);
				intent.putExtra("sucursal", sucursal);
				startActivity(intent);
			}
		});
        
        Button btnCompra = (Button) findViewById(R.id.btnComprarVerCliente);
        btnCompra.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*Intent intent = new Intent(VerClienteActivity.this,ComprarActivity.class);
				intent.putExtra("sucursal", sucursal);
				startActivity(intent);*/
			}
		});
        
        if(sucursal != null){
        	txtRazonSocial.setText(sucursal.getNombreAlmacen());
        	txtTelefono.setText(sucursal.getTelefono());
        	txtCelular.setText(sucursal.getTelefono());
        	txtCorreo.setText(sucursal.getEmail());
        	txtDireccion.setText(sucursal.getDireccion1());
        	txtCiudad.setText(sucursal.getCiudad());
        	txtZona.setText(sucursal.getZona());
        	txtNit.setText(sucursal.getNIT());
        }
		
        hideProgressDialog();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ver_cliente, menu);
		return true;
	}
	
}
