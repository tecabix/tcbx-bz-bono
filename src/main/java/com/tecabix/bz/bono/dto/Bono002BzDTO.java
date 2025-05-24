package com.tecabix.bz.bono.dto;

import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.BonoInversionRepository;
import com.tecabix.db.repository.BonoPagoRepository;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
public class Bono002BzDTO {

	private BonoAhorroRepository bonoAhorroRepository;
	
	private BonoInversionRepository bonoInversionRepository;
	
	private BonoPagoRepository bonoPagoRepository;
	
	private Integer activo;

	public BonoAhorroRepository getBonoAhorroRepository() {
		return bonoAhorroRepository;
	}

	public void setBonoAhorroRepository(BonoAhorroRepository bonoAhorroRepository) {
		this.bonoAhorroRepository = bonoAhorroRepository;
	}

	public BonoInversionRepository getBonoInversionRepository() {
		return bonoInversionRepository;
	}

	public void setBonoInversionRepository(BonoInversionRepository bonoInversionRepository) {
		this.bonoInversionRepository = bonoInversionRepository;
	}

	public BonoPagoRepository getBonoPagoRepository() {
		return bonoPagoRepository;
	}

	public void setBonoPagoRepository(BonoPagoRepository bonoPagoRepository) {
		this.bonoPagoRepository = bonoPagoRepository;
	}

	public Integer getActivo() {
		return activo;
	}

	public void setActivo(Integer activo) {
		this.activo = activo;
	}	
}
