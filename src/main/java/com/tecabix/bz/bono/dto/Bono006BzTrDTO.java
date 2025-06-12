package com.tecabix.bz.bono.dto;

import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.TransaccionRepository;

public class Bono006BzTrDTO {

    /**
     * Bono de ahorro asociado.
     */
    private BonoAhorro bonoAhorro;

    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private TransaccionRepository transaccionRepository;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private Catalogo activo;

    /**
     * ID del usuario.
     */
    private long idUsuario;

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;


    /**
     * Obtiene el bono de ahorro asociado.
     * @return el bono de ahorro
     */
    public BonoAhorro getBonoAhorro() {
        return bonoAhorro;
    }

    /**
     * Establece el bono de ahorro.
     *
     * @param bono el objeto BonoAhorro a establecer.
     */
    public void setBonoAhorro(final BonoAhorro bono) {
        this.bonoAhorro = bono;
    }

    /**
     * Obtiene el repositorio de transacciones.
     *
     * @return el objeto TransaccionRepository actual.
     */
    public TransaccionRepository getTransaccionRepository() {
        return transaccionRepository;
    }

    /**
     * Establece el repositorio de transacciones.
     *
     * @param repository el objeto TransaccionRepository.
     */
    public void setTransaccionRepository(
            final TransaccionRepository repository) {
        this.transaccionRepository = repository;
    }

    /**
     * Obtiene el activo del catálogo.
     *
     * @return el objeto Catalogo actual.
     */
    public Catalogo getActivo() {
        return activo;
    }

    /**
     * Establece el activo del catálogo.
     *
     * @param estatus el objeto Catalogo a establecer.
     */
    public void setActivo(final Catalogo estatus) {
        this.activo = estatus;
    }

    /**
     * Obtiene el ID del usuario.
     *
     * @return el ID del usuario.
     */
    public long getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el ID del usuario.
     *
     * @param id el ID del usuario a establecer.
     */
    public void setIdUsuario(final long id) {
        this.idUsuario = id;
    }

    /**
     * Obtiene el repositorio de bonos de ahorro.
     *
     * @return el objeto BonoAhorroRepository actual.
     */
    public BonoAhorroRepository getBonoAhorroRepository() {
        return bonoAhorroRepository;
    }

    /**
     * Establece el repositorio de bonos de ahorro.
     *
     * @param repository el objeto BonoAhorroRepository a establecer.
     */
    public void setBonoAhorroRepository(
            final BonoAhorroRepository repository) {
        this.bonoAhorroRepository = repository;
    }

}
