package com.tecabix.bz.bono.dto;

import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Cuenta;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.BonoRepository;
import com.tecabix.db.repository.CuentaRepository;
import com.tecabix.db.repository.TransaccionRepository;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
public class Bono003BzDTO {

	private BonoRepository bonoRepository;
    private CuentaRepository cuentaRepository;
    private BonoAhorroRepository bonoAhorroRepository;
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
    private TransaccionRepository transaccionRepository;
    
    private Catalogo activo;
    private Catalogo pendiente;
    private Catalogo eliminado;
    private Catalogo compra;
    
    private Cuenta cuentaInversion;

	public BonoRepository getBonoRepository() {
		return bonoRepository;
	}

	public void setBonoRepository(BonoRepository bonoRepository) {
		this.bonoRepository = bonoRepository;
	}

	public CuentaRepository getCuentaRepository() {
		return cuentaRepository;
	}

	public void setCuentaRepository(CuentaRepository cuentaRepository) {
		this.cuentaRepository = cuentaRepository;
	}

	public BonoAhorroRepository getBonoAhorroRepository() {
		return bonoAhorroRepository;
	}

	public void setBonoAhorroRepository(BonoAhorroRepository bonoAhorroRepository) {
		this.bonoAhorroRepository = bonoAhorroRepository;
	}

	public BonoAhorroPeriodoRepository getBonoAhorroPeriodoRepository() {
		return bonoAhorroPeriodoRepository;
	}

	public void setBonoAhorroPeriodoRepository(BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository) {
		this.bonoAhorroPeriodoRepository = bonoAhorroPeriodoRepository;
	}

	public TransaccionRepository getTransaccionRepository() {
		return transaccionRepository;
	}

	public void setTransaccionRepository(TransaccionRepository transaccionRepository) {
		this.transaccionRepository = transaccionRepository;
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

	public Catalogo getCompra() {
		return compra;
	}

	public void setCompra(Catalogo compra) {
		this.compra = compra;
	}

	public Cuenta getCuentaInversion() {
		return cuentaInversion;
	}

	public void setCuentaInversion(Cuenta cuentaInversion) {
		this.cuentaInversion = cuentaInversion;
	}
}
