package com.deploy.fi;

import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.deploy.fi.adapters.PedidosCongeladosAdapter;
import com.deploy.fi.services.struct.CompraRapidaSingleton;
import com.deploy.fi.services.struct.PedidoCongelado;
import com.deploy.fi.tools.DBManager;

public class CongeladosActivity extends ProgressActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congelados);
        setTitle("Pedidos congelados");
        
        GUI();
    }
	
	private Vector<PedidoCongelado> arrayPedidosCongelados = new Vector<PedidoCongelado>();
	private static CompraRapidaSingleton singleton = CompraRapidaSingleton.getInstance();
	
	private PedidosCongeladosAdapter adapter = null;
	
	@Override
	protected void onResume() {
		super.onResume();
		updateList();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideProgressDialog();
	}
	
	private void GUI(){
		showProgressDialog();
		
		ListView listViewCongelados = (ListView) findViewById(R.id.listViewCongelados);
		adapter = new PedidosCongeladosAdapter(this, R.layout.list_pedido_congelado, arrayPedidosCongelados);
		listViewCongelados.setAdapter(adapter);
		listViewCongelados.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PedidoCongelado item = arrayPedidosCongelados.get(position);
				Intent intent = new Intent(getApplicationContext(), CompraRapidaActivity.class);
				intent.putExtra("pedido", item);
				startActivity(intent);
			}
		});
		listViewCongelados.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				RemoveDialog(position);
				return false;
			}
		});
		
		updateList();
		
		hideProgressDialog();
		
	}
	
	private void RemoveDialog(final int index){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(CongeladosActivity.this);
		alertDialog.setTitle("Confirmacion Eliminar Congelado...");
		alertDialog.setMessage("Esta seguro de eliminar el pedido?");
		alertDialog.setIcon(R.drawable.salir);
		alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	int idPedido = arrayPedidosCongelados.get(index).idPedido;
            	DBManager db = new DBManager(CongeladosActivity.this);
            	db.open();
            	db.deletePedido(idPedido);
            	db.close();
            	singleton.deleteFromJson(CongeladosActivity.this, idPedido);
            	
            	arrayPedidosCongelados.remove(index);
            	if(adapter != null) adapter.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.cancel();
            }
        });
        alertDialog.show();
	}
	
	private void updateList(){
		showProgressDialog();
		
		if(arrayPedidosCongelados.size()>0) arrayPedidosCongelados.removeAllElements();
		DBManager db = new DBManager(this);
		db.open();
		Vector<PedidoCongelado> congelados = db.getPedidoCongelado();
		db.close();
		
		if(congelados != null && congelados.size()>0){
			for(int i=0;i<congelados.size(); i++) arrayPedidosCongelados.add(congelados.get(i));
		}
		
		if(adapter != null) adapter.notifyDataSetChanged();
		
		hideProgressDialog();
	}
	
}
