package com.deploy.fi.services.struct.old;

import com.deploy.fi.services.struct.Sucursal;

public class Cliente {

	private String idCliente;
	private String NIT;
	private String RazonSocial;
	private Sucursal[] sucursales;
	private ListaPrecio[] listaPrecios;
	
	public Cliente() {
	}
	
	public Cliente(String nIT) {
		super();
		NIT = nIT;
	}
	
	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}

	public String getNIT() {
		return NIT;
	}

	public void setNIT(String nIT) {
		NIT = nIT;
	}

	public String getRazonSocial() {
		return RazonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		RazonSocial = razonSocial;
	}

	public Sucursal[] getSucursales() {
		return sucursales;
	}

	public void setSucursales(Sucursal[] sucursales) {
		this.sucursales = sucursales;
	}

	public ListaPrecio[] getListaPrecios() {
		return listaPrecios;
	}

	public void setListaPrecios(ListaPrecio[] listaPrecios) {
		this.listaPrecios = listaPrecios;
	}

	@Override
	public String toString() {
		return this.NIT + " - " + this.RazonSocial;
	}
	
}
