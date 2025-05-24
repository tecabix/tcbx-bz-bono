package com.tecabix.bz.bono.dto;

import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Cuenta;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.CuentaRepository;
import com.tecabix.db.repository.TransaccionRepository;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
public class Bono005BzDTO {


	private BonoAhorroRepository bonoAhorroRepository;
	private TransaccionRepository transaccionRepository;
	private CuentaRepository cuentaRepository;
	private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
	
	private Catalogo activo;
	private Catalogo pendiente;
	private Catalogo eliminado;
	private Catalogo aportacion;
	
	private Cuenta cuentaInversion;

	public BonoAhorroRepository getBonoAhorroRepository() {
		return bonoAhorroRepository;
	}

	public void setBonoAhorroRepository(BonoAhorroRepository bonoAhorroRepository) {
		this.bonoAhorroRepository = bonoAhorroRepository;
	}

	public TransaccionRepository getTransaccionRepository() {
		return transaccionRepository;
	}

	public void setTransaccionRepository(TransaccionRepository transaccionRepository) {
		this.transaccionRepository = transaccionRepository;
	}

	public CuentaRepository getCuentaRepository() {
		return cuentaRepository;
	}

	public void setCuentaRepository(CuentaRepository cuentaRepository) {
		this.cuentaRepository = cuentaRepository;
	}

	public BonoAhorroPeriodoRepository getBonoAhorroPeriodoRepository() {
		return bonoAhorroPeriodoRepository;
	}

	public void setBonoAhorroPeriodoRepository(BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository) {
		this.bonoAhorroPeriodoRepository = bonoAhorroPeriodoRepository;
	}

	public Catalogo getActivo() {
		return activo;
	}

	public void setActivo(Catalogo activo) {
		this.activo = activo;
	}

	public Catalogo getPendiente() {
		return pendiente;
	}

	public void setPendiente(Catalogo pendiente) {
		this.pendiente = pendiente;
	}

	public Catalogo getEliminado() {
		return eliminado;
	}

	public void setEliminado(Catalogo eliminado) {
		this.eliminado = eliminado;
	}

	public Catalogo getAportacion() {
		return aportacion;
	}

	public void setAportacion(Catalogo aportacion) {
		this.aportacion = aportacion;
	}

	public Cuenta getCuentaInversion() {
		return cuentaInversion;
	}

	public void setCuentaInversion(Cuenta cuentaInversion) {
		this.cuentaInversion = cuentaInversion;
	}
}
