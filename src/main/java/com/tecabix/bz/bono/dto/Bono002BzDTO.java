package com.tecabix.bz.bono.dto;

import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.BonoInversionRepository;
import com.tecabix.db.repository.BonoPagoRepository;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Bono002BzDTO {

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Repositorio para acceder a la entidad BonoInversion.
     */
    private BonoInversionRepository bonoInversionRepository;

    /**
     * Repositorio para acceder a la entidad BonoPago.
     */
    private BonoPagoRepository bonoPagoRepository;

    /**
     * Indica si el objeto está activo.
     */
    private Integer activo;

    /**
     * Obtiene el repositorio de Bono Ahorro.
     *
     * @return una instancia de {@link BonoAhorroRepository}
     */
    public BonoAhorroRepository getBonoAhorroRepository() {
        return bonoAhorroRepository;
    }

    /**
     * Establece el repositorio de Bono Ahorro.
     *
     * @param repository una instancia de {@link BonoAhorroRepository}
     */
    public void setBonoAhorroRepository(
            final BonoAhorroRepository repository) {
        this.bonoAhorroRepository = repository;
    }

    /**
     * Obtiene el repositorio de Bono Inversión.
     *
     * @return una instancia de {@link BonoInversionRepository}
     */
    public BonoInversionRepository getBonoInversionRepository() {
        return bonoInversionRepository;
    }

    /**
     * Establece el repositorio de Bono Inversión.
     *
     * @param repository una instancia de
     *                                {@link BonoInversionRepository}
     */
    public void setBonoInversionRepository(
            final BonoInversionRepository repository) {
        this.bonoInversionRepository = repository;
    }

    /**
     * Obtiene el repositorio de Bono Pago.
     *
     * @return una instancia de {@link BonoPagoRepository}
     */

    public BonoPagoRepository getBonoPagoRepository() {
        return bonoPagoRepository;
    }

    /**
     * Establece el repositorio de Bono Pago.
     *
     * @param repository una instancia de {@link BonoPagoRepository}
     */

    public void setBonoPagoRepository(
            final BonoPagoRepository repository) {
        this.bonoPagoRepository = repository;
    }

    /**
     * Obtiene el estado de actividad del objeto.
     *
     * @return un {@link Integer} que representa si el objeto está activo.
     */
    public Integer getActivo() {
        return activo;
    }

    /**
     * Establece el estado de actividad del objeto.
     *
     * @param estatus un {@link Integer} que representa el nuevo estado de
     *               actividad.
     */
    public void setActivo(final Integer estatus) {
        this.activo = estatus;
    }
}
