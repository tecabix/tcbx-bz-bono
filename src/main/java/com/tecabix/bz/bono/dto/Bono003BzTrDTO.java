package com.tecabix.bz.bono.dto;

import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.TransaccionRepository;

public class Bono003BzTrDTO {

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private Catalogo activo;

    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private TransaccionRepository transaccionRepository;

    /**
     * Repositorio para acceder a la entidad Bono Ahorro.
     */
    private BonoAhorro bonoAhorro;

    /**
     * Estado "Pendiente" obtenido desde el catálogo.
     */
    private Catalogo pendiente;

    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Obtiene el catálogo activo.
     *
     * @return el catálogo activo
     */
    public Catalogo getActivo() {
        return activo;
    }

    /**
     * Establece el catálogo activo.
     *
     * @param estatus el catálogo activo a establecer
     */
    public void setActivo(final Catalogo estatus) {
        this.activo = estatus;
    }

    /**
     * Obtiene el repositorio de transacciones.
     *
     * @return el repositorio de transacciones
     */
    public TransaccionRepository getTransaccionRepository() {
        return transaccionRepository;
    }

    /**
     * Establece el repositorio de transacciones.
     *
     * @param repository el repositorio de transacciones a establecer
     */
    public void setTransaccionRepository(
            final TransaccionRepository repository) {
        this.transaccionRepository = repository;
    }

    /**
     * Obtiene el bono de ahorro.
     *
     * @return el bono de ahorro
     */
    public BonoAhorro getBonoAhorro() {
        return bonoAhorro;
    }

    /**
     * Establece el bono de ahorro.
     *
     * @param bono el bono de ahorro a establecer
     */
    public void setBonoAhorro(final BonoAhorro bono) {
        this.bonoAhorro = bono;
    }

    /**
     * Obtiene el catálogo pendiente.
     *
     * @return el catálogo pendiente
     */
    public Catalogo getPendiente() {
        return pendiente;
    }

    /**
     * Establece el catálogo pendiente.
     *
     * @param estatus el catálogo pendiente a establecer
     */
    public void setPendiente(final Catalogo estatus) {
        this.pendiente = estatus;
    }

    /**
     * Obtiene el repositorio de periodos de bono de ahorro.
     *
     * @return el repositorio de periodos de bono de ahorro
     */
    public BonoAhorroPeriodoRepository getBonoAhorroPeriodoRepository() {
        return bonoAhorroPeriodoRepository;
    }

    /**
     * Establece el repositorio de periodos de bono de ahorro.
     *
     * @param repository el repositorio a establecer
     */
    public void setBonoAhorroPeriodoRepository(
            final BonoAhorroPeriodoRepository repository) {
        this.bonoAhorroPeriodoRepository = repository;
    }

    /**
     * Obtiene el repositorio de bonos de ahorro.
     *
     * @return el repositorio de bonos de ahorro
     */
    public BonoAhorroRepository getBonoAhorroRepository() {
        return bonoAhorroRepository;
    }

    /**
     * Establece el repositorio de bonos de ahorro.
     *
     * @param repository el repositorio a establecer
     */
    public void setBonoAhorroRepository(
            final BonoAhorroRepository repository) {
        this.bonoAhorroRepository = repository;
    }
}
