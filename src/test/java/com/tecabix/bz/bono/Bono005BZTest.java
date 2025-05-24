package com.tecabix.bz.bono;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono005BzDTO;
import com.tecabix.db.entity.Bono;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.BonoAhorroPeriodo;
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
import com.tecabix.sv.rq.RQSV006;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
@ExtendWith(MockitoExtension.class)
public class Bono005BZTest {

    @Mock
    private BonoAhorroRepository bonoAhorroRepository;
    
    @Mock
    private TransaccionRepository transaccionRepository;
    
    @Mock 
    private CuentaRepository cuentaRepository;
    
    @Mock
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
    
    @Mock
    private Catalogo activo;
    
    @Mock
    private Catalogo pendiente;
    
    @Mock
    private Catalogo eliminado;
    
    @Mock
    private Catalogo aportacion;
    
    @Mock
    private Cuenta cuentaInversion;

    private Bono005BZ bono005bz;

    @BeforeEach
    void setUp() {
        Bono005BzDTO dto = new Bono005BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setAportacion(aportacion);
        dto.setCuentaInversion(cuentaInversion);
        bono005bz = new Bono005BZ(dto);
    }

    @Test
    void cuandoNoSeEncuentraElBono_retornaNotFound() {
        // arrange
        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(mock(Sesion.class));
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.empty());

        ResponseEntity<RSB003> esperado = ResponseEntity.status(404).build();
        when(rsb.notFound("No se encontró el bono.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("No se encontró el bono.");
    }

    @Test
    void cuandoBonoNoPerteneceASesion_retornaNotFound() {
        // arrange
        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuarioSesion = new Usuario(); 
        usuarioSesion.setClave(UUID.randomUUID());
        Sesion sesion = new Sesion(); 
        sesion.setUsuario(usuarioSesion);
        BonoAhorro bonoAhorro = new BonoAhorro();
        Usuario otroUsuario = new Usuario();
        otroUsuario.setClave(UUID.randomUUID());
        bonoAhorro.setUsuario(otroUsuario);
        
        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(bonoAhorro));

        ResponseEntity<RSB003> esperado = ResponseEntity.status(404).build();
        when(rsb.notFound("El bono no pertenece a la sesion.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("El bono no pertenece a la sesion.");
    }

    @Test
    void cuandoEstatusNoEsActivo_retornaNotFound() {
        // arrange
        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario(); usuario.setId(1L);
        Sesion sesion = new Sesion(); sesion.setUsuario(usuario);
        BonoAhorro bonoAhorro = mock(BonoAhorro.class);

        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(bonoAhorro));
        when(bonoAhorro.getUsuario()).thenReturn(usuario);
        when(bonoAhorro.getEstatus()).thenReturn(pendiente);

        ResponseEntity<RSB003> esperado = ResponseEntity.status(404).build();
        when(rsb.notFound("No esta activo el bono.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("No esta activo el bono.");
    }

    @Test
    void cuandoNoHayPeriodoPendiente_retornaNotFound() {
        // arrange
        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario(); usuario.setId(1L);
        Sesion sesion = new Sesion(); sesion.setUsuario(usuario);
        BonoAhorro bonoAhorro = mock(BonoAhorro.class);

        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(bonoAhorro));
        when(bonoAhorro.getUsuario()).thenReturn(usuario);
        when(bonoAhorro.getEstatus()).thenReturn(activo);
        when(bonoAhorro.getPeriodos()).thenReturn(Collections.emptyList());

        ResponseEntity<RSB003> esperado = ResponseEntity.status(404).build();
        when(rsb.notFound("No coincide el periodo del ahorro.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("No coincide el periodo del ahorro.");
    }

    @Test
    void cuandoNoSeEncuentraCuentaOrigen_retornaNotFound() {
        // arrange
        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario(); usuario.setId(1L);
        Sesion sesion = new Sesion(); sesion.setUsuario(usuario);
        BonoAhorro bonoAhorro = new BonoAhorro();
        Bono bono = new Bono();
        bono.setTipoPlazo(new Catalogo());
        bono.getTipoPlazo().setNombre("CTA");
        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setEstatus(pendiente);
        bonoAhorro.setPeriodos(List.of(periodo));
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setBono(bono);

        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave)).thenReturn(Optional.of(bonoAhorro));
        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.empty());

        ResponseEntity<RSB003> esperado = ResponseEntity.status(404).build();
        when(rsb.notFound("No se encontró la cuenta de origen.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("No se encontró la cuenta de origen.");
    }

    @Test
    void cuandoSaldoInsuficiente_retornaConflict() {
        // arrange
        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario(); 
        usuario.setId(1L);
        usuario.setClave(UUID.randomUUID());
        Sesion sesion = new Sesion(); sesion.setUsuario(usuario);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short)5000);
        
        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setEstatus(pendiente);
        
        bonoAhorro.setPeriodos(List.of(periodo));
        Bono bono = new Bono();
        bono.setTipoPlazo(new Catalogo());
        bono.getTipoPlazo().setNombre("CTA");
        bonoAhorro.setBono(bono);

        Cuenta origen = new Cuenta();
        origen.setId(1L);
        origen.setSaldo(1000);
        origen.setClave(UUID.randomUUID());
        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.of(origen));
        
        Cuenta cuentaInversion = new Cuenta();
        cuentaInversion.setId(2L);
        cuentaInversion.setSaldo(500_00);
        cuentaInversion.setClave(UUID.randomUUID());
        
        Bono005BzDTO dto = new Bono005BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setAportacion(aportacion);
        dto.setCuentaInversion(cuentaInversion);
        Bono005BZ bono005bz = new Bono005BZ(dto);

        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        ResponseEntity<RSB003> esperado = ResponseEntity.status(409).build();
        when(rsb.conflict("El saldo del ordenante es insuficiente"))
            .thenReturn(esperado);
        
        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).conflict("El saldo del ordenante es insuficiente");
    }

    @Test
    void cuandoTodoEsValido_retornaOkConTransaccion() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsbReal = new RSB003();
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario(); usuario.setId(1L);
        Sesion sesion = new Sesion(); sesion.setUsuario(usuario);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short)5000);
        Catalogo tipoPlazo = mock(Catalogo.class);
        when(tipoPlazo.getNombre()).thenReturn("PLAZO_TEST");
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setTransaccion(null);
        periodo.setNumero((short)0);
        periodo.setEstatus(pendiente);
        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(200_00);
        cuentaOrigen.setClave(UUID.randomUUID());

        Cuenta cuentaInversion = new Cuenta();
        cuentaInversion.setId(2L);
        cuentaInversion.setSaldo(500_00);
        cuentaInversion.setClave(UUID.randomUUID());
        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.of(cuentaOrigen));
        
        Bono005BzDTO dto = new Bono005BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setAportacion(aportacion);
        dto.setCuentaInversion(cuentaInversion);
        Bono005BZ bono005bz = new Bono005BZ(dto);

        when(rq.getRsb003()).thenReturn(rsbReal);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));
        when(transaccionRepository.save(any(Transaccion.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<RSB003> respuesta = bono005bz.aportarCTA(rq);

        assertNotNull(respuesta, "La respuesta no debe ser null");
        assertEquals(200, respuesta.getStatusCodeValue(), "Debe retornar HTTP 200 OK");
        assertNotNull(respuesta.getBody(), "El cuerpo de RSB003 no debe ser null");
        verify(transaccionRepository).save(any(Transaccion.class));
        verify(bonoAhorroPeriodoRepository).save(periodo);
    }
}
