package com.tecabix.bz.bono.dto;

import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.repository.BonoAhorroRepository;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
public class Bono004BzDTO {


	private BonoAhorroRepository bonoAhorroRepository;
	
	private Catalogo activo;

	public BonoAhorroRepository getBonoAhorroRepository() {
		return bonoAhorroRepository;
	}

	public void setBonoAhorroRepository(BonoAhorroRepository bonoAhorroRepository) {
		this.bonoAhorroRepository = bonoAhorroRepository;
	}

	public Catalogo getActivo() {
		return activo;
	}

	public void setActivo(Catalogo activo) {
		this.activo = activo;
	}
}
