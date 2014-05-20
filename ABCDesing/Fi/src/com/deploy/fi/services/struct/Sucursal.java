package com.deploy.fi.services.struct;

import java.io.Serializable;
import java.util.Hashtable;

import android.util.Log;

public class Sucursal implements Serializable {

	private static final long serialVersionUID = 5489365794278299059L;
	
	private Sucursal(){
	}
	
	public static Sucursal getInstance(Hashtable<Object,Object> params){
		Sucursal sucursal = null;
		try{
			if(params != null && params.size()>0){
				if(!params.containsKey("idSucursal")) params.put("idSucursal", 1);
				if(!params.containsKey("idCliente")) params.put("idCliente", 1);
				if(!params.containsKey("idCiudad")) params.put("idCiudad", 1);
				if(!params.containsKey("idVendedor")) params.put("idVendedor", 1);
				if(!params.containsKey("idCanal")) params.put("idCanal", 1);
				if(!params.containsKey("idCanal")) params.put("idCanal", 1);
				if(!params.containsKey("idCategoria")) params.put("idCategoria", 1);
				if(!params.containsKey("idSubCategoria")) params.put("idSubCategoria", 1);
				if(!params.containsKey("idOrigen")) params.put("idOrigen", 1);
				if(!params.containsKey("idZona")) params.put("idZona", 1);
				
				if(!params.containsKey("codigoSucursal")) params.put("codigoSucursal", 1);
				if(!params.containsKey("nombreAlmacen")) params.put("nombreAlmacen", "");
				if(!params.containsKey("Cupo")) params.put("Cupo", 1000.0);
				
				if(!params.containsKey("Direccion1")) params.put("Direccion1", "");
				if(!params.containsKey("Direccion2")) params.put("Direccion2", "");
				if(!params.containsKey("Direccion3")) params.put("Direccion3", "");
				
				if(!params.containsKey("Ciudad")) params.put("Ciudad", "");
				if(!params.containsKey("Zona")) params.put("Zona", "");
				if(!params.containsKey("NIT")) params.put("NIT", "");
				
				if(!params.containsKey("Telefono")) params.put("Telefono", "");
				if(!params.containsKey("Email")) params.put("Email", "");
				
				sucursal = new Sucursal();
				
				sucursal.nombreAlmacen = params.get("nombreAlmacen").toString();
				sucursal.codigoSucursal = params.get("codigoSucursal").toString();
				sucursal.Cupo = Double.parseDouble(params.get("Cupo").toString());
				
				sucursal.Direccion1 = params.get("Direccion1").toString();
				sucursal.Direccion2 = params.get("Direccion2").toString();
				sucursal.Direccion3 = params.get("Direccion3").toString();
				
				sucursal.Ciudad = params.get("Ciudad").toString();
				sucursal.Email = params.get("Email").toString();
				sucursal.Zona = params.get("Zona").toString();
				sucursal.NIT = params.get("NIT").toString();
				
				sucursal.idCanal = Integer.parseInt(params.get("idCanal").toString());
				sucursal.idCategoria = Integer.parseInt(params.get("idCategoria").toString());
				sucursal.idCiudad = Integer.parseInt(params.get("idCiudad").toString());
				sucursal.idCliente = Integer.parseInt(params.get("idCliente").toString());
				sucursal.idOrigen = Integer.parseInt(params.get("idOrigen").toString());
				sucursal.idSubCategoria = Integer.parseInt(params.get("idSubCategoria").toString());
				sucursal.idSucursal = Integer.parseInt(params.get("idSucursal").toString());
				sucursal.idVendedor = Integer.parseInt(params.get("idVendedor").toString());
				sucursal.idZona = Integer.parseInt(params.get("idZona").toString());
				
				sucursal.Telefono = params.get("Telefono").toString();
			}
		}catch(Exception e){
			Log.e("Sucursal.getInstance", e.getMessage());
			sucursal = null;
		}
		return sucursal;
	}
	
	private int idSucursal = 1;
	private String codigoSucursal = "1";
	private String nombreAlmacen = "1";
	private double Cupo = 2000.0;
	 
	private String Direccion1 = "";
	private String Direccion2 = "";
	private String Direccion3 = "";
	 
	private String Telefono = "";
	private String Email = "";
	private int idCliente = 1;
	
	private String NIT = "";
	private String Ciudad = "";
	private String Zona = "";
	
	private int idCiudad = 1;
	private int idVendedor = 1;
	private int idCanal = 1;
	private int idCategoria = 1;
	private int idSubCategoria = 1;
	private int idOrigen = 1;
	private int idZona = 1;
	
	public int getIdSucursal() {
		return idSucursal;
	}
	
	public int getIdCliente() {
		return idCliente;
	}
	
	public int getIdCiudad() {
		return idCiudad;
	}
	
	public String getCiudad() {
		return Ciudad;
	}
	
	public int getIdVendedor() {
		return idVendedor;
	}
	
	public int getIdCanal() {
		return idCanal;
	}
	
	public int getIdCategoria() {
		return idCategoria;
	}
	
	public int getIdSubCategoria() {
		return idSubCategoria;
	}
	
	public int getIdOrigen() {
		return idOrigen;
	}
	
	public int getIdZona() {
		return idZona;
	}
	
	public String getZona() {
		return Zona;
	}
	public String getCodigoSucursal() {
		return codigoSucursal;
	}
	
	public String getNombreAlmacen() {
		return nombreAlmacen;
	}
	
	public double getCupo() {
		return Cupo;
	}
	
	public String getDireccion1() {
		return Direccion1;
	}
	
	public String getDireccion2() {
		return Direccion2;
	}
	
	public String getDireccion3() {
		return Direccion3;
	}
	
	public String getTelefono() {
		return Telefono;
	}
	
	public String getEmail() {
		return Email;
	}
	
	public String getNIT() {
		return NIT;
	}
	
}
