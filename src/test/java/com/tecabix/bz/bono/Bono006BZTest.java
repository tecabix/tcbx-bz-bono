package com.tecabix.bz.bono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono006BzDTO;
import com.tecabix.db.entity.Bono;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Cuenta;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Transaccion;
import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.BonoAhorroPeriodoRepository;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.CuentaRepository;
import com.tecabix.db.repository.TransaccionRepository;
import com.tecabix.res.b.RSB003;
import com.tecabix.sv.rq.RQSV007;

@ExtendWith(MockitoExtension.class)
public class Bono006BZTest {

    @Mock 
    private BonoAhorroRepository bonoAhorroRepository;
    
    @Mock
    private CuentaRepository cuentaRepository;
    
    @Mock
    private TransaccionRepository transaccionRepository;
    
    @Mock
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
    
    @Mock
    private Catalogo activo, venta, pendiente, eliminado;
    
    @Mock
    private RQSV007 rqsv007;
    
    @Mock
    private RSB003 rsb003;
    
    @Mock
    private Sesion sesion;
    
    @Mock
    private Usuario usuario;

    @BeforeEach
    void setUp() {
    	
    }

    @Test
    void cuandoNoSeEncuentraElBono_retornaNotFound() {
    	
    	Bono006BzDTO dto = new Bono006BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setVenta(venta);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        Bono006BZ bono006bz = new Bono006BZ(dto);
        
        when(rqsv007.getRsb003()).thenReturn(rsb003);
        when(rqsv007.getSesion()).thenReturn(sesion);
        
        UUID clave = UUID.randomUUID();
        when(rqsv007.getClave()).thenReturn(clave);

        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.empty());

        when(rsb003.notFound(anyString()))
        .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(rsb003));
        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).notFound("No se encontró el bono.");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cuandoElBonoNoPerteneceALaSesion_retornaNotFound() {
    	
    	Bono006BzDTO dto = new Bono006BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setVenta(venta);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        Bono006BZ bono006bz = new Bono006BZ(dto);
        
        when(rqsv007.getRsb003()).thenReturn(rsb003);
        when(rqsv007.getSesion()).thenReturn(sesion);
        
        when(sesion.getUsuario()).thenReturn(usuario);
        UUID clave = UUID.randomUUID();
        when(rqsv007.getClave()).thenReturn(clave);

        BonoAhorro ben = mock(BonoAhorro.class);

        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(ben));
        Usuario usuarioOtro = new Usuario();
        usuarioOtro.setClave(UUID.randomUUID());
        
        when(ben.getUsuario()).thenReturn(usuarioOtro);

        when(rsb003.notFound(anyString()))
        .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(rsb003));
        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).notFound("El bono no pertenece a la sesión.");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cuandoElBonoNoEstaActivoRetornaNotFound() {
    	
    	Bono006BzDTO dto = new Bono006BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setVenta(venta);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        Bono006BZ bono006bz = new Bono006BZ(dto);
        
        when(rqsv007.getRsb003()).thenReturn(rsb003);
        when(rqsv007.getSesion()).thenReturn(sesion);
        
        when(sesion.getUsuario()).thenReturn(usuario);
        UUID clave = UUID.randomUUID();
        when(rqsv007.getClave()).thenReturn(clave);

        BonoAhorro ben = mock(BonoAhorro.class);

        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(ben));
        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(eliminado);

        when(rsb003.notFound(anyString()))
        .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(rsb003));
        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).notFound("No está activo el bono.");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cuandoNoSeEncuentraLaCuentaOrigenRetornaNotFound() {
    	
    	Bono006BzDTO dto = new Bono006BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setVenta(venta);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        Bono006BZ bono006bz = new Bono006BZ(dto);
        
        when(rqsv007.getRsb003()).thenReturn(rsb003);
        when(rqsv007.getSesion()).thenReturn(sesion);
        
        when(sesion.getUsuario()).thenReturn(usuario);
        UUID clave = UUID.randomUUID();
        when(rqsv007.getClave()).thenReturn(clave);

        BonoAhorro ben = mock(BonoAhorro.class);
        
        
        Bono tipoBono = mock(Bono.class);
        Catalogo tipoPlazo = mock(Catalogo.class);

        when(tipoPlazo.getNombre()).thenReturn("PLAZO");
        when(tipoBono.getTipoPlazo()).thenReturn(tipoPlazo);

        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(ben));
        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.empty());

        when(rsb003.notFound(anyString()))
        .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(rsb003));
        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).notFound("No se encontró la cuenta de origen.");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cuandoNoSeEncuentraLaCuentaDestinoRetornaNotFound() {
    	Cuenta destino = new Cuenta(); 
        destino.setSaldo(100000);
        destino.setClave(UUID.randomUUID());
    	Bono006BzDTO dto = new Bono006BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setVenta(venta);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setCuentaInversion(destino);
        Bono006BZ bono006bz = new Bono006BZ(dto);
        
        when(rqsv007.getRsb003()).thenReturn(rsb003);
        when(rqsv007.getSesion()).thenReturn(sesion);
        
        when(sesion.getUsuario()).thenReturn(usuario);
        UUID clave = UUID.randomUUID();
        when(rqsv007.getClave()).thenReturn(clave);

        BonoAhorro ben = mock(BonoAhorro.class);
        
        
        Bono tipoBono = mock(Bono.class);
        Catalogo tipoPlazo = mock(Catalogo.class);

        when(tipoPlazo.getNombre()).thenReturn("PLAZO");
        when(tipoBono.getTipoPlazo()).thenReturn(tipoPlazo);

        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(ben));
        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.empty());

        when(rsb003.notFound(anyString()))
        .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(rsb003));
        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).notFound("No se encontró la cuenta destino.");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cuandoCuentasSonIgualesRetornaConflict() {
    	Cuenta origen = new Cuenta(); 
        origen.setSaldo(100000);
        origen.setClave(UUID.randomUUID());
    	Bono006BzDTO dto = new Bono006BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setVenta(venta);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setCuentaInversion(origen);
        Bono006BZ bono006bz = new Bono006BZ(dto);
        
        when(rqsv007.getRsb003()).thenReturn(rsb003);
        when(rqsv007.getSesion()).thenReturn(sesion);
        
        when(sesion.getUsuario()).thenReturn(usuario);
        UUID clave = UUID.randomUUID();
        when(rqsv007.getClave()).thenReturn(clave);

        BonoAhorro ben = mock(BonoAhorro.class);
        
        
        Bono tipoBono = mock(Bono.class);
        Catalogo tipoPlazo = mock(Catalogo.class);

        when(tipoPlazo.getNombre()).thenReturn("PLAZO");
        when(tipoBono.getTipoPlazo()).thenReturn(tipoPlazo);

        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(ben));
        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.of(origen));

        when(rsb003.conflict(anyString()))
        .thenAnswer(inv -> ResponseEntity.status(HttpStatus.CONFLICT).body(rsb003));
        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).conflict("La cuenta de origen y destino son la misma.");
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void cuandoSaldoInsuficiente_retornaConflict() {
    	Cuenta origen = new Cuenta(); 
        origen.setSaldo(10);
        origen.setClave(UUID.randomUUID());
    	Bono006BzDTO dto = new Bono006BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setVenta(venta);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setCuentaInversion(origen);
        Bono006BZ bono006bz = new Bono006BZ(dto);
        
        when(rqsv007.getRsb003()).thenReturn(rsb003);
        when(rqsv007.getSesion()).thenReturn(sesion);
        
        when(sesion.getUsuario()).thenReturn(usuario);
        UUID clave = UUID.randomUUID();
        when(rqsv007.getClave()).thenReturn(clave);

        BonoAhorro ben = mock(BonoAhorro.class);
        
        Cuenta destino = new Cuenta();
        destino.setClave(UUID.randomUUID());
        destino.setSaldo(1_000_000);
        Bono tipoBono = mock(Bono.class);
        Catalogo tipoPlazo = mock(Catalogo.class);

        when(tipoPlazo.getNombre()).thenReturn("PLAZO");
        when(tipoBono.getTipoPlazo()).thenReturn(tipoPlazo);

        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(ben));
        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);
        when(ben.getAportaciones()).thenReturn(1000);

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.of(destino));

        when(rsb003.conflict(anyString()))
        .thenAnswer(inv -> ResponseEntity.status(HttpStatus.CONFLICT).body(rsb003));
        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).conflict("El saldo del ordenante es insuficiente");
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void cuandoTodoValidoRetornaOk() {

    	Cuenta origen = new Cuenta(); 
        origen.setSaldo(1_000_000);
        origen.setClave(UUID.randomUUID());
    	Bono006BzDTO dto = new Bono006BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setVenta(venta);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setCuentaInversion(origen);
        Bono006BZ bono006bz = new Bono006BZ(dto);
        
        when(rqsv007.getRsb003()).thenReturn(rsb003);
        when(rqsv007.getSesion()).thenReturn(sesion);
        
        
        when(rsb003.ok(any(Transaccion.class)))
            .thenAnswer(inv -> ResponseEntity.ok(rsb003));
        when(sesion.getUsuario()).thenReturn(usuario);
        UUID clave = UUID.randomUUID();
        when(rqsv007.getClave()).thenReturn(clave);

        BonoAhorro ben = mock(BonoAhorro.class);
        
        Cuenta destino = new Cuenta();
        destino.setClave(UUID.randomUUID());
        destino.setSaldo(1_000_000);
        Bono tipoBono = mock(Bono.class);
        Catalogo tipoPlazo = mock(Catalogo.class);

        when(tipoPlazo.getNombre()).thenReturn("PLAZO");
        when(tipoBono.getTipoPlazo()).thenReturn(tipoPlazo);

        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(ben));
        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);
        when(ben.getAportaciones()).thenReturn(1000);
        when(ben.getPeriodos()).thenReturn(Collections.emptyList());

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.of(destino));
        when(transaccionRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));
        when(bonoAhorroRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).ok(any(Transaccion.class));
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
