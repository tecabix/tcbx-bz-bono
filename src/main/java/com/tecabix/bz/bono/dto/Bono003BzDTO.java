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

    /**
     * Repositorio para acceder a la entidad Bono.
     */
    private BonoRepository bonoRepository;
    /**
     * Repositorio para acceder a la entidad Cuenta.
     */
    private CuentaRepository cuentaRepository;
    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;
    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private TransaccionRepository transaccionRepository;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private Catalogo activo;
    /**
     * Estado "Pendiente" obtenido desde el catálogo.
     */
    private Catalogo pendiente;
    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private Catalogo eliminado;
    /**
     * Tipo de transacción "Compra" obtenido desde el catálogo.
     */
    private Catalogo compra;

    /**
     * Cuenta de inversión asignada.
     */
    private Cuenta cuentaInversion;

    /**
     * Obtiene el repositorio de bonos.
     *
     * @return una instancia de {@link BonoRepository}
     */
    public BonoRepository getBonoRepository() {
        return bonoRepository;
    }

    /**
     * Establece el repositorio de bonos.
     *
     * @param repository una instancia de {@link BonoRepository}
     */
    public void setBonoRepository(final BonoRepository repository) {
        this.bonoRepository = repository;
    }

    /**
     * Obtiene el repositorio de cuentas.
     *
     * @return una instancia de {@link CuentaRepository}
     */
    public CuentaRepository getCuentaRepository() {
        return cuentaRepository;
    }

    /**
     * Establece el repositorio de cuentas.
     *
     * @param repository una instancia de {@link CuentaRepository}
     */
    public void setCuentaRepository(final CuentaRepository repository) {
        this.cuentaRepository = repository;
    }

    /**
     * Obtiene el repositorio de bonos de ahorro.
     *
     * @return una instancia de {@link BonoAhorroRepository}
     */
    public BonoAhorroRepository getBonoAhorroRepository() {
        return bonoAhorroRepository;
    }

    /**
     * Establece el repositorio de bonos de ahorro.
     *
     * @param repository una instancia de {@link BonoAhorroRepository}
     */
    public void setBonoAhorroRepository(
            final BonoAhorroRepository repository) {
        this.bonoAhorroRepository = repository;
    }

    /**
     * Obtiene el repositorio de periodos de bonos de ahorro.
     *
     * @return una instancia de {@link BonoAhorroPeriodoRepository}
     */
    public BonoAhorroPeriodoRepository getBonoAhorroPeriodoRepository() {
        return bonoAhorroPeriodoRepository;
    }

    /**
     * Establece el repositorio de periodos de bonos de ahorro.
     *
     * @param repository una instancia de
     *        {@link BonoAhorroPeriodoRepository}
     */
    public void setBonoAhorroPeriodoRepository(
            final BonoAhorroPeriodoRepository repository) {
        this.bonoAhorroPeriodoRepository = repository;
    }

    /**
     * Obtiene el repositorio de transacciones.
     *
     * @return una instancia de {@link TransaccionRepository}
     */
    public TransaccionRepository getTransaccionRepository() {
        return transaccionRepository;
    }

    /**
     * Establece el repositorio de transacciones.
     *
     * @param repository una instancia de
     *        {@link TransaccionRepository}
     */
    public void setTransaccionRepository(
            final TransaccionRepository repository) {
        this.transaccionRepository = repository;
    }

    /**
     * Obtiene el catálogo que representa el estado activo.
     *
     * @return una instancia de {@link Catalogo}
     */
    public Catalogo getActivo() {
        return activo;
    }

    /**
     * Establece el catálogo que representa el estado activo.
     *
     * @param estatus una instancia de {@link Catalogo}
     */
    public void setActivo(final Catalogo estatus) {
        this.activo = estatus;
    }

    /**
     * Obtiene el catálogo que representa el estado pendiente.
     *
     * @return una instancia de {@link Catalogo}
     */
    public Catalogo getPendiente() {
        return pendiente;
    }

    /**
     * Establece el catálogo que representa el estado pendiente.
     *
     * @param estatus una instancia de {@link Catalogo}
     */
    public void setPendiente(final Catalogo estatus) {
        this.pendiente = estatus;
    }

    /**
     * Obtiene el catálogo que representa el estado eliminado.
     *
     * @return una instancia de {@link Catalogo}
     */
    public Catalogo getEliminado() {
        return eliminado;
    }

    /**
     * Establece el catálogo que representa el estado eliminado.
     *
     * @param estatus una instancia de {@link Catalogo}
     */
    public void setEliminado(final Catalogo estatus) {
        this.eliminado = estatus;
    }

    /**
     * Obtiene el catálogo que representa el tipo de operación compra.
     *
     * @return una instancia de {@link Catalogo}
     */
    public Catalogo getCompra() {
        return compra;
    }

    /**
     * Establece el catálogo que representa el tipo de operación compra.
     *
     * @param operacion una instancia de {@link Catalogo}
     */
    public void setCompra(final Catalogo operacion) {
        this.compra = operacion;
    }

    /**
     * Obtiene la cuenta de inversión asociada.
     *
     * @return una instancia de {@link Cuenta}
     */
    public Cuenta getCuentaInversion() {
        return cuentaInversion;
    }

    /**
     * Establece la cuenta de inversión asociada.
     *
     * @param inversion una instancia de {@link Cuenta}
     */
    public void setCuentaInversion(final Cuenta inversion) {
        this.cuentaInversion = inversion;
    }

}
