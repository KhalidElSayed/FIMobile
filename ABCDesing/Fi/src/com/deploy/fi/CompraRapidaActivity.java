package com.deploy.fi;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deploy.fi.adapters.CompraRapidaAdapter;
import com.deploy.fi.services.IDataServiceResponse;
import com.deploy.fi.services.IServiceResponse;
import com.deploy.fi.services.JSON;
import com.deploy.fi.services.ServiceError;
import com.deploy.fi.services.Services;
import com.deploy.fi.services.queries.QueryLogin;
import com.deploy.fi.services.queries.QuerySynchUpload;
import com.deploy.fi.services.responses.DataSynchUpload;
import com.deploy.fi.services.struct.CompraRapidaItem;
import com.deploy.fi.services.struct.CompraRapidaSingleton;
import com.deploy.fi.services.struct.DetallePedidoCongelado;
import com.deploy.fi.services.struct.ICompraRapidaItem;
import com.deploy.fi.services.struct.PedidoCongelado;
import com.deploy.fi.services.struct.SkuColor;
import com.deploy.fi.services.struct.SkuPrecio;
import com.deploy.fi.services.struct.SkuReferencia;
import com.deploy.fi.services.struct.Sucursal;
import com.deploy.fi.tools.DBManager;
import com.deploy.fi.tools.SQLiteTools;
import com.deploy.fi.tools.SingletonConfig;

public class CompraRapidaActivity extends ProgressActivity {

	private static SingletonConfig config = SingletonConfig.getInstance();
	
	private PedidoCongelado pedido = null;
	private Sucursal sucursal = null;
	
	//SI ENCUENTRA REFERENCIA
	//SELECT sku.idsku, sku.Referencia, sku.Descsku FROM sku WHERE Referencia ='10101' GROUP BY sku.Referencia
	
	// para llenar los campos de color
	//SELECT sku.Referencia, sku.Talla, colores.idColor, colores.DescColor, sku.idsku FROM sku JOIN colores On colores.idColor = sku.idColor WHERE sku.Referencia = '10101' ORDER BY colores.idColor
	
	//ojo nvio funciona bien cuando se envia
	
	/*
	 * 783155 ref
	 * 055 blanco estampado
	 * 36=0, 32=1, 34=3
	 * 
	 * 10477 ref
	 * 025 blanco
	 * 10=4, 06=5, 08=6; 14=7; 12=8
	 */
	
	private String[] indiceTallas = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compra_rapida);
		
		showProgressDialog();
		if(singleton.size()>0) singleton.removeAll();
		Bundle extras = getIntent().getExtras(); 
		
		DBManager db = new DBManager(this);
		db.open();
		indiceTallas = db.getIndiceTalla();
		db.close();
		
		if(extras != null){
			if (extras.containsKey("sucursal")) sucursal = (Sucursal) extras.getSerializable("sucursal");
			if (extras.containsKey("pedido")) pedido = (PedidoCongelado) extras.getSerializable("pedido");
		}
		
		if(pedido != null) querySucursal();
		if(sucursal != null) GUI();
		hideProgressDialog();
	}
	
	private Services services = null;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(singleton.size()>0) singleton.removeAll();
		if(services != null) services.cancel(true);
		services = null;
	}
	
	private void GUI(){
		TextView txtRazonSocial = (TextView) findViewById(R.id.txtCPRazonSocial);
		txtRazonSocial.setText("NombreAlmacen: " + sucursal.getNombreAlmacen());
		
		TextView txtNit = (TextView) findViewById(R.id.txtCPNit);
		txtNit.setText("NIT: " + sucursal.getNIT());
		
		TextView txtNombre = (TextView) findViewById(R.id.txtCPNombre);
		txtNombre.setText("???");
		
		TextView txtTelefono = (TextView) findViewById(R.id.txtCPAlmacen);
		txtTelefono.setText("Telefono: " + sucursal.getTelefono());
		
		TextView txtCelular = (TextView) findViewById(R.id.txtCPCelular);
		txtCelular.setText("Celular: " + sucursal.getTelefono());
		
		TextView txtCorreo = (TextView) findViewById(R.id.txtCPCorreo);
		txtCorreo.setText("Email: " + sucursal.getEmail());
		
		TextView txtDireccion = (TextView) findViewById(R.id.txtCPDireccion);
		txtDireccion.setText("Direccion: " + sucursal.getDireccion1());
		
		final TextView txtCPHasta = (TextView) findViewById(R.id.txtCPHasta);
		txtCPHasta.setText("Ciudad: " + sucursal.getCiudad());
		
		/*txtCPHasta.setText("Hasta: ???");
		txtCPHasta.setTextColor(Color.RED);
		txtCPHasta.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				createDateDialog(txtCPHasta);
			}
		});*/
		
		TextView txtDepartamento = (TextView) findViewById(R.id.txtCPDepartamento);
		txtDepartamento.setText("Departamento: " + "???");
		
		TextView txtPais = (TextView) findViewById(R.id.txtCPPais);
		txtPais.setText("Pais: " + "???");
		
		TextView txtTotalUnidades = (TextView) findViewById(R.id.txtCPTotalUnidades);
		txtTotalUnidades.setText("0");
		
		TextView txtTotalPrecio = (TextView) findViewById(R.id.txtCPTotal);
		txtTotalPrecio.setText("0");
		
		TextView txtGrandTotal = (TextView) findViewById(R.id.txtCPGrandTotal);
		txtGrandTotal.setText("0");
		
		TextView txtIva = (TextView) findViewById(R.id.txtCPIva);
		txtIva.setText("0");
		
		Button btnCompraRapidaCongelar = (Button) findViewById(R.id.btnCompraRapidaCongelar);
		btnCompraRapidaCongelar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(CompraRapidaActivity.this);
				alertDialog.setTitle("Confirmacion Congelar Pedido...");
				alertDialog.setMessage("Esta seguro de congelar el pedido?");
				alertDialog.setIcon(R.drawable.salir);
				alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	Congelar();
		            }
		        });
		        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.cancel();
		            }
		        });
		        alertDialog.show();
			}
		});
		
		Button btnGuardarEnviar = (Button) findViewById(R.id.btnCompraRapidaGuardarEnviar);
		btnGuardarEnviar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(CompraRapidaActivity.this);
				alertDialog.setTitle("Confirmacion Envio Pedido...");
				alertDialog.setMessage("Esta seguro de enviar el pedido?");
				alertDialog.setIcon(R.drawable.salir);
				alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	GuardarEnviar();
		            }
		        });
		        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.cancel();
		            }
		        });
		        alertDialog.show();
			}
		});
		
		Button btnCongelarCompraRapida = (Button) findViewById(R.id.btnCompraRapidaCP);
		btnCongelarCompraRapida.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addRowProducto();
			}
		});
		
		final View activityRootView = findViewById(R.id.full_layout);
		final View layoutBottom = findViewById(R.id.layout_bottom);
		
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		  @Override
		  public void onGlobalLayout() {
			  int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
		        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
		        	layoutBottom.setVisibility(LinearLayout.GONE);
		        }
		        else{
		        	layoutBottom.setVisibility(LinearLayout.VISIBLE);
		        }
		  }
		});
		
		ICompraRapidaItem instance = new ICompraRapidaItem(){
			@Override
			public void focusColor(int indexItem){
				hideProgressDialog();
				CompraRapidaItem item = singleton.getItem(indexItem);
				createSelectorDialog(item, indexItem);
			}
			@Override
			public void removeFila(int indexItem){
				singleton.removeItem(indexItem);
				updateAdapter();
				hideProgressDialog();
			}
			@Override
			public void updateTalla(String tallaName, int value, int indexItem) {
				CompraRapidaItem item = singleton.getItem(indexItem);
				if(item != null){
					item.setCountTalla(tallaName, value);
					//item.setTalla(type, value);
					singleton.updateItem(indexItem, item);
					updateAdapter();
				}
				hideProgressDialog();
			}
			@Override
			public void updateReferencia(String value, int indexItem) {
				CompraRapidaItem item = singleton.getItem(indexItem);
				queryReferencia(item, value, indexItem);
				hideProgressDialog();
			}
			@Override
			public void updateCodigoColor(String value, int indexItem) {
				CompraRapidaItem item = singleton.getItem(indexItem);
				if(item != null){
					item.setSelectColores(value);
					singleton.updateItem(indexItem, item);
					updateAdapter();
				}
				hideProgressDialog();
			}
			@Override
			public void updateColor(String value, int indexItem) {
				CompraRapidaItem item = singleton.getItem(indexItem);
				if(item != null){
					item.setSelectColores(value);
					singleton.updateItem(indexItem, item);
					updateAdapter();
				}
				hideProgressDialog();
			}
		};
		
		ListView tableListaProductos = (ListView) findViewById(R.id.tableListaProductos);
		adapter = new CompraRapidaAdapter(this, R.layout.adapter_compra_rapida, singleton.getArray());
		adapter.indiceTallas = indiceTallas;
		adapter.iCompraRapidaItem = instance;
		tableListaProductos.setAdapter(adapter);
		
		if(sucursal != null && pedido == null) addRowProducto();
		
	}
	
	private static CompraRapidaSingleton singleton = CompraRapidaSingleton.getInstance();
	
	private CompraRapidaAdapter adapter = null;
	
	private int[] getTotalCurrent(){
		int totalSum = 0;
		int totalCount = 0;
		if(singleton != null && singleton.size()>0){
			for(int i=0; i<singleton.size(); i++){
				CompraRapidaItem item = singleton.getItem(i);
				if(item != null){
					String[] aviableTalla = item.getAviableTalla();
					if(aviableTalla != null && aviableTalla.length>0){
						for(int at=0; at<aviableTalla.length; at++){
							int count = item.getCountTalla(aviableTalla[at]);
							int sum = 0;
							if(count>0) sum = item.getPrecioTalla(aviableTalla[at]) * count;
							if(sum > 0) {
								totalCount += count;
								totalSum += sum;
							}
						}
					}
				}
			}
		}
		return new int[]{totalSum, totalCount};
	}
	
	private CompraRapidaItem createItemWithReferencia(String referencia, String selectCodColor, String selectColor){
		CompraRapidaItem item = new CompraRapidaItem();
		item.setReferencia(referencia);
		
		Vector<SkuPrecio> skuPrecios = null;
		item.setSkuPrecio(null);
		
		DBManager db = new DBManager(this);
		db.open();
		int idListaPrecio = db.getIdListaPrecio(sucursal.getIdSucursal());
		if(idListaPrecio != -1){
			skuPrecios = db.getListaPrecio(sucursal.getIdSucursal(), idListaPrecio, referencia);
		}
		db.close();
		
		if(skuPrecios != null && skuPrecios.size()>0){
			db = new DBManager(this);
			db.open();
			Vector<SkuColor> arrayColores = db.getSkuColores(referencia);
			db.close();
			if(arrayColores != null && arrayColores.size()>0){
				if(arrayColores != null && arrayColores.size()>0){
					String[] ID_COLOR = new String[arrayColores.size()];
					String[] TALLAS = new String[arrayColores.size()];
					String[] COLOR = new String[arrayColores.size()];
					int[] PRECIOS = new int[arrayColores.size()];
					int[] SKU = new int[arrayColores.size()];
					
					for(int i=0; i<arrayColores.size(); i++){
						COLOR[i] = arrayColores.get(i).getDescripcionColor();
						ID_COLOR[i] = arrayColores.get(i).getIDColor();
						TALLAS[i] = arrayColores.get(i).getTalla();
						SKU[i] = arrayColores.get(i).getIDSku();
					}
					
					for(int i=0; i<SKU.length; i++){
						for(int p=0; p<skuPrecios.size(); p++){
							if(SKU[i] == skuPrecios.get(p).idsku){
								PRECIOS[i] = skuPrecios.get(p).precio;
								break;
							}
						}
					}
					
					item.setSkuPrecio(PRECIOS);
					item.setSkuID(SKU);
					
					item.setCodigosColor(ID_COLOR);
					if(selectCodColor == null || selectCodColor.length() == 0) item.setSelectCodigosColor("...");
					else if(selectCodColor == null || selectCodColor.length() == 0) item.setSelectCodigosColor(selectCodColor);
					
					item.setColores(COLOR);
					if(selectCodColor == null || selectCodColor.length() == 0) item.setSelectColores("...");
					else if(selectCodColor == null || selectCodColor.length() == 0) item.setSelectColores(selectCodColor);
					
					item.setTallas(TALLAS);
					
				}
			}
		}
		else {
			Toast.makeText(getApplicationContext(),"Esta referencia no contiene lista de precios", Toast.LENGTH_SHORT).show();
			hideProgressDialog();
		}
		
		return item;
	}
	
	private int ID_SUCURSAL = -1;
	private int ID_PEDIDO = -1;
	
	private void querySucursal(){
		int idSucursal = ID_SUCURSAL = pedido.idSucursal;
		int idPedido = ID_PEDIDO = pedido.idPedido;
		
		DBManager db = new DBManager(this);
		db.open();
		sucursal = db.getSucursal(idSucursal);
		db.close();
		
		singleton.loadFromJson(this, idPedido);
		
		/*db = new DBManager(this);
		db.open();
		Vector<DetallePedidoCongelado> detalles = db.getDetallePedidoCongelado(idPedido);
		db.close();
		
		if(detalles != null && detalles.size()>0){
			Hashtable<String, Object> group = groupByReference(detalles);
			if(group != null && group.size()>0){
				Enumeration keys = group.keys();
				while(keys.hasMoreElements()){
					String referencia = keys.nextElement().toString();
					Vector<Hashtable> listReferencia = (Vector<Hashtable>)group.get(referencia);
					Hashtable<String, Object> listColor = groupByColor(listReferencia);
					if(listColor != null && listColor.size()>0){
						Enumeration eColores = listColor.keys();
						while(eColores.hasMoreElements()){
							String codColor = eColores.nextElement().toString();
							Vector<Hashtable> arrayColor = (Vector<Hashtable>)listColor.get(codColor);
							if(arrayColor != null && arrayColor.size()>0){
								CompraRapidaItem item = createItemWithReferencia(referencia, null, null);
								for(int i=0; i<arrayColor.size(); i++){
									Hashtable dataItem = listReferencia.get(i);
									String talla = dataItem.get("talla").toString();
									String color = dataItem.get("color").toString();
									String idColor = dataItem.get("idColor").toString();
									int cantidad = Integer.parseInt(dataItem.get("cantidad").toString());
									
									item.setSelectCodigosColor(idColor);
									item.setSelectColores(color);
									if(!item.isAviableTalla(talla)) item.addAviableTalla(talla);
									cantidad = item.getCountTalla(talla) + cantidad;
									item.setCountTalla(talla, cantidad);
								}
								if(singleton != null && item != null) singleton.addItem(item);
							}
						}
					}
				}
			}
		}*/
		
		if(adapter != null) adapter.notifyDataSetChanged(); 
		
		hideProgressDialog();
	}
	
	private Hashtable<String, Object> groupByColor(Vector<Hashtable> listReferencia){
		Hashtable<String, Object> groupByColor = new Hashtable<String, Object>();
		if(listReferencia != null && listReferencia.size()>0){
			for(int i=0; i<listReferencia.size(); i++){
				Hashtable dataItem = listReferencia.get(i);
				if(dataItem != null && dataItem.size()>0){
					String color = dataItem.get("color").toString();
					if(!groupByColor.containsKey(color)) groupByColor.put(color, new Vector<Hashtable>());
					Vector<Hashtable> arrayColor = (Vector<Hashtable>)groupByColor.get(color);
					arrayColor.add(dataItem);
					groupByColor.put(color, arrayColor);
				}
			}
		}
		return groupByColor;
	}
	
	private Hashtable<String, Object> groupByReference(Vector<DetallePedidoCongelado> detalles){
		Hashtable<String, Object> group = new Hashtable<String, Object>();
		for(int i=0; i<detalles.size(); i++){
			DetallePedidoCongelado item = detalles.get(i);
			
			DBManager db = new DBManager(this);
			db.open();
			Object[] referenciaColor = db.getSkuReferencia(item.idSku);//ojo debe devolver el nombre del color
			db.close();
			
			if(referenciaColor != null && referenciaColor.length == 4){
				String idColor = referenciaColor[1].toString();
				String referencia = referenciaColor[0].toString();
				String color = referenciaColor[2].toString();
				String talla = referenciaColor[3].toString();
				
				if(!group.containsKey(referencia)) group.put(referencia, new Vector<Hashtable>());
				
				Vector<Hashtable> list = (Vector<Hashtable>)group.get(referencia);
				
				Hashtable<String, Object> tabla = new Hashtable<String, Object>();
				tabla.put("idDetallePedido", item.idDetallePedido);
				tabla.put("referencia", referencia);
				tabla.put("cantidad", item.cantidad);
				tabla.put("idSku", item.idSku);
				tabla.put("idColor", idColor);
				tabla.put("color", color);
				tabla.put("talla", talla);
				list.add(tabla);
				
				group.put(referencia, list);
			}
		}
		return group;
	}
	
	private CompraRapidaItem parseDetallePedidoCongelado(DetallePedidoCongelado item){
		CompraRapidaItem result = null;
		if(item != null){
			
		}
		return result;
	}
	
	private void queryReferencia(CompraRapidaItem item, String value, int indexItem){
		if(item != null && value != null && value.length()>0) {
			value = value.trim();
			if(value != null && value.length()>0) {
				item.removeAviableTalla();
				//for(int TALLA = CompraRapidaItem.TYPE_S; TALLA<=CompraRapidaItem.TYPE_UNICA; TALLA++) item.setVisibleTalla(false, TALLA);
				DBManager db = new DBManager(CompraRapidaActivity.this);
				db.open();
				
				Vector<SkuPrecio> skuPrecios = null;
				item.setSkuPrecio(null);
				
				Vector<SkuReferencia> arrayReferencia = db.getSkuReferencia(value);
				db.close();
				
				item.setCodigosColor(null);
				item.setSelectCodigosColor(null);
				
				item.setColores(null);
				item.setSelectColores(null);
				item.setSkuPrecio(null);
				item.setTallas(null);
				
				db = new DBManager(this);
				db.open();
				int idListaPrecio = db.getIdListaPrecio(sucursal.getIdSucursal());
				if(idListaPrecio != -1){
					skuPrecios = db.getListaPrecio(sucursal.getIdSucursal(), idListaPrecio, value);
				}
				db.close();
				
				if(skuPrecios != null && skuPrecios.size()>0){ 
					if(arrayReferencia != null && arrayReferencia.size()>0){
						item.setReferencia(arrayReferencia.get(0).getReferencia());
						
						db = new DBManager(CompraRapidaActivity.this);
						db.open();
						Vector<SkuColor> arrayColores = db.getSkuColores(item.getReferencia());
						db.close();
						
						if(arrayColores != null && arrayColores.size()>0){
							String[] ID_COLOR = new String[arrayColores.size()];
							String[] TALLAS = new String[arrayColores.size()];
							String[] COLOR = new String[arrayColores.size()];
							int[] PRECIOS = new int[arrayColores.size()];
							int[] SKU = new int[arrayColores.size()];
							
							for(int i=0; i<arrayColores.size(); i++){
								COLOR[i] = arrayColores.get(i).getDescripcionColor();
								ID_COLOR[i] = arrayColores.get(i).getIDColor();
								TALLAS[i] = arrayColores.get(i).getTalla();
								SKU[i] = arrayColores.get(i).getIDSku();
							}
							
							for(int i=0; i<SKU.length; i++){
								for(int p=0; p<skuPrecios.size(); p++){
									if(SKU[i] == skuPrecios.get(p).idsku){
										PRECIOS[i] = skuPrecios.get(p).precio;
										break;
									}
								}
							}
							
							item.setSkuPrecio(PRECIOS);
							item.setSkuID(SKU);
							
							item.setCodigosColor(ID_COLOR);
							item.setSelectCodigosColor("...");
							
							item.setColores(COLOR);
							item.setSelectColores("...");
							
							item.setTallas(TALLAS);
							
						}
					}
				}
				else {
					Toast.makeText(getApplicationContext(),"Esta referencia no contiene lista de precios", Toast.LENGTH_SHORT).show();
					hideProgressDialog();
				}
			}
		}
		
		if(item != null) singleton.updateItem(indexItem, item);
		updateAdapter();
	}
	
	private void updateAdapter(){
		if(adapter != null) adapter.notifyDataSetChanged();
		int[] total = getTotalCurrent();
		
		double iva = 0, granTotal = 0;
		if(total[0] > 0)  {
			iva = (double)total[0] / (double)100; 
			iva = (iva * (double)16);
			granTotal = total[0] + iva;
			
			iva = Math.round(iva * 100);
			iva = iva/100;
			
			granTotal = Math.round(granTotal * 100);
			granTotal = granTotal/100;
		}
		
		TextView txtTotalUnidades = (TextView) findViewById(R.id.txtCPTotalUnidades);
		txtTotalUnidades.setText("$ " + LoginActivity.formatNumber(total[1]));
		
		TextView txtTotalPrecio = (TextView) findViewById(R.id.txtCPTotal);
		txtTotalPrecio.setText("$ " + LoginActivity.formatNumber(total[0]));
		
		TextView txtGrandTotal = (TextView) findViewById(R.id.txtCPGrandTotal);
		txtGrandTotal.setText("$ " + LoginActivity.formatNumber(granTotal));

		TextView txtIva = (TextView) findViewById(R.id.txtCPIva);
		txtIva.setText("$ " + LoginActivity.formatNumber(iva));
	}
	
	private Dialog createTemplateDialog(){
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		return dialog;
	}
	
	private void createDateDialog(final TextView textView){
		final Dialog dialog = createTemplateDialog();
		dialog.setContentView(R.layout.dialog_date_select);
		
		final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
		
		Button buttonSelect = (Button) dialog.findViewById(R.id.buttonCancel);
		buttonSelect.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int month = datePicker.getMonth() + 1;
				int day = datePicker.getDayOfMonth();
				int year = datePicker.getYear();
				String date = year + "/" + month + "/" + day;
				textView.setText(date);
				textView.setTextColor(Color.BLACK);
				dialog.cancel();
			}
		});
		
		Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		
		dialog.show();
		
	}
	
	private void createSelectorDialog(final CompraRapidaItem item, final int indexItem){
		final Dialog dialog = createTemplateDialog();
		dialog.setContentView(R.layout.dialog_compra_rapida_select);

		ListView listViewSelector = (ListView) dialog.findViewById(R.id.listViewSelector);
		
		final Vector<String> arraySelect = new Vector<String>();
		for(int i=0; i<item.getColores().length; i++){
			String id = item.getCodigosColor()[i] + " - " + item.getColores()[i];
			if(!arraySelect.contains(id)) arraySelect.add(id);
		}
		
		ArrayAdapter adapterColors = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arraySelect);
		listViewSelector.setAdapter(adapterColors);
		listViewSelector.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String selected = arraySelect.get(position);
				String[] lines = selected.split(" - ");
				String selectedCod = lines[0];
				String selectedColor = lines[1];
				item.setSelectCodigosColor(selectedCod);
				item.setSelectColores(selectedColor);
				
				//for(int TALLA = CompraRapidaItem.TYPE_S; TALLA<=CompraRapidaItem.TYPE_UNICA; TALLA++) item.setVisibleTalla(false, TALLA);
				item.removeAviableTalla();
				singleton.updateItem(indexItem, item);
				
				Vector<String> tallas = item.getFiltredTallas();
				if(tallas != null && tallas.size()>0){
					for(int i=0; i<tallas.size(); i++){
						item.addAviableTalla(tallas.get(i));
						/*int talla = CompraRapidaItem.parseTalla(tallas.get(i));
						item.setVisibleTalla(true, talla);*/
					}
				}
				singleton.updateItem(indexItem, item);
				updateAdapter();
				dialog.cancel();
			}
		});
		
		Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		
		dialog.show();
	}
	
	private Hashtable<String, Object>[] getData(){
		Hashtable<String, Object>[] tableTallas = null;
		int count = singleton.size();
		if(count>0){
			tableTallas = new Hashtable[count];
			boolean checkTallas = true;
			for(int i=0; i<count; i++){
				tableTallas[i] = new Hashtable<String, Object>();
				CompraRapidaItem item = singleton.getItem(i);
				String Referencia = item.getReferencia();
				int[] skuID = item.getSkuID();
				int[] skuPRECIO = item.getSkuPrecio();
				if(Referencia != null && Referencia.length()>0 && !Referencia.equals("...") && Referencia != null && skuID != null && skuID.length>0){
					tableTallas[i].put("referencia", Referencia);
					Vector<String> tallas = item.getFiltredTallas();
					if(checkTallas && tallas != null && tallas.size()>0){
						for(int t=0; t<tallas.size(); t++){
							String talla = tallas.get(t);
							int sku = skuID[t];
							int precio = skuPRECIO[t];
							boolean visible = item.isVisbleTalla(talla);
							int totalTalla = item.getCountTalla(talla);
							
							Log.e("Get Singleton", talla + "->" + totalTalla);
							
							/*int typeTalla = CompraRapidaItem.parseTalla(talla);
							boolean visible = item.getVisibleTalla(typeTalla);
							int totalTalla = item.getTalla(typeTalla);*/
							if(checkTallas && visible && totalTalla >= 0) {
								Hashtable<String, String> skuNode = new Hashtable<String, String>();
								skuNode.put("count", String.valueOf(totalTalla));
								skuNode.put("precio", String.valueOf(precio));
								skuNode.put("sku", String.valueOf(sku));
								skuNode.put("talla", talla);
								tableTallas[i].put(talla, skuNode);
							}
							else{
								if(pedido == null){
									checkTallas = false;//sku 189674, talla 42
									break;
								}
								else{
									DBManager db = new DBManager(this);
									db.open();
									Vector<DetallePedidoCongelado> detalles = db.getDetallePedidoCongelado(pedido.idPedido);
									db.close();//189674
									
									if(detalles != null && detalles.size()>0){
										for(int ii= 0; ii<detalles.size(); ii++){
											DetallePedidoCongelado detalle = detalles.get(ii);
											if(detalle.idSku == sku){
												checkTallas = false;//sku 189674, talla 42
												break;
											}
										}
									}
									else{
										checkTallas = false;//sku 189674, talla 42
										break;
									}
								}
							}
						}
					}
					else checkTallas = false;
				}
			}
			if(!checkTallas) tableTallas = null;
		}
		return tableTallas;
	}
	
	private void GuardarEnviar(){
		showProgressDialog();
		int count = singleton.size();
		if(count > 0){
			Hashtable<String, Object>[] data = getData();
			if(data != null && data.length>0){
				int idSucursal = sucursal.getIdSucursal();
				Date currentDate = SQLiteTools.getCurrentDate();
				Date shippingDate = SQLiteTools.addDays(currentDate, 7);
				
				DBManager db = new DBManager(this);
				db.open();
				int pedidoID = db.getPedidoID()+1;
				db.close();
				
				Hashtable<Object,Object> dataPedido = new Hashtable<Object,Object>();
				//dataPedido.put("FechaEntrega", SQLiteTools.toString(shippingDate));
				dataPedido.put("FechaEntrega", "-");
				dataPedido.put("FechaPedido", SQLiteTools.toString(currentDate));
				dataPedido.put("Descripcion", "Mobile");
				dataPedido.put("idSucursal", idSucursal);
				dataPedido.put("idPedido", pedidoID);
				
				Vector<Hashtable<Object,Object>> listaDetalle = new Vector<Hashtable<Object,Object>>(); 
				for(int i=0; i<data.length; i++){
					data[i].remove("referencia");
					Enumeration<String> eTallas = data[i].keys();
					while(eTallas.hasMoreElements()){
						String talla = eTallas.nextElement();
						Hashtable<String, String> detail = (Hashtable)data[i].get(talla);
						if(talla.equals(detail.get("talla"))){
							Hashtable<Object,Object> dataDetallePedido = new Hashtable<Object,Object>();
							dataDetallePedido.put("cantidad", detail.get("count"));
							dataDetallePedido.put("idSku", detail.get("sku"));
							dataDetallePedido.put("idPedido", pedidoID);
							listaDetalle.add(dataDetallePedido);
						}
					}
				}
				
				if(dataPedido.size()>0 && listaDetalle.size()>0){
					initSynchUpload(dataPedido, listaDetalle);
				}
				else {
					Toast.makeText(getApplicationContext(),"Esta compra rapida contiene registros incompletos", Toast.LENGTH_SHORT).show();
					hideProgressDialog();
				}
			}
			else {
				Toast.makeText(getApplicationContext(),"Esta compra rapida contiene registros incompletos", Toast.LENGTH_SHORT).show();
				hideProgressDialog();
			}
		} 
		else {
			Toast.makeText(getApplicationContext(),"Esta compra rapida contiene registros incompletos", Toast.LENGTH_SHORT).show();
			hideProgressDialog();
		}
	}
	
	private void initSynchUpload(Hashtable<Object,Object> dataPedido, Vector<Hashtable<Object,Object>> listaDetalle){
		JSONObject objJSON = new JSONObject();
		
		JSONArray objPedido = new JSONArray();
		try{
			JSONObject objPedido_ = new JSONObject();
			Enumeration keys = dataPedido.keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement().toString();
				String value = dataPedido.get(key).toString();
				if(key.equals("idPedido")) objPedido_.put(key, Integer.parseInt(value));
				else objPedido_.put(key, value);
			}
			objPedido.put(objPedido_);
			objJSON.put("pedido", objPedido);
		}catch(Exception e){
			e.printStackTrace();
			objPedido = new JSONArray();
		}
		
		JSONArray objDetalle = new JSONArray();
		try{
			for(int i=0; i<listaDetalle.size(); i++){
				Hashtable<Object,Object> item = listaDetalle.get(i);
				JSONObject objItem = new JSONObject();
				Enumeration keys = item.keys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement().toString();
					String value = item.get(key).toString();
					if(key.equals("idPedido") || key.equals("cantidad")) objItem.put(key, Integer.parseInt(value));
					else objItem.put(key, value);
				}
				objItem.put("idDetallePedido", i+1);
				objDetalle.put(objItem);
			}
			objJSON.put("detalle_pedido", objDetalle);
		}catch(Exception e){
			e.printStackTrace();
			objDetalle = new JSONArray();
		}
		
		
		try{
			objJSON.put("token", config.getToken());
			String json = objJSON.toString();
			if(json != null && json.length()>3){
				QuerySynchUpload query = new QuerySynchUpload(QueryLogin.TYPE_SYNCH_UPLOAD);
				query.setRawJSON(json);
				services = new Services(CompraRapidaActivity.this, query,
						new IServiceResponse() {
							@Override
							public void successResponse(IDataServiceResponse response) {
								DataSynchUpload rsp = (DataSynchUpload) response;
								int code = rsp.getCode();
								String msg = rsp.getMessage();
								if(code == 1) successService(msg);
								else errorService(msg);
							}

							@Override
							public void errorResponse(ServiceError error) {
								hideProgressDialog();
								errorService(error.getMessage());
							}
						});
				services.execute(new Object[] {});
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void errorService(String msg){
		if(msg != null && msg.length()>0){
    		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    		Log.e("errorService", msg);
    	}
    	hideProgressDialog();
	}
	
	private void successService(String msg){
		if(msg != null && msg.length()>0) Log.e("successService", msg);
		
		DBManager db = new DBManager(this);
		db.open();
		if(ID_SUCURSAL != -1 && ID_PEDIDO  != -1) {
			db.deletePedido(ID_PEDIDO);
			singleton.deleteFromJson(this, ID_PEDIDO);
		}
		db.close();
		db = new DBManager(this);
		db.open();
		db.backupDatabase();
		db.close();
		
		if(singleton.size()>0) singleton.removeAll();
		addRowProducto();
		updateAdapter();
		Toast.makeText(getApplicationContext(), "Pedido enviado correctamente", Toast.LENGTH_LONG).show();
		
    	hideProgressDialog();
	}
	
	private void Congelar(){
		showProgressDialog();
		int count = singleton.size();
		if(count > 0){
			Date currentDate = SQLiteTools.getCurrentDate();
			Date shippingDate = SQLiteTools.addDays(currentDate, 7);
			Hashtable<String, Object>[] data = getData();
			if(data != null && data.length>0){
				int idSucursal = sucursal.getIdSucursal();
				DBManager db = new DBManager(this);
				db.open();
				int pedidoID = db.getPedidoID();
				if(ID_SUCURSAL != -1 && ID_PEDIDO  != -1) {
					db.deletePedido(ID_PEDIDO);
					singleton.deleteFromJson(this, ID_PEDIDO);
				}
				db.close();
				
				Hashtable<Object,Object> dataPedido = new Hashtable<Object,Object>();
				dataPedido.put("FechaEntrega", SQLiteTools.parseDate(shippingDate));//ojo el campo esta mal escrito
				dataPedido.put("FechaPedido", SQLiteTools.parseDate(currentDate));
				dataPedido.put("Descripcion", "Congelado");
				dataPedido.put("idSucursal", idSucursal);
				dataPedido.put("estado", "congelado");
				dataPedido.put("idPedido", pedidoID);
				
				String json = JSON.encodeHashtable(dataPedido);
				Log.e("json head", json);
				
				db = new DBManager(this);
				db.open();
				db.insertPedido(dataPedido);
				db.close();
				singleton.saveToJson(this, pedidoID);
				
				dataPedido = null;
				
				//ojo esto inserta los datos pero cuando se leen salen los que  no son
				/*for(int i=0; i<data.length; i++){
					data[i].remove("referencia");
					Enumeration<String> eTallas = data[i].keys();
					while(eTallas.hasMoreElements()){
						String talla = eTallas.nextElement();
						Hashtable<String, String> detail = (Hashtable)data[i].get(talla);
						if(talla.equals(detail.get("talla"))){
							Hashtable<Object,Object> dataDetallePedido = new Hashtable<Object,Object>();
							dataDetallePedido.put("cantidad", detail.get("count"));
							dataDetallePedido.put("idSku", detail.get("sku"));
							dataDetallePedido.put("idPedido", pedidoID);
							dataDetallePedido.put("estado", "congelado");
							
							json = JSON.encodeHashtable(dataDetallePedido);
							Log.e("json item", json);
							
							db = new DBManager(this);
							db.open();
							dataDetallePedido.put("idDetallePedido", db.getDetallePedidoID());
							db.insertDetallePedido(dataDetallePedido);
							db.close();
							
							json = JSON.encodeHashtable(dataDetallePedido);
							Log.e("json item", json);
						}
					}
				}*/
				
				db = new DBManager(this);
				db.open();
				db.backupDatabase();
				db.close();
				
				//idPedido, Descripcion, FechaEntrega, FechaPedido, idSucursal -> pedido
				//idDetallePedido, idSku, Cantidad -> detalle_pedido
				
				Toast.makeText(getApplicationContext(),"Este pedido se ha congelado", Toast.LENGTH_SHORT).show();
				singleton.removeAll();
				addRowProducto();
				updateAdapter();
			}
			else Toast.makeText(getApplicationContext(),"Esta compra rapida contiene registros incompletos", Toast.LENGTH_SHORT).show();
		} 
		else Toast.makeText(getApplicationContext(),"Esta compra rapida contiene registros incompletos", Toast.LENGTH_SHORT).show();
		
		hideProgressDialog();
	}
	
	private void addRowProducto(){
		singleton.addItem(new CompraRapidaItem());
		updateAdapter();
	}
	
	//String[] labels = new String[]{"Accion","Referencia","Cod. Color","Color"};	
}
