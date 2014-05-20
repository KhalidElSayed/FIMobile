package com.deploy.fi.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.deploy.fi.services.struct.DetallePedidoCongelado;
import com.deploy.fi.services.struct.PedidoCongelado;
import com.deploy.fi.services.struct.SkuColor;
import com.deploy.fi.services.struct.SkuPrecio;
import com.deploy.fi.services.struct.SkuReferencia;
import com.deploy.fi.services.struct.Sucursal;

public class DBManager extends SQLiteOpenHelper {

	private static String DB_NAME = "databse.sqlite";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	public DBManager(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	private void createDataBase() throws IOException{
		boolean dbExist = checkDataBase();
		if(!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copiando Base de Datos");
			}
		}
	}

	private boolean checkDataBase(){
		SQLiteDatabase checkDB = null;
		try{
			String myPath = myContext.getDatabasePath(DB_NAME).toString();
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){ checkDB = null; }

		if(checkDB != null) checkDB.close();
		return checkDB != null ? true : false;
	}

	private void copyDataBase() throws IOException{
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		File database = myContext.getDatabasePath(DB_NAME);

		OutputStream myOutput = new FileOutputStream(database);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0) myOutput.write(buffer, 0, length);
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void open() throws SQLException{
		try {
			createDataBase();
		} catch (IOException e) {
			throw new Error("Ha sido imposible crear la Base de Datos");
		}
		String myPath = myContext.getDatabasePath(DB_NAME).toString();
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		if(myDataBase == null) Log.e("myDataBase", "is null");
	}

	@Override
	public synchronized void close() {
		if(myDataBase != null) myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void backupDatabase(){
		if(myDataBase != null) {
			try{
				File extStore = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				if(!extStore.exists()) extStore.mkdirs();
				extStore = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DB_NAME);
				if(extStore.exists()) extStore.delete();
				File database = myContext.getDatabasePath(DB_NAME);
				OutputStream os = new FileOutputStream(extStore);
				FileInputStream is = new FileInputStream(database);
				byte[] b = new byte[1024];
				int r;
				while ((r = is.read(b)) != -1) os.write(b, 0, r);
				is.close();
				os.close();			
			}catch(Exception e){
				Log.e("Exception", e.toString());
				e.printStackTrace();
			}
		}
	}

	//*************************************************************************
	
	/*
	 SELECT idListaPrecio FROM listaprecio_sucursales WHERE idSucursal = 5978 
	  
	 SELECT s.idSucursal,s.nombreAlmacen,lps.idListaPrecio,sku.Referencia,lpd.idsku,lpd.Precio
 FROM sucursales s
JOIN listaprecio_sucursales lps ON lps.idSucursal=s.idSucursal 
JOIN listaprecio_detalle lpd ON lpd.idListaPrecio=1
JOIN sku ON sku.idSKU=lpd.idsku WHERE s.idSucursal=5978 and sku.Referencia='10028' 
	 * */
	
	public int getIdListaPrecio(int idSucursal){
		int idListaPrecio = -1;
		if(myDataBase != null) {
			try{
				String query = "SELECT idListaPrecio FROM listaprecio_sucursales WHERE idSucursal = " + idSucursal + " LIMIT 1";
				//String query = "SELECT idListaPrecio FROM listaprecio_sucursales LIMIT 1";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					if(mCursor.moveToNext()) idListaPrecio = mCursor.getInt(0);
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				idListaPrecio = -1;
			}
		}
		return idListaPrecio;
	}
	
	public Vector<SkuPrecio> getListaPrecio(int idSucursal, int idListaPrecio, String Referencia){
		Vector<SkuPrecio> array = new Vector<SkuPrecio>();
		if(myDataBase != null) {
			try{//s.idSucursal,s.nombreAlmacen,lps.idListaPrecio,
				String query = "SELECT lpd.idsku, lpd.Precio ";
				query += "FROM sucursales s ";
				query += "JOIN listaprecio_sucursales lps ON lps.idSucursal=s.idSucursal "; 
				query += "JOIN listaprecio_detalle lpd ON lpd.idListaPrecio="+idListaPrecio+" ";
				query += "JOIN sku ON sku.idSKU=lpd.idsku WHERE s.idSucursal="+idSucursal+" and sku.Referencia='"+Referencia+"'";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()) {
							SkuPrecio item = new SkuPrecio();
							item.idsku = mCursor.getInt(0);
							item.precio = mCursor.getInt(1);
							array.add(item);
						}
					}
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return array;
	}
	
	
	//*************************************************************************
	
	public Vector<PedidoCongelado> getPedidoCongelado(){
		Vector<PedidoCongelado> arrayPedidosCongelados = new Vector<PedidoCongelado>();
		if(myDataBase != null) {
			try{
				String query = "SELECT pedido.idPedido, pedido.FechaEntrega, pedido.FechaPedido, pedido.idSucursal, ";
				query += "sucursales.NombreAlmacen ";//, COUNT() AS TotalItems
				query += "FROM pedido JOIN sucursales ON sucursales.idSucursal = pedido.idSucursal ";
				query += "WHERE (pedido.estado = 'congelado') ";
				
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()){
							PedidoCongelado pc = new PedidoCongelado();
							pc.idPedido = mCursor.getInt(0);
							pc.FechaEntrega = SQLiteTools.parseDate(mCursor.getLong(1));
							pc.FechaPedido = SQLiteTools.parseDate(mCursor.getLong(2));
							pc.idSucursal = mCursor.getInt(3);
							pc.NombreAlmacen = mCursor.getString(4);
							arrayPedidosCongelados.add(pc);
						}
					}
				}
				mCursor.close();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return arrayPedidosCongelados;
	}
	
	//*************************************************************************
	
	public Vector<DetallePedidoCongelado> getDetallePedidoCongelado(int idPedido){
		Vector<DetallePedidoCongelado> arrayDetalles = new Vector<DetallePedidoCongelado>();
		if(myDataBase != null) {
			try{
				String query = "SELECT idDetallePedido, idPedido, idSku, cantidad FROM detalle_pedido WHERE (idPedido="+idPedido+" AND estado = 'congelado')";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()){
							int idDetallePedido = mCursor.getInt(0);
							int idPedido_ = mCursor.getInt(1);
							long idSku = mCursor.getLong(2);
							int cantidad = mCursor.getInt(3);
							
							DetallePedidoCongelado item = new DetallePedidoCongelado();
							item.idDetallePedido = idDetallePedido;
							item.idPedido = idPedido_;
							item.idSku = idSku;
							item.cantidad = cantidad;

							arrayDetalles.add(item);
						}
					}
					mCursor.close();
				}
				mCursor.close();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return arrayDetalles;
	}
	
	public boolean deletePedido(int idPedido){
		boolean result = false;
		if(myDataBase != null) {
			try{
				//String query = "DELETE FROM detalle_pedido WHERE (idPedido="+idPedido+" AND estado = '"+estado+"')";
				String query = "DELETE FROM detalle_pedido WHERE (idPedido="+idPedido+")";
				myDataBase.execSQL(query);
				//query = "DELETE FROM pedido WHERE idPedido="+idPedido+" AND estado = '"+estado+"')";
				query = "DELETE FROM pedido WHERE (idPedido="+idPedido+")";
				myDataBase.execSQL(query);
				result = true;
			}catch(Exception e){
				e.printStackTrace();
				result = false;
			}
		}
		return result;
	}
	
	public int getDetallePedidoID(){
		int idDetallePedido = 0;
		if(myDataBase != null) {
			try{
				String query = "SELECT MAX(idDetallePedido) AS idDetallePedido FROM detalle_pedido LIMIT 1";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()) idDetallePedido = mCursor.getInt(0)+1;
					}
					mCursor.close();
				}
				else idDetallePedido = 0;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				idDetallePedido = 0;
			}
		}
		return idDetallePedido;
	}
	
	public int getPedidoID(){
		int idPedido = 0;
		if(myDataBase != null) {
			try{
				String query = "SELECT MAX(idPedido) AS idPedido FROM pedido LIMIT 1";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()) idPedido = mCursor.getInt(0)+1;
					}
					mCursor.close();
				}
				else idPedido = 0;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				idPedido = 0;
			}
		}
		return idPedido;
	}
	
	/*
	///////////////////////////////////////
	tableName=pedido
	campos:
	idPedido:int
	Descripcion:texto
	FechaEntrega:date
	FechaPedido:date
	idSucursal:int
	 * */
	
	public boolean insertPedido(Hashtable<Object,Object> data){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idPedido", data.get("idPedido").toString());
				values.put("Descripcion", data.get("Descripcion").toString());
				values.put("FechaEntrega", data.get("FechaEntrega").toString());
				values.put("FechaPedido", data.get("FechaPedido").toString());
				values.put("idSucursal", data.get("idSucursal").toString());
				values.put("estado", data.get("estado").toString());
			    myDataBase.insert("pedido", null, values);
			    response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	/*
	 ///////////////////////////////////////
	tableName=detalle_pedido
	idDetallePedido:int
	idPedido:int
	idSku:int
	cantidad:double 
	 
	 * */
	
	public boolean insertDetallePedido(Hashtable<Object,Object> data){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idDetallePedido", data.get("idDetallePedido").toString());
				values.put("idPedido", data.get("idPedido").toString());
				values.put("idSku", data.get("idSku").toString());
				values.put("cantidad", data.get("cantidad").toString());
				values.put("estado", data.get("estado").toString());
			    myDataBase.insert("detalle_pedido", null, values);
			    response = true;
			}catch(Exception e){
				Log.e("Exception", e.toString());
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	//*************************************************************************
	
	public Object[] getSkuReferencia(long idSku){
		Object[] obj = null;
		if(myDataBase != null) {
			try{
				String query = "SELECT sku.Referencia, sku.idColor, colores.DescColor, sku.Talla FROM sku JOIN colores On colores.idColor = sku.idColor WHERE sku.idsku ='"+idSku+"' LIMIT 1";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					if(mCursor.moveToNext()){
						String Referencia = mCursor.getString(0);
						String idColor = mCursor.getString(1);
						String color = mCursor.getString(2);
						String talla = mCursor.getString(3);
						obj = new Object[]{Referencia, idColor, color, talla};
					}
					mCursor.close();
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				obj = null;
			}
		}
		return obj;
	}
	
	public Vector<SkuReferencia> getSkuReferencia(String buscarReferencia){
		Vector<SkuReferencia> array = new Vector<SkuReferencia>();
		if(myDataBase != null) {
			try{
				String query = "SELECT sku.idsku, sku.Referencia, sku.DescSku FROM sku WHERE Referencia ='"+buscarReferencia+"' GROUP BY sku.Referencia";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()){
							Hashtable<Object, Object> table = new Hashtable<Object, Object>(0);
							int idsku = mCursor.getInt(0);
							String Referencia = mCursor.getString(1);
							String DescSku = mCursor.getString(2);

							table.put("idsku", idsku);
							table.put("Referencia", Referencia);
							table.put("DescSku", DescSku);
							
							SkuReferencia skuReferencia = SkuReferencia.getInstance(table);
							table = null;

							if(skuReferencia != null) array.add(skuReferencia);
						}
					}
					mCursor.close();
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return array;
	}
	
	public Vector<SkuColor> getSkuColores(String buscarReferencia){
		Vector<SkuColor> array = new Vector<SkuColor>();
		if(myDataBase != null) {
			try{
				String query = "SELECT sku.Referencia, sku.Talla, colores.idColor, colores.DescColor, sku.idsku FROM sku JOIN colores On colores.idColor = sku.idColor WHERE sku.Referencia = '"+buscarReferencia+"' ORDER BY colores.idColor";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()){
							Hashtable<Object, Object> table = new Hashtable<Object, Object>(0);
							String Referencia = mCursor.getString(0);
							String Talla = mCursor.getString(1);
							String idColor = mCursor.getString(2);
							String DescColor = mCursor.getString(3);
							int idsku = mCursor.getInt(4);

							table.put("Referencia", Referencia);
							table.put("Talla", Talla);
							table.put("idColor", idColor);
							table.put("DescColor", DescColor);
							table.put("idsku", idsku);
							
							SkuColor skuColor = SkuColor.getInstance(table);
							table = null;

							if(skuColor != null) array.add(skuColor);
						}
					}
					mCursor.close();
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return array;
	}
	
	
	
	//*************************************************************************
	
	/*
	lista_precios
	idListaPrecio
	DescListaPrecio
	* */
	public boolean existRecordListaPrecios(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM lista_precios WHERE (";
				query += "idListaPrecio = "+params.get("idListaPrecio")+" AND ";
				query += "DescListaPrecio = '"+params.get("DescListaPrecio")+"' )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	public boolean insertRecordListaPrecios(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idListaPrecio", params.get("idListaPrecio").toString());
				values.put("DescListaPrecio", params.get("DescListaPrecio").toString());
				myDataBase.insert("lista_precios", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	/*
	listaprecio_sucursales
	idListaPrecioSucursal
	idSucursal
	idListaPrecio
	 * */
	public boolean existRecordListaprecioSucursales(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM listaprecio_sucursales WHERE (";
				query += "idListaPrecioSucursal = "+params.get("idListaPrecioSucursal")+" AND ";
				query += "idSucursal = "+params.get("idSucursal")+" AND ";
				query += "idListaPrecio = "+params.get("idListaPrecio")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	public boolean insertRecordListaprecioSucursales(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idListaPrecioSucursal", params.get("idListaPrecioSucursal").toString());
				values.put("idListaPrecio", params.get("idListaPrecio").toString());
				values.put("idSucursal", params.get("idSucursal").toString());
				myDataBase.insert("listaprecio_sucursales", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	/*
	listaprecio_detalle
	idListaPrecioDetalle
	idsku
	idListaPrecio
	precio=double 
	
	{"Precio":"2000","idListaPrecioDetalle":"1","idListaPrecio":"1","idsku":"127786"}
	Precio":"3000","idSku":"240548","idListaPrecio":"53"
	 * */
	public boolean existRecordListaprecioDetalle(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM listaprecio_detalle WHERE (";
				query += "idListaPrecioDetalle = "+params.get("idListaPrecioDetalle")+" AND ";
				query += "idListaPrecio = "+params.get("idListaPrecio")+" AND ";
				query += "idsku = "+params.get("idsku")+" AND ";
				query += "precio = "+params.get("Precio")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	//{Precio=3000, idSku=240548, idListaPrecioDetalle=32800, idListaPrecio=53}
	public boolean insertRecordListaprecioDetalle(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idListaPrecioDetalle", params.get("idListaPrecioDetalle").toString());
				values.put("idListaPrecio", params.get("idListaPrecio").toString());
				values.put("precio", params.get("Precio").toString());
				values.put("idsku", params.get("idsku").toString());
				myDataBase.insert("listaprecio_detalle", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	
	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordMarcas(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM marcas WHERE (";
				query += "idMarca = "+params.get("idMarca")+" AND ";
				query += "DescMarca = '"+params.get("DescMarca")+"' )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a sucursales record
	 * 
	 *
	 * "marcas": [{
        "idMarca": "1",
        "DescMarca": "marca1"
    }
	 * */
	public boolean insertRecordMarcas(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idMarca", params.get("idMarca").toString());
				values.put("DescMarca", params.get("DescMarca").toString());
				myDataBase.insert("marcas", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordColores(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM colores WHERE (";
				query += "idColor = '"+params.get("idColor")+"' AND ";
				query += "DescColor = '"+params.get("DescColor")+"' )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a sucursales record
	 * 
	 *
	 * /*
        	 "colores": [{
        "idColor": "000",
        "DescColor": "surtido"
	 * */
	public boolean insertRecordColores(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idColor", params.get("idColor").toString());
				values.put("DescColor", params.get("DescColor").toString());
				myDataBase.insert("colores", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordTipoPrendas(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM tipo_prendas WHERE (";
				query += "idTipoPrenda = "+params.get("idTipoPrenda")+" AND ";
				query += "DescTipoPrenda = '"+params.get("DescTipoPrenda")+"' )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a sucursales record
	 * 
	 *
	 * /*
    	 tipo_prendas": [{
    "idTipoPrenda": "1",
    "DescTipoPrenda": "tipoPrenda1"
	 * */
	public boolean insertRecordTipoPrendas(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idTipoPrenda", params.get("idTipoPrenda").toString());
				values.put("DescTipoPrenda", params.get("DescTipoPrenda").toString());
				myDataBase.insert("tipo_prendas", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordSku(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM sku WHERE (";
				query += "idsku = "+params.get("idsku")+" AND ";
				query += "Referencia = '"+params.get("Referencia")+"' AND ";
				query += "idMarca = "+params.get("idMarca")+" AND ";
				query += "idColor = '"+params.get("idColor")+"' AND ";
				query += "idTipoPrenda = "+params.get("idTipoPrenda")+" AND ";
				query += "UnidadMedida = '"+params.get("UnidadMedida")+"' AND ";
				query += "DescSku = '"+params.get("DescSku")+"' AND ";
				query += "Talla = '"+params.get("Talla")+"' )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a sucursales record
	 * 
	 *
	 * /*
	 "idsku": "1",
            "Referencia": "10101",
            "idMarca": "1",
            "idColor": "000",
            "idTipoPrenda": "1",
            "UnidadMedida": "uni",
            "DescSku": "descsuku",
            "Talla": "S" 
	 * */
	public boolean insertRecordSku(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idsku", params.get("idsku").toString());
				values.put("Referencia", params.get("Referencia").toString());
				values.put("idMarca", params.get("idMarca").toString());
				values.put("idColor", params.get("idColor").toString());
				values.put("idTipoPrenda", params.get("idTipoPrenda").toString());
				values.put("UnidadMedida", params.get("UnidadMedida").toString());
				values.put("DescSku", params.get("DescSku").toString());
				values.put("Talla", params.get("Talla").toString());
				myDataBase.insert("sku", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/*
	 * 
	 * */
	public Sucursal getSucursal(int idSucursal){
		Sucursal sucursal = null;
		if(myDataBase != null) {
			try{
				String query = "SELECT sucursales.idSucursal, sucursales.codigoSucursal, sucursales.nombreAlmacen, sucursales.Cupo, ";
				query += "sucursales.Direccion1, sucursales.Direccion2, sucursales.Direccion3, sucursales.Telefono, sucursales.Email, ";
				query += "sucursales.idCliente, sucursales.idCiudad, sucursales.idVendedor, sucursales.idCanal, sucursales.idCategoria, ";
				query += "sucursales.idSubCategoria, sucursales.idOrigen, sucursales.idZona, ciudad.Ciudad, zonas.DescZona, clientes.NIT ";
				query += "FROM sucursales JOIN ciudad ON sucursales.idCiudad = ciudad.idCiudad ";
				query += "JOIN zonas ON sucursales.idZona = zonas.idZona ";
				query += "JOIN clientes ON sucursales.idCliente = clientes.idCliente ";
				query += "WHERE sucursales.idSucursal = " + idSucursal + " LIMIT 1";
				
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					if(mCursor.moveToNext()){
						Hashtable<Object, Object> table = new Hashtable<Object, Object>(0);
						int idSucursal_ = mCursor.getInt(0);
						String codigoSucursal = mCursor.getString(1);
						String nombreAlmacen = mCursor.getString(2);
						long Cupo = mCursor.getLong(3);
						String Direccion1 = mCursor.getString(4);
						String Direccion2 = mCursor.getString(5);
						String Direccion3 = mCursor.getString(6);
						String Telefono = mCursor.getString(7);
						String Email = mCursor.getString(8);
						int idCliente = mCursor.getInt(9);
						int idCiudad = mCursor.getInt(10);
						int idVendedor = mCursor.getInt(11);
						int idCanal = mCursor.getInt(12);
						int idCategoria = mCursor.getInt(13);
						int idSubCategoria = mCursor.getInt(14);
						int idOrigen = mCursor.getInt(15);
						int idZona = mCursor.getInt(16);
						String Ciudad = mCursor.getString(17);
						String Zona = mCursor.getString(18);
						String NIT = mCursor.getString(19);

						table.put("idSucursal", idSucursal);
						table.put("codigoSucursal", codigoSucursal);
						table.put("nombreAlmacen", nombreAlmacen);
						table.put("Cupo", Cupo);
						table.put("Direccion1", Direccion1);
						table.put("Direccion2", Direccion2);
						table.put("Direccion3", Direccion3);
						table.put("Telefono", Telefono);
						table.put("Email", Email);
						table.put("idCliente", idCliente);
						table.put("idCiudad", idCiudad);
						table.put("idVendedor", idVendedor);
						table.put("idCanal", idCanal);
						table.put("idCategoria", idCategoria);
						table.put("idSubCategoria", idSubCategoria);
						table.put("idOrigen", idOrigen);
						table.put("idZona", idZona);
						table.put("Ciudad", Ciudad);
						table.put("Zona", Zona);
						table.put("NIT", NIT);

						sucursal = Sucursal.getInstance(table);
						table = null;
					}
					mCursor.close();
				}
				mCursor.close();
				
				
			}catch(Exception e){
				e.printStackTrace();
				sucursal = null;
			}
		}
		return sucursal;
	}
	
	/*
	 * 
	 * */
	public Vector<Sucursal> getSucursales(Hashtable<String, String> params){
		Vector<Sucursal> array = new Vector<Sucursal>();
		if(myDataBase != null) {
			try{
				////NOMBRE, NIT, RAZON_SOCIAL, CIUDAD, ALMACEN, VENTAS_DESDE, VENTAS_HASTA

				//ojo, Se deben especificar los capara que la consulta genere error
				//String query = "SELECT sucursales.*, ciudad.* FROM sucursales JOIN ciudad ON sucursales.idCiudad = ciudad.idCiudad";

				//ECT firstName || ' ' || lastName fullName FROM myTable;

				//if(!params.containsKey("NIT")) params.put("NIT", "");

				String query = "SELECT sucursales.idSucursal, sucursales.codigoSucursal, sucursales.nombreAlmacen, sucursales.Cupo, ";
				query += "sucursales.Direccion1, sucursales.Direccion2, sucursales.Direccion3, sucursales.Telefono, sucursales.Email, ";
				query += "sucursales.idCliente, sucursales.idCiudad, sucursales.idVendedor, sucursales.idCanal, sucursales.idCategoria, ";
				query += "sucursales.idSubCategoria, sucursales.idOrigen, sucursales.idZona, ciudad.Ciudad, zonas.DescZona, clientes.NIT ";
				query += "FROM sucursales JOIN ciudad ON sucursales.idCiudad = ciudad.idCiudad ";
				query += "JOIN zonas ON sucursales.idZona = zonas.idZona ";
				query += "JOIN clientes ON sucursales.idCliente = clientes.idCliente ";

				if(params.size()>0){
					Vector<String> where = new Vector<String>();

					if(params.containsKey("NOMBRE")) query += "JOIN cuentas ON cuentas.idCuenta = clientes.idCuenta ";

					if(params.containsKey("NIT")) where.add("clientes.NIT = '"+params.get("NIT")+"'");
					else if(params.containsKey("NOMBRE")) where.add("cuentas.nitCuenta = '"+params.get("NOMBRE")+"'");
					else if(params.containsKey("CIUDAD")) where.add("ciudad.Ciudad LIKE '%"+params.get("CIUDAD")+"%'");
					else if(params.containsKey("ALMACEN")) where.add("sucursales.nombreAlmacen LIKE '%"+params.get("ALMACEN")+"%'");
					else if(params.containsKey("RAZON_SOCIAL")) where.add("clientes.RazonSocial LIKE '%"+params.get("RAZON_SOCIAL")+"%'");

					if(where.size()>0){
						query += "WHERE (";
						for(int i=0; i<where.size(); i++){
							query += where.get(i);
							if(where.size() > 1 && i <where.size()-1) query += " AND ";
						}
						query += ")";
					}
				}

				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()){
							Hashtable<Object, Object> table = new Hashtable<Object, Object>(0);
							int idSucursal = mCursor.getInt(0);
							String codigoSucursal = mCursor.getString(1);
							String nombreAlmacen = mCursor.getString(2);
							long Cupo = mCursor.getLong(3);
							String Direccion1 = mCursor.getString(4);
							String Direccion2 = mCursor.getString(5);
							String Direccion3 = mCursor.getString(6);
							String Telefono = mCursor.getString(7);
							String Email = mCursor.getString(8);
							int idCliente = mCursor.getInt(9);
							int idCiudad = mCursor.getInt(10);
							int idVendedor = mCursor.getInt(11);
							int idCanal = mCursor.getInt(12);
							int idCategoria = mCursor.getInt(13);
							int idSubCategoria = mCursor.getInt(14);
							int idOrigen = mCursor.getInt(15);
							int idZona = mCursor.getInt(16);
							String Ciudad = mCursor.getString(17);
							String Zona = mCursor.getString(18);
							String NIT = mCursor.getString(19);


							table.put("idSucursal", idSucursal);
							table.put("codigoSucursal", codigoSucursal);
							table.put("nombreAlmacen", nombreAlmacen);
							table.put("Cupo", Cupo);
							table.put("Direccion1", Direccion1);
							table.put("Direccion2", Direccion2);
							table.put("Direccion3", Direccion3);
							table.put("Telefono", Telefono);
							table.put("Email", Email);
							table.put("idCliente", idCliente);
							table.put("idCiudad", idCiudad);
							table.put("idVendedor", idVendedor);
							table.put("idCanal", idCanal);
							table.put("idCategoria", idCategoria);
							table.put("idSubCategoria", idSubCategoria);
							table.put("idOrigen", idOrigen);
							table.put("idZona", idZona);
							table.put("Ciudad", Ciudad);
							table.put("Zona", Zona);
							table.put("NIT", NIT);

							Sucursal sucursal = Sucursal.getInstance(table);
							table = null;

							if(sucursal != null) array.add(sucursal);
						}
					}
					mCursor.close();
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return array;
	}

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordSucursales(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM sucursales WHERE (";
				query += "idSucursal = "+params.get("idSucursal")+" AND ";
				query += "codigoSucursal = '"+params.get("codigoSucursal")+"' AND ";
				query += "nombreAlmacen = '"+params.get("nombreAlmacen")+"' AND ";
				query += "Cupo = "+params.get("Cupo")+" AND ";
				query += "Direccion1 = '"+params.get("Direccion1")+"' AND ";
				query += "Direccion2 = '"+params.get("Direccion2")+"' AND ";
				query += "Direccion3 = '"+params.get("DIreccion3")+"' AND ";
				query += "Telefono = '"+params.get("Telefono")+"' AND ";
				query += "Email = '"+params.get("Email")+"' AND ";
				query += "idCliente = "+params.get("idCliente")+" AND ";
				query += "idCiudad = "+params.get("idCiudad")+" AND ";
				query += "idVendedor = "+params.get("idVendedor")+" AND ";
				query += "idCanal = "+params.get("idCanal")+" AND ";
				query += "idCategoria = "+params.get("idCategoria")+" AND ";
				query += "idSubCategoria = "+params.get("idSubCategoria")+" AND ";
				query += "idOrigen = "+params.get("idOrigen")+" AND ";
				query += "idZona = "+params.get("idZona")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a sucursales record
	 * 
	 *
	 * "idSucursal": "1",
	 * "codigoSucursal": "sucursal1",
	 * "nombreAlmacen": "almacen1",
	 * "Cupo": "2000",
	 * "Direccion1": "direccion",
	 * "Direccion2": "cireccion2",
	 * "DIreccion3": "direccion3",
	 * "Telefono": "4040",
	 * "Email": "juanito@hotmail.com",
	 * "idCliente": "1",
	 * "idCiudad": "1",
	 * "idVendedor": "1",
	 * "idCanal": "1",
	 * "idCategoria": "1",
	 * "idSubCategoria": "1",
	 * "idOrigen": "1",
	 * "idZona": "1"
	 * */
	public boolean insertRecordSucursales(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idSucursal", params.get("idSucursal").toString());
				values.put("codigoSucursal", params.get("codigoSucursal").toString());
				values.put("nombreAlmacen", params.get("nombreAlmacen").toString());
				values.put("Cupo", params.get("Cupo").toString());
				values.put("Direccion1", params.get("Direccion1").toString());
				values.put("Direccion2", params.get("Direccion2").toString());
				values.put("Direccion3", params.get("DIreccion3").toString());
				values.put("Telefono", params.get("Telefono").toString());
				values.put("Email", params.get("Email").toString());
				values.put("idCliente", params.get("idCliente").toString());
				values.put("idCiudad", params.get("idCiudad").toString());
				values.put("idVendedor", params.get("idVendedor").toString());
				values.put("idCanal", params.get("idCanal").toString());
				values.put("idCategoria", params.get("idCategoria").toString());
				values.put("idSubCategoria", params.get("idSubCategoria").toString());
				values.put("idOrigen", params.get("idOrigen").toString());
				values.put("idZona", params.get("idZona").toString());
				myDataBase.insert("sucursales", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordClientes(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM clientes WHERE (";
				query += "idCliente = "+params.get("idCliente")+" AND ";
				query += "NIT = '"+params.get("NIT")+"' AND ";
				query += "RazonSocial = '"+params.get("RazonSocial")+"' AND ";
				query += "Nombre = '"+params.get("Nombre")+"' AND ";
				query += "Telefono = '"+params.get("Telefono")+"' AND ";
				query += "Celular = '"+params.get("Celular")+"' AND ";
				query += "Direccion = '"+params.get("Direccion")+"' AND ";
				query += "idCliente = "+params.get("idCliente")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a clientes record
	 * 
	 * "idCliente": "1",
	            "NIT": "1010",
	            "RazonSocial": "razonsocial",
	            "Nombre": "garcia",
	            "Telefono": "3215561",
	            "Celular": "3108976543",
	            "Direccion": "cra 90 sur",
	            "idCuenta": "1"
	 * */
	public boolean insertRecordClientes(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idCliente", params.get("idCliente").toString());
				values.put("NIT", params.get("NIT").toString());
				values.put("RazonSocial", params.get("RazonSocial").toString());
				values.put("Nombre", params.get("Nombre").toString());
				values.put("Telefono", params.get("Telefono").toString());
				values.put("Celular", params.get("Celular").toString());
				values.put("Direccion", params.get("Direccion").toString());
				values.put("idCuenta", params.get("idCuenta").toString());
				myDataBase.insert("clientes", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordCiudad(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM ciudad WHERE (";
				query += "Ciudad = '"+params.get("Ciudad")+"' AND ";
				query += "idCiudad = "+params.get("idCiudad")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a ciudad record
	 * 
	 * "Ciudad":"ciudad1",
	 * "idCiudad":"1"
	 * */
	public boolean insertRecordCiudad(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("Ciudad", params.get("Ciudad").toString());
				values.put("idCiudad", params.get("idCiudad").toString());
				myDataBase.insert("ciudad", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordCanales(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM canales WHERE (";
				query += "DescCanal = '"+params.get("DescCanal")+"' AND ";
				query += "idCanal = "+params.get("idCanal")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a canales record
	 * 
	 * "idCanal":"1"
	 * "DescCanal":"canal1"
	 * */
	public boolean insertRecordCanales(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idCanal", params.get("idCanal").toString());
				values.put("DescCanal", params.get("DescCanal").toString());
				myDataBase.insert("canales", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordCategorias(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM categorias WHERE (";
				query += "DescCategoria = '"+params.get("DescCategoria")+"' AND ";
				query += "idCategoria = "+params.get("idCategoria")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a categorias record
	 * 
	 * "idCategoria":"1"
	 * "DescCategoria":"categoria1"
	 * */
	public boolean insertRecordCategorias(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idCategoria", params.get("idCategoria").toString());
				values.put("DescCategoria", params.get("DescCategoria").toString());
				myDataBase.insert("categorias", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordSubcategorias(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM subcategorias WHERE (";
				query += "DescSubCategoria = '"+params.get("DescSubCategoria")+"' AND ";
				query += "idSubcategoria = "+params.get("idSubcategoria")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a subcategorias record
	 * 
	 * "DescSubCategoria":"subcategoria1",
	 * "idSubcategoria":"1"
	 * */
	public boolean insertRecordSubcategorias(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idSubcategoria", params.get("idSubcategoria").toString());
				values.put("DescSubCategoria", params.get("DescSubCategoria").toString());
				myDataBase.insert("subcategorias", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************
	
	////[{"talla":"XXS","IndiceTalla":"0"},{"talla":"XS","IndiceTalla":"1"},{"talla":"S","IndiceTalla":"2"},{"talla":"M","IndiceTalla":"3"},{"talla":"L","IndiceTalla":"4"},{"talla":"XL","IndiceTalla":"5"},{"talla":"XXL","IndiceTalla":"6"},{"talla":"unica","IndiceTalla":"7"}]
	
	public boolean existIndiceTalla(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM IndiceTalla WHERE (";
				query += "talla = '"+params.get("talla")+"' AND ";
				query += "indice = "+params.get("IndiceTalla")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	private boolean createIndiceTalla(){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "CREATE TABLE IF NOT EXISTS IndiceTalla (talla TEXT, indice NUMERIC) ";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				mCursor.close();
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	private boolean deleteIndiceTalla(){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "DROP TABLE IF NOT EXISTS IndiceTalla";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				mCursor.close();
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	public boolean insertIndiceTalla(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("talla", params.get("talla").toString());
				values.put("indice", params.get("IndiceTalla").toString());
				myDataBase.insert("IndiceTalla", null, values);
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	public String[] getIndiceTalla(){
		String[] TALLAS = null;
		if(myDataBase != null) {
			try{
				String query = "SELECT talla FROM IndiceTalla ORDER BY indice";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					TALLAS = new String[c];
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()) TALLAS[i] = mCursor.getString(0);
					}
					mCursor.close();
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				TALLAS = null;
			}
		}
		return TALLAS;
	}
	
	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordOrigenes(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM origenes WHERE (";
				query += "DescOrigen = '"+params.get("DescOrigen")+"' AND ";
				query += "idOrigen = "+params.get("idOrigen")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a origenes record
	 * 
	 * "idOrigen":"1",
	 * "DescOrigen":"origen1"
	 * */
	public boolean insertRecordOrigenes(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idOrigen", params.get("idOrigen").toString());
				values.put("DescOrigen", params.get("DescOrigen").toString());
				myDataBase.insert("origenes", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordZonas(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM zonas WHERE (";
				query += "DescZona = '"+params.get("DescZona")+"' AND ";
				query += "idZona = "+params.get("idZona")+" )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a zonas record
	 * 
	 * "DescZona":"zona1",
	 * "idZona":"1"
	 * */
	public boolean insertRecordZonas(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idZona", params.get("idZona").toString());
				values.put("DescZona", params.get("DescZona").toString());
				myDataBase.insert("zonas", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordVendedores(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM vendedores WHERE (";
				query += "Nombres = '"+params.get("Nombres")+"' AND ";
				query += "idVendedor = "+params.get("idVendedor")+" AND ";
				query += "Cedula = '"+params.get("Cedula")+"' AND ";
				query += "Codigo = '"+params.get("Codigo")+"' AND ";
				query += "Email = '"+params.get("Email")+"' AND ";
				query += "Telefono = '"+params.get("Telefono")+"' )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a zonas record
	 * 
	 * "Nombres": "vendedor1",
	 * "idVendedor": "1",
	 * "Cedula": "123",
	 * "Codigo": "001",
	 * "Email": "juanito@hotmail.com",
	 * "Telefono": "4040"
	 * */
	public boolean insertRecordVendedores(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("Nombres", params.get("Nombres").toString());
				values.put("idVendedor", params.get("idVendedor").toString());
				values.put("Cedula", params.get("Cedula").toString());
				values.put("Codigo", params.get("Codigo").toString());
				values.put("Email", params.get("Email").toString());
				values.put("Telefono", params.get("Telefono").toString());
				myDataBase.insert("vendedores", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	//*************************************************************************

	/**
	 * This method ask if exist record
	 * */
	public boolean existRecordCuentas(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM cuentas WHERE (";
				query += "nitCuenta = '"+params.get("nitCuenta")+"' AND ";
				query += "idCuenta = "+params.get("idCuenta")+" AND ";
				query += "DescCuenta = '"+params.get("DescCuenta")+"' )";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) response = true;
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	/*
	 * This method insert a cuentas record
	 * 
	 * "idCuenta": "1",
       "nitCuenta": "2030",
        "DescCuenta": "cuenta1"
	 * */
	public boolean insertRecordCuentas(Hashtable<Object, Object> params){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
				values.put("idCuenta", params.get("idCuenta").toString());
				values.put("nitCuenta", params.get("nitCuenta").toString());
				values.put("DescCuenta", params.get("DescCuenta").toString());
				myDataBase.insert("cuentas", null, values);
				response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}
	
	//*************************************************************************
	
	//http://sekthdroid.wordpress.com/2013/02/06/elemento-autocompletetextview-en-android/
	//autocompletar referencias sku
	public String[] getReferenciasSku(){
		String[] referencias = null;
		if(myDataBase != null) {
			try{
				String query = "select DISTINCT(Referencia) from sku";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					referencias = new String[c];
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()) referencias[i] = mCursor.getString(0);
					}
					mCursor.close();
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
				referencias = null;
			}
		}
		return referencias;
	}

	//*************************************************************************

	/*public Hashtable<Object, Object>[] getSongs(String id){
		if(myDataBase != null) {
			try{
				String query = "SELECT cancion, duracion, letra, link, sing FROM albunes WHERE id='10"+id+"'";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					Hashtable<Object, Object>[] table = new Hashtable[c];
					for(int i=0; i<c; i++){
						if(mCursor.moveToNext()){
							table[i] = new Hashtable<Object, Object>(0);
							String Cancion = mCursor.getString(0);
							String Duracion = mCursor.getString(1);
							String Letra = mCursor.getString(2);
							String Link = mCursor.getString(3);
							String Sing = mCursor.getString(4);

							table[i].put("cancion", Cancion);
							table[i].put("duracion", Duracion);
							table[i].put("letra", Letra);
							table[i].put("link", Link);
							table[i].put("sing", Sing);
						}
					}
					mCursor.close();
					return table;
				}
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

	//*************************************************************************

	public boolean isRegistredUser(){
		boolean rsp = false;
		if(myDataBase != null) {
			try{
				String query = "SELECT * FROM user";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				if(mCursor.getCount() > 0) rsp = true;
				mCursor.close();
			}catch(Exception e){}
		}
		return rsp;
	}

	public boolean deleteUser(){
		if(myDataBase != null) {
			try{
				String query = "DELETE FROM user";
				myDataBase.execSQL(query);
			}catch(Exception e){}
		    return true;
		}
		return false;
	}

	public boolean insertUser(Hashtable data){
		boolean response = false;
		if(myDataBase != null) {
			try{
				ContentValues values = new ContentValues();
			    values.put("uid", data.get("uid").toString());
			    values.put("device", data.get("device").toString());
			    values.put("pwd", data.get("pwd").toString());
			    values.put("birdthday", data.get("birdthday").toString());
			    values.put("email", data.get("email").toString());
			    values.put("fullname", data.get("fullname").toString());
			    values.put("uidd", data.get("uidd").toString());
			    myDataBase.insert("user", null, values);
			    response = true;
			}catch(Exception e){
				e.printStackTrace();
				response = false;
			}
		}
		return response;
	}

	public Hashtable getUser(){
		if(myDataBase != null) {
			try{
				String query = "SELECT uid, device, pwd, birdthday, email, fullname, uidd FROM user";
				Cursor mCursor = myDataBase.rawQuery(query, null);
				int c = mCursor.getCount();
				if(c > 0){
					if(mCursor.moveToNext()){
						Hashtable<Object, Object> table = new Hashtable<Object, Object>(0);
						String uid = mCursor.getString(0);
						String device = mCursor.getString(1);
						String pwd = mCursor.getString(2);
						String birdthday = mCursor.getString(3);
						String email = mCursor.getString(4);
						String fullname = mCursor.getString(5);
						String uidd = mCursor.getString(6);

						mCursor.close();

						table.put("uid", uid);
						table.put("device", device);
						table.put("pwd", pwd);
						table.put("birdthday", birdthday);
						table.put("email", email);
						table.put("fullname", fullname);
						table.put("uidd", uidd);
						return table;
					}
				}
				mCursor.close();
			}catch(Exception e){}
		}
		return null;
	}*/

	//*************************************************************************

}
