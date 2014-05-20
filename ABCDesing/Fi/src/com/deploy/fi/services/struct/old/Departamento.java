package com.deploy.fi.services.struct.old;

import java.io.Serializable;

public class Departamento implements Serializable {

	private static final long serialVersionUID = 6647582034219969185L;
	
	private String f013_id_depto;
	private String f012_descripcion;
	private Pais pais;
	
	public Departamento() {
	}
	
	public String getF013_id_depto() {
		return f013_id_depto;
	}
	public void setF013_id_depto(String f013_id_depto) {
		this.f013_id_depto = f013_id_depto;
	}
	public String getF012_descripcion() {
		return f012_descripcion;
	}
	public void setF012_descripcion(String f012_descripcion) {
		this.f012_descripcion = f012_descripcion;
	}
	
	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

}
