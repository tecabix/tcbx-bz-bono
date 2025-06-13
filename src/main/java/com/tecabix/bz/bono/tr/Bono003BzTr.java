package com.tecabix.bz.bono.tr;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.tecabix.bz.bono.dto.Bono003BzTrDTO;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.BonoAhorroPeriodo;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Transaccion;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.TransaccionRepository;

public class Bono003BzTr implements Runnable {

    /**
     * Cinco segundos en milisegundos.
     */
    private static final short CINCO_SEGUNDO = 5000;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private final Catalogo activo;

    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private final TransaccionRepository transaccionRepository;

    /**
     * Bono de ahorro asociado.
     */
    private final BonoAhorro bonoAhorro;

    /**
     * Estado "Pendiente" obtenido desde el catálogo.
     */
    private final Catalogo pendiente;

    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private final BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private final BonoAhorroRepository bonoAhorroRepository;

    /**
     * Constructor que inicializa una instancia de {@code Bono003BzTr}
     * utilizando los valores proporcionados por un objeto
     * {@code Bono003BzTrDTO}.
     *
     * @param dto el objeto {@code Bono003BzTrDTO} que contiene los datos
     *            necesarios para inicializar esta instancia.
     */
    public Bono003BzTr(final Bono003BzTrDTO dto) {

        this.activo = dto.getActivo();
        this.transaccionRepository = dto.getTransaccionRepository();
        this.bonoAhorro = dto.getBonoAhorro();
        this.pendiente = dto.getPendiente();
        this.bonoAhorroPeriodoRepository = dto.getBonoAhorroPeriodoRepository();
        this.bonoAhorroRepository = dto.getBonoAhorroRepository();
    }

    /**
     * Ejecuta la lógica de activación o eliminación de un {@code BonoAhorro}
     * según el estado de la transacción asociada.
     *
     * <p>Si la transacción está activa, se actualiza el bono y sus periodos,
     * se establece la fecha de aportación y se guardan en sus respectivos
     *  repositorios.Si no, se eliminan.</p>
     *
     * <p>Incluye una espera de {@code CINCO_SEGUNDO} milisegundos
     * antes de iniciar.</p>
     *
     * <p>Captura y muestra cualquier excepción ocurrida durante la
     * ejecución.</p>
     */
    @Override
    public void run() {

        try {
            Thread.sleep(CINCO_SEGUNDO);
            Optional<Transaccion> clave;
            List<BonoAhorroPeriodo> periodosAhorro;
            periodosAhorro = bonoAhorro.getPeriodos();
            BonoAhorroPeriodo indexPeriodo = periodosAhorro.get(0);
            Transaccion trx = indexPeriodo.getTransaccion();
            clave = transaccionRepository.findByClave(trx.getClave());

            if (clave.isPresent() && clave.get().getEstatus().equals(activo)) {
                bonoAhorro.setEstatus(activo);
                bonoAhorro.getPeriodos().stream().forEach(x -> {
                    x.setEstatus(pendiente);
                });
                bonoAhorro.getPeriodos().get(0).setEstatus(activo);
                LocalDate hoy = LocalDate.now();
                bonoAhorro.getPeriodos().get(0).setFechaAportacion(hoy);
                bonoAhorro.getPeriodos().stream().forEach(x -> {
                    bonoAhorroPeriodoRepository.save(x);
                });
                bonoAhorro.setAportaciones(bonoAhorro.getAportacion());
                bonoAhorroRepository.save(bonoAhorro);
            } else {
                bonoAhorro.getPeriodos().stream().forEach(x -> {
                    bonoAhorroPeriodoRepository.delete(x);
                });
                bonoAhorroRepository.delete(bonoAhorro);
            }
        } catch (Exception e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }

    }

}
