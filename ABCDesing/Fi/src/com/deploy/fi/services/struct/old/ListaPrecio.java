package com.deploy.fi.services.struct.old;

import java.io.Serializable;

public class ListaPrecio implements Serializable {

	private static final long serialVersionUID = -5884237578590148166L;

	private String ID_Lista;
	private String DESC_Lista;
	
	public ListaPrecio() {
	}

	public String getID_Lista() {
		return ID_Lista;
	}

	public void setID_Lista(String iD_Lista) {
		ID_Lista = iD_Lista;
	}

	public String getDESC_Lista() {
		return DESC_Lista;
	}

	public void setDESC_Lista(String dESC_Lista) {
		DESC_Lista = dESC_Lista;
	}
	
}
