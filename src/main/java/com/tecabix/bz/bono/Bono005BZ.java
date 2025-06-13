package com.tecabix.bz.bono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono005BzDTO;
import com.tecabix.bz.bono.dto.Bono005BzTrDTO;
import com.tecabix.bz.bono.tr.Bono005BzTr;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.BonoAhorroPeriodo;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Cuenta;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Transaccion;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.CuentaRepository;
import com.tecabix.db.repository.TransaccionRepository;
import com.tecabix.res.b.RSB003;
import com.tecabix.sv.rq.RQSV006;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Bono005BZ {

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private final BonoAhorroRepository bonoAhorroRepository;

    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private final TransaccionRepository transaccionRepository;

    /**
     * Repositorio para acceder a la entidad Cuenta.
     */
    private final CuentaRepository cuentaRepository;

    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private final BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private final Catalogo activo;

    /**
     * Estado "Pendiente" obtenido desde el catálogo.
     */
    private final Catalogo pendiente;

    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private final Catalogo eliminado;

    /**
     * Tipo de transacción "Aportación" obtenido desde el catálogo.
     */
    private final Catalogo aportacion;

    /**
     * Cuenta de inversión asignada.
     */
    private final Cuenta cuentaInversion;

    /**
     * Bono no encontrado.
     */
    private static final String NO_SE_ENCONTRO_EL_BONO;

    static {
        NO_SE_ENCONTRO_EL_BONO = "No se encontró el bono.";
        EL_BONO_NO_PERTENECE_A_LA_SESION = "El bono no pertenece a la sesion.";
        NO_COINCIDE_EL_PERIODO_DEL_AHORRO = "No coincide el periodo del ahorro.";
        EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE = "El saldo del ordenante es insuficiente";
        NO_ESTA_ACTIVO_EL_BONO = "No esta activo el bono.";
        NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN = "No se encontró la cuenta de origen.";
        NO_SE_ENCONTRO_LA_CUENTA_DESTINO = "No se encontró la cuenta destino.";
        LA_CUENTA_SON_LA_MISMA = "La cuenta de origen y destino son la misma.";
    }

    /**
     * Bono no pertenece a la sesión.
     */
    private static final String EL_BONO_NO_PERTENECE_A_LA_SESION;

    /**
     * Bono no está activo.
     */
    private static final String NO_ESTA_ACTIVO_EL_BONO;

    /**
     * Periodo de ahorro no coincide.
     */
    private static final String NO_COINCIDE_EL_PERIODO_DEL_AHORRO;

    /**
     * Saldo insuficiente del ordenante.
     */
    private static final String EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE;

    /**
     * Cuenta de origen no encontrada.
     */
    private static final String NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN;

    /**
     * Cuenta destino no encontrada.
     */
    private static final String NO_SE_ENCONTRO_LA_CUENTA_DESTINO;

    /**
     * Origen y destino son iguales.
     */
    private static final String LA_CUENTA_SON_LA_MISMA;

    /**
     * Constructor que inicializa los repositorios y propiedades del bono desde
     * el DTO.
     *
     * @param dto objeto de transferencia con los datos necesarios para la
     *            inicialización.
     */
    public Bono005BZ(final Bono005BzDTO dto) {
        this.bonoAhorroRepository = dto.getBonoAhorroRepository();
        this.transaccionRepository = dto.getTransaccionRepository();
        this.cuentaRepository = dto.getCuentaRepository();
        this.bonoAhorroPeriodoRepository = dto.getBonoAhorroPeriodoRepository();
        this.activo = dto.getActivo();
        this.pendiente = dto.getPendiente();
        this.eliminado = dto.getEliminado();
        this.aportacion = dto.getAportacion();
        this.cuentaInversion = dto.getCuentaInversion();
    }

    /**
     * Realiza un aporte a un Bono de Ahorro (CTA) activo.
     * <p>
     * Verifica que el bono exista, que pertenezca al usuario y que esté activo.
     * Luego, identifica el período actual para el aporte y crea una transacción
     * de aporte.
     * Se ejecuta un hilo asíncrono para actualizar el estado del período y
     * del bono según el resultado de la transacción.
     * </p>
     *
     * @param rqsv006 objeto de solicitud que contiene la sesión, la clave del
     *                bono y el contenedor de respuesta
     *                {@link RQSV006#getRsb003()}
     * @return ResponseEntity con el objeto {@link RSB003} que encapsula la
     *         transacción del aporte o un error en caso de fallo.
     */
    public ResponseEntity<RSB003> aportarCTA(final RQSV006 rqsv006) {

        RSB003 rsb003 = rqsv006.getRsb003();
        Sesion sesion = rqsv006.getSesion();
        UUID clave = rqsv006.getClave();

        BonoAhorro bonoAhorro;
        bonoAhorro = bonoAhorroRepository.findByClave(clave).orElse(null);
        if (bonoAhorro == null) {
            return rsb003.notFound(NO_SE_ENCONTRO_EL_BONO);
        }
        if (!bonoAhorro.getUsuario().equals(sesion.getUsuario())) {
            return rsb003.notFound(EL_BONO_NO_PERTENECE_A_LA_SESION);
        }
        if (!bonoAhorro.getEstatus().equals(activo)) {
            return rsb003.notFound(NO_ESTA_ACTIVO_EL_BONO);
        }
        LocalDate hoy = LocalDate.now();
        Predicate<BonoAhorroPeriodo> predicateAhorro;
        predicateAhorro = x -> isPeriodoCTA(x, hoy);

        BonoAhorroPeriodo periodoActual;
        List<BonoAhorroPeriodo> periodosAhorro;
        periodosAhorro = bonoAhorro.getPeriodos();
        Stream<BonoAhorroPeriodo> filtroPeriodos;
        filtroPeriodos = periodosAhorro.stream().filter(predicateAhorro);
        periodoActual = filtroPeriodos.findAny().orElse(null);

        if (periodoActual == null) {
            return rsb003.notFound(NO_COINCIDE_EL_PERIODO_DEL_AHORRO);
        }

        Transaccion transaccion = new Transaccion();
        long idUsuario = sesion.getUsuario().getId();
        Cuenta origen = cuentaRepository.findByUsuario(idUsuario).orElse(null);
        transaccion.setOrigen(origen);
        transaccion.setDestino(cuentaInversion);
        String descripcion = bonoAhorro.getBono().getTipoPlazo().getNombre();
        transaccion.setDescripcion(descripcion);

        if (transaccion.getOrigen() == null) {
            return rsb003.notFound(NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN);
        } else if (transaccion.getDestino() == null) {
            return rsb003.notFound(NO_SE_ENCONTRO_LA_CUENTA_DESTINO);
        } else if (transaccion.getOrigen().equals(transaccion.getDestino())) {
            return rsb003.conflict(LA_CUENTA_SON_LA_MISMA);
        }
        transaccion.setMonto(bonoAhorro.getAportacion());
        transaccion.setTipo(aportacion);
        if (transaccion.getOrigen().getSaldo() < transaccion.getMonto()) {
            return rsb003.conflict(EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE);
        }
        transaccion.setEstatus(pendiente);
        transaccion.setIdUsuarioModificado(sesion.getUsuario().getId());
        transaccion.setFechaDeModificacion(LocalDateTime.now());
        transaccion.setClave(UUID.randomUUID());

        periodoActual.setTransaccion(transaccionRepository.save(transaccion));
        periodoActual.setIdUsuarioModificado(sesion.getUsuario().getId());
        periodoActual.setFechaModificado(LocalDateTime.now());
        bonoAhorroPeriodoRepository.save(periodoActual);

        Bono005BzTrDTO dto = new Bono005BzTrDTO();
        dto.setEliminado(eliminado);
        dto.setPeriodoActual(periodoActual);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setIdUsuario(idUsuario);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setPendiente(pendiente);
        dto.setActivo(activo);
        Bono005BzTr hilo = new Bono005BzTr(dto);
        Thread thread = new Thread(hilo);
        thread.setDaemon(true);
        thread.start();
        return rsb003.ok(transaccion);
    }

    /**
     * Verifica si un periodo de bono ahorro cumple con las condiciones para ser
     * considerado como periodo CTA.
     *
     * @param x para el periodo del bono.
     * @param hoy la fecha actual.
     * @return {@code true} si el periodo está dentro del rango de fechas
     *                      permitido, no tiene transacción, su número es 0
     *                      y su estatus es igual al valor pendiente;
     *                      de lo contrario, {@code false}.
     */
    public boolean isPeriodoCTA(final BonoAhorroPeriodo x,
        final LocalDate hoy) {

        boolean fechaAntesHoy;
        fechaAntesHoy = x.getFechaProgramada().minusWeeks(1).isBefore(hoy);
        boolean fechaDespuesHoy;
        fechaDespuesHoy = x.getFechaProgramada().plusDays(1).isAfter(hoy);
        return  fechaAntesHoy && fechaDespuesHoy && x.getTransaccion() == null
                && x.getNumero() == 0
                && x.getEstatus().equals(pendiente);
    }
}
