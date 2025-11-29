package com.tecabix.bz.bono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono003BzDTO;
import com.tecabix.bz.bono.dto.Bono003BzTrDTO;
import com.tecabix.bz.bono.tr.Bono003BzTr;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.BonoAhorroPeriodo;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Cuenta;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Transaccion;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.BonoRepository;
import com.tecabix.db.repository.CuentaRepository;
import com.tecabix.db.repository.TransaccionRepository;
import com.tecabix.res.b.RSB003;
import com.tecabix.sv.rq.RQSV004;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Bono003BZ {

    /**
     * Repositorio para acceder a la entidad Bono.
     */
    private final BonoRepository bonoRepository;

    /**
     * Repositorio para acceder a la entidad Cuenta.
     */
    private final CuentaRepository cuentaRepository;

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private final BonoAhorroRepository bonoAhorroRepository;

    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private final BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private final TransaccionRepository transaccionRepository;

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
     * Tipo de transacción "Compra" obtenido desde el catálogo.
     */
    private final Catalogo compra;

    /**
     * Cuenta de inversión asignada.
     */
    private final Cuenta cuentaInversion;

    /**
     * Mensaje cuando no se encuentra el bono.
     */
    private static final String NO_SE_ENCONTRO_EL_BONO;

    static {

        NO_SE_ENCONTRO_EL_BONO = "No se encontró el bono.";
        EL_ESTADO_DEBE_ESTAR_PENDIENTE = "El estado del bono debe estar como pendiente.";
        NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN = "No se encontró la cuenta de origen.";
        NO_SE_ENCONTRO_LA_CUENTA_DESTINO = "No se encontró la cuenta destino.";
        LA_CUENTA_SON_LA_MISMA = "La cuenta de origen y destino son la misma.";
        EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE = "El saldo del ordenante es insuficiente";
    }

    /**
     * Mensaje cuando el estado de bono debe estar pendiente.
     */
    private static final String EL_ESTADO_DEBE_ESTAR_PENDIENTE;

    /**
     * Mensaje cuando no se encuentra la cuenta origen.
     */
    private static final String NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN;

    /**
     * Mensaje cuando no se encuentra la cuenta destino.
     */
    private static final String NO_SE_ENCONTRO_LA_CUENTA_DESTINO;

    /**
     * Mensaje cuando la cuenta origen y destino son la misma.
     */
    private static final String LA_CUENTA_SON_LA_MISMA;

    /**
     * Mensaje cuando el saldo ordenante es insuficiente.
     */
    private static final String EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE;


    /**
     * Constante para la cuenta CTA53.
     */
    private static final String CTA14 = "CTA14";

    /**
     * Constante para la cuenta CTA53.
     */
    private static final String CTA27 = "CTA27";

    /**
     * Constante para la cuenta CTA53.
     */
    private static final String CTA53 = "CTA53";

    /**
     * Código numérico para la cuenta CTA_14.
     */
    private static final byte CTA_14 = 14;

    /**
     * Código numérico para la cuenta CTA_27.
     */
    private static final byte CTA_27 = 27;
    /**
     * Código numérico para la cuenta CTA_53.
     */
    private static final byte CTA_53 = 53;

    /**
     * Monto base predeterminado en centavos (equivale a 100.00).
     */
    private static final short MONTO = 100_00;

    /**
     * Constructor que inicializa los repositorios y propiedades del bono desde
     * el DTO.
     *
     * @param dto objeto de transferencia con los datos necesarios para la
     *            inicialización.
     */
    public Bono003BZ(final Bono003BzDTO dto) {
        this.bonoRepository = dto.getBonoRepository();
        this.cuentaRepository = dto.getCuentaRepository();
        this.bonoAhorroRepository = dto.getBonoAhorroRepository();
        this.bonoAhorroPeriodoRepository = dto.getBonoAhorroPeriodoRepository();
        this.transaccionRepository = dto.getTransaccionRepository();
        this.activo = dto.getActivo();
        this.pendiente = dto.getPendiente();
        this.eliminado = dto.getEliminado();
        this.compra = dto.getCompra();
        this.cuentaInversion = dto.getCuentaInversion();
    }

    /**
     * Realiza la compra de un Bono de Ahorro (CTA).
     * <p>
     * Este método crea un objeto {@link BonoAhorro} a partir del bono obtenido
     * mediante su clave, valida el estado del bono, determina el número de
     * aportes según el plazo (CTA14, CTA27 o CTA53) y registra una transacción
     * de compra.
     * Además, genera los períodos de ahorro y ejecuta un hilo para procesar la
     * transacción de manera asíncrona.
     * </p>
     *
     * @param rqsv004 objeto de solicitud que contiene la sesión, el bono y el
     *                contenedor de respuesta {@link RQSV004#getRsb003()}
     * @return ResponseEntity con el objeto {@link RSB003} que encapsula la
     *         transacción realizada o un error en caso de fallo
     */
    public ResponseEntity<RSB003> comprarCTA(final RQSV004 rqsv004) {

        RSB003 rsb003 = rqsv004.getRsb003();
        Sesion sesion = rqsv004.getSesion();
        UUID clave = rqsv004.getClave();

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setBono(bonoRepository.findByClave(clave).orElse(null));
        if (bonoAhorro.getBono() == null) {
            return rsb003.notFound(NO_SE_ENCONTRO_EL_BONO);
        }
        if (!bonoAhorro.getBono().getEstatus().equals(pendiente)) {
            return rsb003.notFound(EL_ESTADO_DEBE_ESTAR_PENDIENTE);
        }

        String plazo = bonoAhorro.getBono().getTipoPlazo().getNombre();
        int numeroAporte = switch (plazo) {
            case CTA14 -> CTA_14;
            case CTA27 -> CTA_27;
            case CTA53 -> CTA_53;
            default -> 0;
        };

        if (numeroAporte == 0) {
            return rsb003.notFound(NO_SE_ENCONTRO_EL_BONO);
        }

        bonoAhorro.setUsuario(sesion.getUsuario());
        bonoAhorro.setIdUsuarioModificado(sesion.getUsuario().getId());
        bonoAhorro.setFechaModificado(LocalDateTime.now());
        bonoAhorro.setEstatus(eliminado);
        bonoAhorro.setClave(UUID.randomUUID());

        Transaccion transaccion = new Transaccion();
        long idUsuario = sesion.getUsuario().getId();
        Cuenta origen = cuentaRepository.findByUsuario(idUsuario).orElse(null);
        transaccion.setOrigen(origen);
        transaccion.setDestino(cuentaInversion);
        String descripcion = bonoAhorro.getBono().getTipoPlazo().getNombre();
        transaccion.setDescripcion(descripcion);
        transaccion.setMonto(MONTO);
        transaccion.setTipo(compra);

        if (transaccion.getOrigen() == null) {
            return rsb003.notFound(NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN);
        } else if (transaccion.getDestino() == null) {
            return rsb003.notFound(NO_SE_ENCONTRO_LA_CUENTA_DESTINO);
        } else if (transaccion.getOrigen().equals(transaccion.getDestino())) {
            return rsb003.badRequest(LA_CUENTA_SON_LA_MISMA);
        }
        if (transaccion.getOrigen().getSaldo() < transaccion.getMonto()) {
            return rsb003.conflict(EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE);
        }

        bonoAhorroRepository.save(bonoAhorro);
        List<BonoAhorroPeriodo> periodos = new ArrayList<>(numeroAporte);
        LocalDate fecha = bonoAhorro.getBono().getInicio();
        for (short i = 0; i < numeroAporte; i++) {
            BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
            periodo.setBonoAhorro(bonoAhorro);
            periodo.setFechaProgramada(fecha);
            periodo.setEstatus(eliminado);
            periodo.setFechaModificado(LocalDateTime.now());
            periodo.setIdUsuarioModificado(sesion.getUsuario().getId());
            periodo.setClave(UUID.randomUUID());
            fecha = fecha.plusWeeks(1);
            periodos.add(bonoAhorroPeriodoRepository.save(periodo));
        }

        bonoAhorro.setPeriodos(periodos);

        transaccion.setEstatus(pendiente);
        transaccion.setIdUsuarioModificado(sesion.getUsuario().getId());
        transaccion.setFechaDeModificacion(LocalDateTime.now());
        transaccion.setClave(UUID.randomUUID());

        periodos.get(0).setTransaccion(transaccionRepository.save(transaccion));

        Bono003BzTrDTO dto = new Bono003BzTrDTO();
        dto.setActivo(activo);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setPendiente(pendiente);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        Bono003BzTr hilo = new Bono003BzTr(dto);
        Thread thread = new Thread(hilo);
        thread.setDaemon(true);
        thread.start();
        return rsb003.ok(transaccion);
    }
}
