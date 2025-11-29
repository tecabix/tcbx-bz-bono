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

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Repositorio para acceder a la entidad Cuenta.
     */
    private CuentaRepository cuentaRepository;

    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private TransaccionRepository transaccionRepository;

    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private Catalogo activo;

    /**
     * Tipo de transacción "Venta" obtenido desde el catálogo.
     */
    private Catalogo venta;

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private Catalogo eliminado;

    /**
     * Estado "Pendiente" obtenido desde el catálogo.
     */
    private Catalogo pendiente;

    /**
     * Cuenta de inversión asignada.
     */
    private Cuenta cuentaInversion;

    /**
     * Obtiene el repositorio de Bono Ahorro.
     *
     * @return el repositorio de Bono Ahorro
     */
    public BonoAhorroRepository getBonoAhorroRepository() {
        return bonoAhorroRepository;
    }

    /**
     * Establece el repositorio de Bono Ahorro.
     *
     * @param repository el repositorio de Bono Ahorro a establecer
     */
    public void setBonoAhorroRepository(
            final BonoAhorroRepository repository) {
        this.bonoAhorroRepository = repository;
    }

    /**
     * Obtiene el repositorio de Cuenta.
     *
     * @return el repositorio de Cuenta
     */
    public CuentaRepository getCuentaRepository() {
        return cuentaRepository;
    }

    /**
     * Establece el repositorio de Cuenta.
     *
     * @param repository el repositorio de Cuenta a establecer
     */
    public void setCuentaRepository(final CuentaRepository repository) {
        this.cuentaRepository = repository;
    }

    /**
     * Obtiene el repositorio de Transacción.
     *
     * @return el repositorio de Transacción
     */
    public TransaccionRepository getTransaccionRepository() {
        return transaccionRepository;
    }

    /**
     * Establece el repositorio de Transacción.
     *
     * @param repository el repositorio de Transacción a establecer
     */
    public void setTransaccionRepository(
            final TransaccionRepository repository) {
        this.transaccionRepository = repository;
    }

    /**
     * Obtiene el repositorio de Bono Ahorro por Periodo.
     *
     * @return el repositorio de Bono Ahorro por Periodo
     */
    public BonoAhorroPeriodoRepository getBonoAhorroPeriodoRepository() {
        return bonoAhorroPeriodoRepository;
    }

    /**
     * Establece el repositorio de Bono Ahorro por Periodo.
     *
     * @param repository el repositorio a establecer
     */
    public void setBonoAhorroPeriodoRepository(
            final BonoAhorroPeriodoRepository repository) {
        this.bonoAhorroPeriodoRepository = repository;
    }

    /**
     * Obtiene el catálogo de estado "Activo".
     *
     * @return el catálogo "Activo"
     */
    public Catalogo getActivo() {
        return activo;
    }

    /**
     * Establece el catálogo de estado "Activo".
     *
     * @param estatus el catálogo "Activo" a establecer
     */
    public void setActivo(final Catalogo estatus) {
        this.activo = estatus;
    }

    /**
     * Obtiene el catálogo de estado "Venta".
     *
     * @return el catálogo "Venta"
     */
    public Catalogo getVenta() {
        return venta;
    }

    /**
     * Establece el catálogo de estado "Venta".
     *
     * @param operacion el catálogo "Venta" a establecer
     */
    public void setVenta(final Catalogo operacion) {
        this.venta = operacion;
    }

    /**
     * Obtiene el catálogo de estado "Eliminado".
     *
     * @return el catálogo "Eliminado"
     */
    public Catalogo getEliminado() {
        return eliminado;
    }

    /**
     * Establece el catálogo de estado "Eliminado".
     *
     * @param estatus el catálogo "Eliminado" a establecer
     */
    public void setEliminado(final Catalogo estatus) {
        this.eliminado = estatus;
    }

    /**
     * Obtiene el catálogo de estado "Pendiente".
     *
     * @return el catálogo "Pendiente"
     */
    public Catalogo getPendiente() {
        return pendiente;
    }

    /**
     * Establece el catálogo de estado "Pendiente".
     *
     * @param estatus el catálogo "Pendiente" a establecer
     */
    public void setPendiente(final Catalogo estatus) {
        this.pendiente = estatus;
    }

    /**
     * Obtiene la cuenta de inversión.
     *
     * @return la cuenta de inversión
     */
    public Cuenta getCuentaInversion() {
        return cuentaInversion;
    }

    /**
     * Establece la cuenta de inversión.
     *
     * @param inversion la cuenta de inversión a establecer
     */
    public void setCuentaInversion(final Cuenta inversion) {
        this.cuentaInversion = inversion;
    }
}
