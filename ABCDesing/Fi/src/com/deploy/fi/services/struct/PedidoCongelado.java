package com.deploy.fi.services.struct;

import java.io.Serializable;

public class PedidoCongelado implements Serializable {
	
	private static final long serialVersionUID = 7824945962207370440L;
	
	public PedidoCongelado(){}
	
	public java.util.Date FechaEntrega = null;
	public java.util.Date FechaPedido = null;
	public String NombreAlmacen = null;
	public int TotalItems = 0;
	public int idSucursal = 0;
	public int idPedido = 0;

}
