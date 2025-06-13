package com.tecabix.bz.bono.dto;

import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.BonoAhorroPeriodo;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.TransaccionRepository;

public class Bono005BzTrDTO {

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private Catalogo eliminado;

    /**
     * Período actual del bono de ahorro.
     */
    private BonoAhorroPeriodo periodoActual;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private Catalogo activo;

    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private TransaccionRepository transaccionRepository;

    /**
     * Bono de ahorro asociado.
     */
    private BonoAhorro bonoAhorro;

    /**
     * ID del usuario.
     */
    private long idUsuario;

    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Estado "Pendiente" obtenido desde el catálogo.
     */
    private Catalogo pendiente;

    /**
     * Obtiene el catálogo marcado como eliminado.
     * @return el catálogo eliminado
     */
    public Catalogo getEliminado() {
        return eliminado;
    }

    /**
     * Establece el catálogo eliminado.
     * @param estatus el catálogo a marcar como eliminado
     */
    public void setEliminado(final Catalogo estatus) {
        this.eliminado = estatus;
    }

    /**
     * Obtiene el período actual del bono de ahorro.
     * @return el período actual
     */
    public BonoAhorroPeriodo getPeriodoActual() {
        return periodoActual;
    }

    /**
     * Establece el período actual del bono de ahorro.
     * @param periodo el nuevo período actual
     */
    public void setPeriodoActual(final BonoAhorroPeriodo periodo) {
        this.periodoActual = periodo;
    }

    /**
     * Obtiene el catálogo activo.
     * @return el catálogo activo
     */
    public Catalogo getActivo() {
        return activo;
    }

    /**
     * Establece el catálogo activo.
     * @param estatus el catálogo a marcar como activo
     */
    public void setActivo(final Catalogo estatus) {
        this.activo = estatus;
    }

    /**
     * Obtiene el repositorio de transacciones.
     * @return el repositorio de transacciones
     */
    public TransaccionRepository getTransaccionRepository() {
        return transaccionRepository;
    }

    /**
     * Establece el repositorio de transacciones.
     * @param repository el repositorio a usar
     */
    public void setTransaccionRepository(
            final TransaccionRepository repository) {
        this.transaccionRepository = repository;
    }

    /**
     * Obtiene el bono de ahorro.
     * @return el bono de ahorro
     */
    public BonoAhorro getBonoAhorro() {
        return bonoAhorro;
    }

    /**
     * Establece el bono de ahorro.
     * @param bono el bono a asignar
     */
    public void setBonoAhorro(final BonoAhorro bono) {
        this.bonoAhorro = bono;
    }

    /**
     * Obtiene el ID del usuario.
     * @return el ID del usuario
     */
    public long getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el ID del usuario.
     * @param id el nuevo ID
     */
    public void setIdUsuario(final long id) {
        this.idUsuario = id;
    }

    /**
     * Obtiene el repositorio de períodos de bono de ahorro.
     * @return el repositorio de períodos
     */
    public BonoAhorroPeriodoRepository getBonoAhorroPeriodoRepository() {
        return bonoAhorroPeriodoRepository;
    }

    /**
     * Establece el repositorio de períodos de bono de ahorro.
     * @param repository el repositorio a usar
     */
    public void setBonoAhorroPeriodoRepository(
            final BonoAhorroPeriodoRepository repository) {
        this.bonoAhorroPeriodoRepository = repository;
    }

    /**
     * Obtiene el repositorio de bonos de ahorro.
     * @return el repositorio de bonos
     */
    public BonoAhorroRepository getBonoAhorroRepository() {
        return bonoAhorroRepository;
    }

    /**
     * Establece el repositorio de bonos de ahorro.
     * @param repository el repositorio a usar
     */
    public void setBonoAhorroRepository(
            final BonoAhorroRepository repository) {
        this.bonoAhorroRepository = repository;
    }

    /**
     * Obtiene el catálogo pendiente.
     * @return el catálogo pendiente
     */
    public Catalogo getPendiente() {
        return pendiente;
    }

    /**
     * Establece el catálogo pendiente.
     * @param estatus el catálogo a marcar como pendiente
     */
    public void setPendiente(final Catalogo estatus) {
        this.pendiente = estatus;
    }
}
