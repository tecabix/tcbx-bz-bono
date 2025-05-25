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
public class Bono006BzDTO {

    private BonoAhorroRepository bonoAhorroRepository;
    private CuentaRepository cuentaRepository;
    private TransaccionRepository transaccionRepository;
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
    
    private Catalogo activo;
    private Catalogo venta;
    private Catalogo eliminado;
    private Catalogo pendiente;
    
    private Cuenta cuentaInversion;

	public BonoAhorroRepository getBonoAhorroRepository() {
		return bonoAhorroRepository;
	}

	public void setBonoAhorroRepository(BonoAhorroRepository bonoAhorroRepository) {
		this.bonoAhorroRepository = bonoAhorroRepository;
	}

	public CuentaRepository getCuentaRepository() {
		return cuentaRepository;
	}

	public void setCuentaRepository(CuentaRepository cuentaRepository) {
		this.cuentaRepository = cuentaRepository;
	}

	public TransaccionRepository getTransaccionRepository() {
		return transaccionRepository;
	}

	public void setTransaccionRepository(TransaccionRepository transaccionRepository) {
		this.transaccionRepository = transaccionRepository;
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

	public Catalogo getVenta() {
		return venta;
	}

	public void setVenta(Catalogo venta) {
		this.venta = venta;
	}

	public Catalogo getEliminado() {
		return eliminado;
	}

	public void setEliminado(Catalogo eliminado) {
		this.eliminado = eliminado;
	}

	public Catalogo getPendiente() {
		return pendiente;
	}

	public void setPendiente(Catalogo pendiente) {
		this.pendiente = pendiente;
	}

	public Cuenta getCuentaInversion() {
		return cuentaInversion;
	}

	public void setCuentaInversion(Cuenta cuentaInversion) {
		this.cuentaInversion = cuentaInversion;
	}
}
