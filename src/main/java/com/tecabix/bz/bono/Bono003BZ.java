package com.tecabix.bz.bono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono003BzDTO;
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

    private BonoRepository bonoRepository;
    private CuentaRepository cuentaRepository;
    private BonoAhorroRepository bonoAhorroRepository;
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
    private TransaccionRepository transaccionRepository;
    
    private Catalogo activo;
    private Catalogo pendiente;
    private Catalogo eliminado;
    private Catalogo compra;
    
    private Cuenta cuentaInversion;

    private String NO_SE_ENCONTRO_EL_BONO = "No se encontró el bono.";
    private String EL_ESTADO_DEBE_ESTAR_PENDIENTE = "El estado del bono debe estar como pendiente.";
    private String NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN = "No se encontró la cuenta de origen.";
    private String NO_SE_ENCONTRO_LA_CUENTA_DESTINO = "No se encontró la cuenta destino.";
    private String LA_CUENTA_SON_LA_MISMA = "La cuenta de origen y destino son la misma.";
    private String EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE = "El saldo del ordenante es insuficiente";

    private final String CTA14 = "CTA14";
    private final String CTA27 = "CTA27";
    private final String CTA53 = "CTA53";
    
    private byte CTA_14 = 14;
    private byte CTA_27 = 27;
    private byte CTA_53 = 53;
    
    private short MONTO = 100_00;
    private short CINCO_SEGUNDO = 5000;
    
    public Bono003BZ(Bono003BzDTO dto) {
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
        } if (transaccion.getOrigen().getSaldo() < transaccion.getMonto()) {
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

        Thread hilo = new Thread(() -> {
            try {
                Thread.sleep(CINCO_SEGUNDO);
                if (transaccionRepository.findByClave(bonoAhorro.getPeriodos()
                        .get(0).getTransaccion().getClave()).get().getEstatus()
                        .equals(activo)) {
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
                e.printStackTrace();
            }
        });
        hilo.setDaemon(true);
        hilo.start();
        return rsb003.ok(transaccion);
    }
}
