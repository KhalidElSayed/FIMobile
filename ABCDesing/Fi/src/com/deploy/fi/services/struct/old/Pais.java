package com.deploy.fi.services.struct.old;

import java.io.Serializable;

public class Pais implements Serializable {

	private static final long serialVersionUID = -4859551207223884919L;
	
	private String f013_id_pais;
	private String f011_descripcion;
	
	public Pais() {
	}

	public String getF013_id_pais() {
		return f013_id_pais;
	}

	public void setF013_id_pais(String f013_id_pais) {
		this.f013_id_pais = f013_id_pais;
	}

	public String getF011_descripcion() {
		return f011_descripcion;
	}

	public void setF011_descripcion(String f011_descripcion) {
		this.f011_descripcion = f011_descripcion;
	}

}
