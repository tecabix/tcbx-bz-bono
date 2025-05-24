package com.tecabix.bz.bono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono005BzDTO;
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

	private BonoAhorroRepository bonoAhorroRepository;
	private TransaccionRepository transaccionRepository;
	private CuentaRepository cuentaRepository;
	private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
	
	private Catalogo activo;
	private Catalogo pendiente;
	private Catalogo eliminado;
	private Catalogo aportacion;
	
	private Cuenta cuentaInversion;
	
	private byte ANIO_EN_SEMANA = 52;
	private short MONTO = 100_00;
	private short CINCO_SEGUNDO = 5000;

	private String NO_SE_ENCONTRO_EL_BONO = "No se encontró el bono.";
	private String EL_BONO_NO_PERTENECE_A_LA_SESION = "El bono no pertenece a la sesion.";
	private String NO_ESTA_ACTIVO_EL_BONO = "No esta activo el bono.";
	private String NO_COINCIDE_EL_PERIODO_DEL_AHORRO = "No coincide el periodo del ahorro.";
	private String EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE = "El saldo del ordenante es insuficiente";
	private String NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN = "No se encontró la cuenta de origen.";
	private String NO_SE_ENCONTRO_LA_CUENTA_DESTINO = "No se encontró la cuenta destino.";
	private String LA_CUENTA_SON_LA_MISMA = "La cuenta de origen y destino son la misma.";
	

	public Bono005BZ(Bono005BzDTO dto) {
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
        BonoAhorroPeriodo periodoActual = bonoAhorro.getPeriodos().stream()
                .filter(x -> x.getFechaProgramada().minusWeeks(1).isBefore(hoy)
                        && x.getFechaProgramada().plusDays(1).isAfter(hoy)
                        && x.getTransaccion() == null
                        && x.getNumero() == 0
                        && x.getEstatus().equals(pendiente))
                .findAny().orElse(null);

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

        Thread hilo = new Thread(() -> {
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
                        LocalDate fecha2 = x1.getFechaProgramada();
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
                e.printStackTrace();
            }
        });
        hilo.setDaemon(true);
        hilo.start();
        return rsb003.ok(transaccion);
    }
}
