package com.tecabix.bz.bono;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono006BzDTO;
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

	private BonoAhorroRepository bonoAhorroRepository;
    private CuentaRepository cuentaRepository;
    private TransaccionRepository transaccionRepository;
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
    
    private Catalogo activo;
    private Catalogo venta;
    private Catalogo eliminado;
    private Catalogo pendiente;
    
    private Cuenta cuentaInversion;
    
    private short CINCO_SEGUNDO = 5000;
    private float INTERES = 0.15F;

    private String NO_SE_ENCONTRO_EL_BONO = "No se encontró el bono.";
    private String EL_BONO_NO_PERTENECE_A_LA_SESION = "El bono no pertenece a la sesión.";
    private String NO_ESTA_ACTIVO_EL_BONO = "No está activo el bono.";
    private String NO_SE_ENCONTRO_LA_CUENTA_DE_ORIGEN = "No se encontró la cuenta de origen.";
    private String NO_SE_ENCONTRO_LA_CUENTA_DESTINO = "No se encontró la cuenta destino.";
    private String LA_CUENTA_SON_LA_MISMA = "La cuenta de origen y destino son la misma.";
    private String EL_SALDO_DEL_ORDENANTE_ES_INSUFICIENTE = "El saldo del ordenante es insuficiente";


    public Bono006BZ(Bono006BzDTO dto) {
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
        bonoAhorro.getPeriodos().stream().forEach(x -> x.setEstatus(eliminado));
        bonoAhorro.getPeriodos().stream().forEach(x -> {
            bonoAhorroPeriodoRepository.save(x);
        });
        bonoAhorroRepository.save(bonoAhorro);

        Thread hilo = new Thread(() -> {
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
                e.printStackTrace();
            }
        });
        hilo.setDaemon(true);
        hilo.start();
        return rsb003.ok(transaccion);
    }
}
