package com.deploy.fi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.deploy.fi.tools.ActivityTools;

public class DashboardActivity extends ProgressActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        setTitle("Formas Intimas");
        /*DBManager db = new DBManager(this);
		db.open();
		db.deletePedido(0);
		db.close();*/
        
        GUI();
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideProgressDialog();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		hideProgressDialog();
	}
	
	private static int LAUNCH_ACTIVITY_SINCH = 11;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LAUNCH_ACTIVITY_SINCH) {
			if(resultCode == RESULT_OK){
				int result = data.getExtras().getInt("result");
				if(result == 1) Toast.makeText(getApplicationContext(),"Sincronizacion exitosa", Toast.LENGTH_SHORT).show();
				else Toast.makeText(getApplicationContext(),"Error en la sincronizacion", Toast.LENGTH_SHORT).show();
			}
			else Toast.makeText(getApplicationContext(),"Error en la sincronizacion", Toast.LENGTH_SHORT).show();
		}
		hideProgressDialog();
	}
	
	private void GUI(){
		Button btnSync = (Button) findViewById(R.id.btnSync);
		btnSync.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				showProgressDialog();
				Intent intent = new Intent(getApplicationContext(), SincronizacionActivity.class);
				ActivityTools.waitForActivity(DashboardActivity.this, intent, LAUNCH_ACTIVITY_SINCH);
			}
		});
		
		Button btnClientes = (Button) findViewById(R.id.btnClientes);
		btnClientes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				showProgressDialog();
				ActivityTools.launchOne(DashboardActivity.this, BuscarClienteActivity.class);
			}
		});
		
		Button btnFacturacion = (Button) findViewById(R.id.btnFacturacion);
		btnFacturacion.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			}
		});
		
		Button btnPresupuesto = (Button) findViewById(R.id.btnPresupuesto);
		btnPresupuesto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			}
		});
		
		Button btnInformes = (Button) findViewById(R.id.btnInformes);
		btnInformes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			}
		});
		
		Button btnRelaciones = (Button) findViewById(R.id.btnRelaciones);
		btnRelaciones.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			}
		});
		
		Button btnCalendario = (Button) findViewById(R.id.btnCalendario);
		btnCalendario.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			}
		});
		
		Button btnPerfil = (Button) findViewById(R.id.btnPerfilc);
		btnPerfil.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			}
		});
		
		Button btnNovedades = (Button) findViewById(R.id.btnNovedades);
		btnNovedades.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			}
		});
		
		Button btnRutas = (Button) findViewById(R.id.btnRutas);
		btnRutas.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			}
		});
		
		Button btnSalir = (Button) findViewById(R.id.btnSalir);
		btnSalir.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				createTemplateDialog().show();
			}
		});
		
		Button btnPedidosCongelados = (Button) findViewById(R.id.btnPedidosCongelados);
		btnPedidosCongelados.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				showProgressDialog();
				ActivityTools.launchOne(DashboardActivity.this, CongeladosActivity.class);
			}
		});
		
	}
	
	private AlertDialog.Builder createTemplateDialog(){
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Desea cerra la sessión ?");
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	LoginActivity.deleteToken(DashboardActivity.this);
	        	finish();
	      } });
	    adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	      } });
	    return adb;
	}
	
}
