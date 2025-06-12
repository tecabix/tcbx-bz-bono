package com.tecabix.bz.bono;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono006BzDTO;
import com.tecabix.bz.bono.dto.Bono006BzTrDTO;
import com.tecabix.bz.bono.tr.Bono006BzTr;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Cuenta;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Transaccion;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.CuentaRepository;
import com.tecabix.db.repository.TransaccionRepository;
import com.tecabix.res.b.RSB003;
import com.tecabix.sv.rq.RQSV007;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Bono006BZ {

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;
    /**
     * Repositorio para acceder a la entidad Cuenta.
     */
    private CuentaRepository cuentaRepository;
    /**
     * Repositorio para acceder a la entidad Transaccion.
     */
    private TransaccionRepository transaccionRepository;
    /**
     * Repositorio para acceder a la entidad BonoAhorroPeriodo.
     */
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private Catalogo activo;
    /**
     * Tipo de transacción "Venta" obtenido desde el catálogo.
     */
    private Catalogo venta;
    /**
     * Estado "Eliminado" obtenido desde el catálogo.
     */
    private Catalogo eliminado;
    /**
     * Estado "Pendiente" obtenido desde el catálogo.
     */
    private Catalogo pendiente;

    /**
     * Cuenta de inversión asignada.
     */
    private Cuenta cuentaInversion;

    /**
     * 52 Semanas = 1 año.
     */
    private static final float INTERES = 0.15F;

    /**
     * Mensaje bono no encontrado.
     */
    private static final String NO_SE_ENCONTRO_EL_BONO;

    static {
        NO_SE_ENCONTRO_EL_BONO = "No se encontró el bono.";
        EL_BONO_NO_PERTENECE_A_LA_SESION = "El bono no pertenece a la sesión.";
        NO_ESTA_ACTIVO_EL_BONO = "No está activo el bono.";
        NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN = "No se encontró la cuenta de origen.";
        NO_SE_ENCONTRO_LA_CUENTA_DESTINO = "No se encontró la cuenta destino.";
        EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE = "El saldo del ordenante es insuficiente";
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
     * Saldo insuficiente del ordenante.
     */
    private static final String EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE;

    /**
     * Constructor que inicializa los repositorios y propiedades del bono desde
     * el DTO.
     *
     * @param dto objeto de transferencia con los datos necesarios para la
     *            inicialización.
     */
    public Bono006BZ(final Bono006BzDTO dto) {
        this.bonoAhorroRepository = dto.getBonoAhorroRepository();
        this.cuentaRepository = dto.getCuentaRepository();
        this.transaccionRepository = dto.getTransaccionRepository();
        this.bonoAhorroPeriodoRepository = dto.getBonoAhorroPeriodoRepository();
        this.activo = dto.getActivo();
        this.venta = dto.getVenta();
        this.eliminado = dto.getEliminado();
        this.pendiente = dto.getPendiente();
        this.cuentaInversion = dto.getCuentaInversion();
    }

    /**
     * Cancela (vende de forma anticipada) un Bono de Ahorro (CTA).
     * <p>
     * Verifica que el bono exista, pertenezca al usuario y se encuentre activo.
     * Luego, crea una transacción de venta anticipada, actualiza el estado del
     * bono y de sus períodos, y ejecuta un hilo asíncrono para confirmar la
     * operación.
     * </p>
     *
     * @param rqsv007 objeto de solicitud que contiene la sesión, la clave del
     *                bono y el contenedor de respuesta
     *                {@link RQSV007#getRsb003()}
     * @return ResponseEntity con el objeto {@link RSB003} que encapsula la
     *         transacción de venta o un error en caso de fallo.
     */
    public ResponseEntity<RSB003> borrarCTA(final RQSV007 rqsv007) {
        RSB003 rsb003 = rqsv007.getRsb003();
        Sesion sesion = rqsv007.getSesion();
        UUID clave = rqsv007.getClave();

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
        Transaccion transaccion = new Transaccion();
        transaccion.setOrigen(cuentaInversion);
        long idUsuario = sesion.getUsuario().getId();
        Cuenta destino = cuentaRepository.findByUsuario(idUsuario).orElse(null);
        transaccion.setDestino(destino);
        String nombrePlazo = bonoAhorro.getBono().getTipoPlazo().getNombre();
        transaccion.setDescripcion("VENTA ANTICIPADA DE " + nombrePlazo);

        if (transaccion.getOrigen() == null) {
            return rsb003.notFound(NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN);
        } else if (transaccion.getDestino() == null) {
            return rsb003.notFound(NO_SE_ENCONTRO_LA_CUENTA_DESTINO);
        } else if (transaccion.getOrigen().equals(transaccion.getDestino())) {
            return rsb003.conflict(LA_CUENTA_SON_LA_MISMA);
        }

        int monto = (int) (bonoAhorro.getAportaciones() * (1 - INTERES));
        transaccion.setMonto(monto);
        transaccion.setTipo(venta);

        if (transaccion.getOrigen().getSaldo() < transaccion.getMonto()) {
            return rsb003.conflict(EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE);
        }
        transaccion.setEstatus(pendiente);
        transaccion.setIdUsuarioModificado(idUsuario);
        transaccion.setFechaDeModificacion(LocalDateTime.now());
        transaccion.setClave(UUID.randomUUID());

        bonoAhorro.setTransaccion(transaccionRepository.save(transaccion));
        bonoAhorro.setEstatus(eliminado);
        bonoAhorro.setIdUsuarioModificado(idUsuario);
        bonoAhorro.setFechaModificado(LocalDateTime.now());
        for (var x : bonoAhorro.getPeriodos()) {
            x.setEstatus(eliminado);
        }
        for (var x : bonoAhorro.getPeriodos()) {
            bonoAhorroPeriodoRepository.save(x);
        }

        bonoAhorroRepository.save(bonoAhorro);

        Bono006BzTrDTO dto = new Bono006BzTrDTO();
        dto.setBonoAhorro(bonoAhorro);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setActivo(activo);
        dto.setIdUsuario(idUsuario);
        dto.setBonoAhorroRepository(bonoAhorroRepository);

        Bono006BzTr hilo = new Bono006BzTr(dto);
        Thread thread = new Thread(hilo);
        thread.setDaemon(true);
        thread.start();
        return rsb003.ok(transaccion);
    }
}
