package com.tecabix.bz.bono.tr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tecabix.bz.bono.dto.Bono005BzTrDTO;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.BonoAhorroPeriodo;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Transaccion;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.TransaccionRepository;

public class Bono005BzTr implements Runnable {

    /**
     * Cinco segundos en milisegundos.
     */
    private static final short CINCO_SEGUNDO = 500;

    /**
     * 52 Semanas = 1 año.
     */
    private static final byte ANIO_EN_SEMANA = 52;

    /**
     * Monto CTA.
     */
    private static final short MONTO = 100_00;

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private final Catalogo eliminado;

    /**
     * Período actual del bono de ahorro.
     */
    private final BonoAhorroPeriodo periodoActual;

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
     * ID del usuario.
     */
    private final long idUsuario;

    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private final BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private final BonoAhorroRepository bonoAhorroRepository;

    /**
     * Estado "Pendiente" obtenido desde el catálogo.
     */
    private final Catalogo pendiente;

    /**
     * Constructor que inicializa una instancia de {@code Bono005BzTr}
     * a partir de un objeto {@code Bono005BzTrDTO}.
     *
     * @param dto Objeto de transferencia de datos que contiene la información
     *            necesaria para inicializar la instancia.
     */
    public Bono005BzTr(final Bono005BzTrDTO dto) {

        this.eliminado = dto.getEliminado();
        this.periodoActual = dto.getPeriodoActual();
        this.activo = dto.getActivo();
        this.transaccionRepository = dto.getTransaccionRepository();
        this.bonoAhorro = dto.getBonoAhorro();
        this.idUsuario = dto.getIdUsuario();
        this.bonoAhorroPeriodoRepository = dto.getBonoAhorroPeriodoRepository();
        this.bonoAhorroRepository = dto.getBonoAhorroRepository();
        this.pendiente = dto.getPendiente();
    }

    /**
     * Ejecuta la lógica de actualización del periodo actual del bono de ahorro.
     * <p>
     * Este método espera cinco segundos antes de verificar el estado de la
     * transacción asociada al periodo actual. Si la transacción está activa, se
     * calcula el nuevo periodo, se actualiza el interés acumulado y se guarda
     * la información en los repositorios correspondientes.
     * También se eliminanlos periodos pendientes anteriores al actual.
     * Si la transacción no está activa, se desvincula del periodo.
     * <p>
     * Cualquier excepción durante la ejecución es capturada.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(CINCO_SEGUNDO);
            UUID transaccionClave;
            transaccionClave = periodoActual.getTransaccion().getClave();
            Optional<Transaccion> txOpt;
            txOpt = transaccionRepository.findByClave(transaccionClave);
            if (txOpt.get().getEstatus().equals(activo)) {
                short semana;
                List<BonoAhorroPeriodo> periodos;
                periodos = bonoAhorro.getPeriodos();
                semana = periodos.stream().max((x1, x2) -> {
                    return (int) x1.getNumero() - (int) x2.getNumero();
                }).orElse(new BonoAhorroPeriodo()).getNumero();
                periodoActual.setNumero(++semana);
                periodoActual.setFechaAportacion(LocalDate.now());
                int tasa = bonoAhorro.getBono().getTasa();
                int numAportacion = bonoAhorro.getAportacion();
                float interes = tasa / (float) MONTO;
                interes *= semana;
                interes *= numAportacion;
                interes /= (float) ANIO_EN_SEMANA;
                periodoActual.setInteres((short) interes);
                periodoActual.setEstatus(activo);
                periodoActual.setIdUsuarioModificado(idUsuario);
                periodoActual.setFechaModificado(LocalDateTime.now());
                bonoAhorroPeriodoRepository.save(periodoActual);

                int interesSum = periodos.stream().mapToInt(x -> {
                    return x.getInteres();
                }).sum();
                bonoAhorro.setInteres(interesSum);
                numAportacion = periodos.stream().filter(x -> {
                    return x.getEstatus().equals(activo);
                }).mapToInt(x -> x.getBonoAhorro().getAportacion()).sum();
                bonoAhorro.setAportaciones(numAportacion);

                bonoAhorro.setIdUsuarioModificado(idUsuario);
                bonoAhorro.setFechaModificado(LocalDateTime.now());
                bonoAhorroRepository.save(bonoAhorro);
                bonoAhorro.getPeriodos().sort((x1, x2) -> {
                    LocalDate fecha1 = x1.getFechaProgramada();
                    LocalDate fecha2 = x2.getFechaProgramada();
                    return fecha1.compareTo(fecha2);
                });

                for (BonoAhorroPeriodo periodo : bonoAhorro.getPeriodos()) {

                    if (periodo.equals(periodoActual)) {
                        break;
                    }
                    if (periodo.getEstatus().equals(pendiente)) {
                        periodo.setEstatus(eliminado);
                        bonoAhorroPeriodoRepository.save(periodo);
                    }
                }
            } else {
                periodoActual.setTransaccion(null);
                periodoActual.setIdUsuarioModificado(idUsuario);
                periodoActual.setFechaModificado(LocalDateTime.now());
                bonoAhorroPeriodoRepository.save(periodoActual);
            }
        } catch (Exception e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
    }
}
