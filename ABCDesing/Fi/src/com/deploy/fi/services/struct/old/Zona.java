package com.deploy.fi.services.struct.old;

import java.io.Serializable;

public class Zona implements Serializable {

	private static final long serialVersionUID = 858242210701170286L;
	
	private String ID_Zona="";
	private String descripcion="";
	
	public Zona() {
	}
	
	public String getID_Zona() {
		return ID_Zona;
	}
	public void setID_Zona(String iD_Zona) {
		ID_Zona = iD_Zona;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
