package com.tecabix.bz.bono.tr;

import java.time.LocalDateTime;

import com.tecabix.bz.bono.dto.Bono006BzTrDTO;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Transaccion;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.TransaccionRepository;

public class Bono006BzTr implements Runnable {

    /**
     * Cinco segundos en milisegundos.
     */
    private static final short CINCO_SEGUNDO = 500;

    /**
     * Bono de ahorro asociado.
     */
    private final BonoAhorro bonoAhorro;

    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private final TransaccionRepository transaccionRepository;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private final Catalogo activo;

    /**
     * ID del usuario.
     */
    private final long idUsuario;

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private final BonoAhorroRepository bonoAhorroRepository;

    /**
     * Constructor que inicializa una instancia de {@code Bono006BzTr}
     * utilizando los datos proporcionados por un {@code Bono006BzTrDTO}.
     *
     * @param dto Objeto de transferencia de datos que contiene la información
     *            necesaria para inicializar la instancia.
     */
    public Bono006BzTr(final Bono006BzTrDTO dto) {

        this.bonoAhorro = dto.getBonoAhorro();
        this.transaccionRepository = dto.getTransaccionRepository();
        this.activo = dto.getActivo();
        this.idUsuario = dto.getIdUsuario();
        this.bonoAhorroRepository = dto.getBonoAhorroRepository();
    }

    /**
     * Ejecuta la lógica para activar un bono de ahorro si la transacción
     * asociada ya no está activa.
     * <p>
     * Este método espera cinco segundos antes de verificar el estado de la
     * transacción asociada al bono.
     * Si la transacción no está activa, se actualiza el estado del bono
     * a activo y se guarda la modificación en el repositorio correspondiente.
     * <p>
     * Cualquier excepción durante la ejecución es capturada.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(CINCO_SEGUNDO);
            Transaccion tx = bonoAhorro.getTransaccion();
            tx = transaccionRepository.findByClave(tx.getClave()).get();
            if (!tx.getEstatus().equals(activo)) {
                bonoAhorro.setEstatus(activo);
                bonoAhorro.setIdUsuarioModificado(idUsuario);
                bonoAhorro.setFechaModificado(LocalDateTime.now());
                bonoAhorroRepository.save(bonoAhorro);
            }
        } catch (Exception e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
    }

}
