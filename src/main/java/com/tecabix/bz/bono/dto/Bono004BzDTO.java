package com.tecabix.bz.bono.dto;

import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.repository.BonoAhorroRepository;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Bono004BzDTO {

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private Catalogo activo;

    /**
     * Obtiene el repositorio de bonos de ahorro.
     *
     * @return el objeto {@link BonoAhorroRepository} que gestiona el acceso a
     *         los datos de bonos de ahorro.
     */
    public BonoAhorroRepository getBonoAhorroRepository() {
        return bonoAhorroRepository;
    }

    /**
     * Establece el repositorio de bonos de ahorro.
     *
     * @param repository el objeto {@link BonoAhorroRepository} a
     *                             establecer.
     */
    public void setBonoAhorroRepository(
            final BonoAhorroRepository repository) {
        this.bonoAhorroRepository = repository;
    }

    /**
     * Obtiene el catálogo activo.
     *
     * @return el objeto {@link Catalogo} que representa el estado activo.
     */
    public Catalogo getActivo() {
        return activo;
    }

    /**
     * Establece el catálogo activo.
     *
     * @param estatus el objeto {@link Catalogo} que representa el estado activo
     *               a establecer.
     */
    public void setActivo(final Catalogo estatus) {
        this.activo = estatus;
    }
}
