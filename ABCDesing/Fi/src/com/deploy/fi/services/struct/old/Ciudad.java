package com.deploy.fi.services.struct.old;

import java.io.Serializable;

public class Ciudad implements Serializable {

	private static final long serialVersionUID = -4714964736789151777L;
	private String f013_id;
	private String f013_descripcion="";
	private Departamento departamento;
	
	public Ciudad() {
	}
	
	public Ciudad(String f013_descripcion) {
		super();
		this.f013_descripcion = f013_descripcion;
	}

	public String getF013_id() {
		return f013_id;
	}

	public void setF013_id(String f013_id) {
		this.f013_id = f013_id;
	}

	public String getF013_descripcion() {
		return f013_descripcion;
	}

	public void setF013_descripcion(String f013_descripcion) {
		this.f013_descripcion = f013_descripcion;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

}
