package com.deploy.fi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.deploy.fi.adapters.SucursalArrayAdapter;
import com.deploy.fi.fragments.BuscarClienteFragment;
import com.deploy.fi.services.IDataServiceResponse;
import com.deploy.fi.services.IServiceResponse;
import com.deploy.fi.services.JSON;
import com.deploy.fi.services.ServiceError;
import com.deploy.fi.services.Services;
import com.deploy.fi.services.queries.QueryListClients;
import com.deploy.fi.services.queries.QueryLogin;
import com.deploy.fi.services.responses.DataListClientsResponse;
import com.deploy.fi.services.struct.Sucursal;
import com.deploy.fi.services.struct.old.Cliente;
import com.deploy.fi.tools.DBManager;

public class BuscarClienteActivity extends ProgressFragmentActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_cliente);
        GUI(savedInstanceState);
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideProgressDialog();
		if(services != null) services.cancel(true);
		services = null;
	}
	
	private Services services = null; 
	
	private List<Sucursal> listaSucursales = new ArrayList<Sucursal>();
	
	private DrawerLayout mDrawerLayout = null;
	private ListView mDrawerList = null;
	
	private SucursalArrayAdapter adapter = null;
	
	private ActionBarDrawerToggle mDrawerToggle = null;
	private CharSequence mDrawerTitle = null;
	
	private void GUI(Bundle savedInstanceState){
		mDrawerTitle = getTitle();
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.listClientes);
		
		adapter = new SucursalArrayAdapter(this, R.layout.list_item, listaSucursales);
		mDrawerList.setAdapter(adapter);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Sucursal sucursal = BuscarClienteActivity.this.adapter.getItem(position);
				if(sucursal != null){
					mDrawerLayout.closeDrawer(mDrawerList);
					
					Intent i = new Intent(BuscarClienteActivity.this, VerClienteActivity.class);
					i.putExtra("sucursal", sucursal);
					startActivity(i);
				}
			}
		});
		
		initQuery(savedInstanceState);	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buscar_cliente, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new BuscarClienteFragment();
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle("Lista Clientes");
			mDrawerLayout.closeDrawer(mDrawerList);
			
			
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
	
	private void initQuery(Bundle savedInstanceState){
		//showProgressDialog();
		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		showProgressDialog();
		hideProgressDialog();
		
		/*DBManager db = new DBManager(this);
		db.open();
		Vector<Sucursal> sucursal = db.getSucursales();
		if(sucursal != null && sucursal.size()>0){
			for(int i=0; i<sucursal.size(); i++) listaSucursales.add(sucursal.get(i));
		} 
		db.close();
		
		hideProgressDialog();
		
		if(sucursal != null && sucursal.size()>0) Toast.makeText(this, R.string.toast_search_client_activity, Toast.LENGTH_LONG).show();*/
	}
	
	private void successService(String msg, String[] data){
    	Log.e("successService", msg);
    	if(listaSucursales != null && listaSucursales.size()>0) listaSucursales.clear();
    	if(data != null && data.length>0){
    		for(int i=0; i<data.length; i++){
    			Hashtable<Object,Object> row = JSON.decodeHashtable(data[i]);
    			if(row != null && row.size()>0){
    				Sucursal sucursal = Sucursal.getInstance(row);
    				if(sucursal != null) listaSucursales.add(sucursal);
    			}
    		}
    	}
    	if(adapter != null && listaSucursales != null) adapter.notifyDataSetChanged();
    	hideProgressDialog();
    	Toast.makeText(this, R.string.toast_search_client_activity, Toast.LENGTH_LONG).show();
    	//mDrawerLayout.openDrawer(mDrawerList);
    	
    	//ojo los hptas cajones no estan abriendo
    }
	
	private void errorService(String msg){
		if(msg != null && msg.length()>0){
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			Log.e("errorService", msg);
    	}
    	hideProgressDialog();
    }
	
	//ojo que la sincronizacion es esto
	public void buscarClientes(Hashtable<String, String> params/*Cliente cliente*/){
		//ojo esto se debe hacer local no conectado al server
		if(params != null && params.size()>0){
			showProgressDialog();
			
			//NOMBRE, NIT, RAZON_SOCIAL, CIUDAD, ALMACEN, VENTAS_DESDE, VENTAS_HASTA
			
			DBManager db = new DBManager(this);
			db.open();
			Vector<Sucursal> sucursal = db.getSucursales(params);
			db.close();
			
			if(sucursal != null && sucursal.size()>0){
				if(listaSucursales.size()>0) listaSucursales.clear();
				for(int i=0; i<sucursal.size(); i++) listaSucursales.add(sucursal.get(i));
				if(adapter != null) adapter.notifyDataSetChanged();
				Toast.makeText(this, R.string.toast_search_client_activity, Toast.LENGTH_LONG).show();
			} 
			else {
				if(adapter != null) adapter.notifyDataSetChanged();
				Toast.makeText(this, "No se han encontrado sucursales con estos criterios", Toast.LENGTH_LONG).show();
			}
		}
		
		hideProgressDialog();
		//nic 1010
	}
	
}
