package com.tecabix.bz.bono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono003BzDTO;
import com.tecabix.db.entity.Bono;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Cuenta;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Transaccion;
import com.tecabix.db.entity.Usuario;
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
@ExtendWith(MockitoExtension.class)
public class Bono003BZTest {

    @Mock
    private BonoRepository bonoRepository;
    
    @Mock
    private CuentaRepository cuentaRepository;
    
    @Mock
    private BonoAhorroRepository bonoAhorroRepository;
    
    @Mock
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;
    
    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private Catalogo activo;
    
    @Mock
    private Catalogo pendiente;
    
    @Mock
    private Catalogo eliminado;
    
    @Mock
    private Catalogo compra;

    @Mock 
    private Cuenta cuentaInversion;
    
    @Mock 
    private Bono003BzDTO dto;
    
    private Bono003BZ bono003bz;

    @BeforeEach
    void setUp() {
    	Bono003BzDTO dto = new Bono003BzDTO();
    	dto.setBonoRepository(bonoRepository);
    	dto.setCuentaRepository(cuentaRepository);
    	dto.setBonoAhorroRepository(bonoAhorroRepository);
    	dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
    	dto.setTransaccionRepository(transaccionRepository);
    	dto.setActivo(activo);
    	dto.setPendiente(pendiente);
    	dto.setEliminado(eliminado);
    	dto.setCompra(compra);
    	dto.setCuentaInversion(cuentaInversion);
    	bono003bz = new Bono003BZ(dto);
    }

    private RQSV004 prepareRequest(UUID clave, RSB003 rsb003, Sesion sesion) {
        RQSV004 rq = mock(RQSV004.class);
        when(rq.getClave()).thenReturn(clave);
        when(rq.getRsb003()).thenReturn(rsb003);
        when(rq.getSesion()).thenReturn(sesion);
        return rq;
    }

    @Test
    void cuandoBonoNoExisteDebeRetornarNotFound() {
        UUID clave = UUID.randomUUID();
        RSB003 rsb003 = mock(RSB003.class);
        Sesion sesion = mock(Sesion.class);
        sesion.setId(anyLong());
        RQSV004 rq = prepareRequest(clave, rsb003, sesion);

        when(bonoRepository.findByClave(clave)).thenReturn(Optional.empty());
        ResponseEntity<RSB003> expected = ResponseEntity.notFound().build();
        when(rsb003.notFound("No se encontró el bono.")).thenReturn(expected);

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).notFound("No se encontró el bono.");
        assertEquals(expected, response);
    }

    @Test
    void cuandoEstatusNoPendientedebeRetornarNotFound() {
        UUID clave = UUID.randomUUID();
        Bono bono = mock(Bono.class);
        RSB003 rsb003 = mock(RSB003.class);
        Sesion sesion = mock(Sesion.class);
        RQSV004 rq = prepareRequest(clave, rsb003, sesion);
        
        when(bonoRepository.findByClave(clave)).thenReturn(Optional.of(bono));

        when(bono.getEstatus()).thenReturn(activo);

        ResponseEntity<RSB003> expected = ResponseEntity.notFound().build();
        when(rsb003.notFound("El estado del bono debe estar como pendiente.")).thenReturn(expected);

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).notFound("El estado del bono debe estar como pendiente.");
        assertEquals(expected, response);
    }

    @Test
    void cuandoCuentaOrigenNoExisteDebeRetornarNotFound() {
        UUID clave = UUID.randomUUID();
        RSB003 rsb003 = mock(RSB003.class);

        Sesion sesion = new Sesion();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        sesion.setUsuario(usuario);

        Bono bono = new Bono();
        bono.setEstatus(pendiente);
        Catalogo tipoPlazo = new Catalogo();
        tipoPlazo.setNombre("CTA14");
        bono.setTipoPlazo(tipoPlazo);

        when(bonoRepository.findByClave(clave)).thenReturn(Optional.of(bono));
        when(cuentaRepository.findByUsuario(1L)).thenReturn(Optional.empty());

        RQSV004 rq = prepareRequest(clave, rsb003, sesion);
        when(rsb003.notFound("No se encontró la cuenta de origen."))
            .thenReturn(ResponseEntity.notFound().build());

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).notFound("No se encontró la cuenta de origen.");
        assertTrue(response.getStatusCode().is4xxClientError());
    }


    @Test
    void cuandoSaldoInsuficienteDebeRetornarConflict() {
        UUID clave = UUID.randomUUID();
        RSB003 rsb003 = mock(RSB003.class);
        Sesion sesion = new Sesion();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        sesion.setUsuario(usuario);
        Bono bono = new Bono();
        bono.setEstatus(pendiente);
        Catalogo tipoPlazo = new Catalogo();
        tipoPlazo.setNombre("CTA14");
        bono.setTipoPlazo(tipoPlazo);
        bono.setEstatus(pendiente);
        when(bonoRepository.findByClave(clave)).thenReturn(Optional.of(bono));
        when(cuentaRepository.findByUsuario(anyLong())).thenReturn(Optional.of(new Cuenta()));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(20_00);
        cuentaOrigen.setClave(UUID.randomUUID());

        Cuenta cuentaInversion = new Cuenta();
        cuentaInversion.setId(2L);
        cuentaInversion.setSaldo(20_00);
        cuentaInversion.setClave(UUID.randomUUID());

        when(cuentaRepository.findByUsuario(1L)).thenReturn(Optional.of(cuentaOrigen));

        Bono003BzDTO dto = new Bono003BzDTO();
        dto.setBonoRepository(bonoRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setCompra(compra);
        dto.setCuentaInversion(cuentaInversion);

        bono003bz = new Bono003BZ(dto);
        
        RQSV004 rq = prepareRequest(clave, rsb003, sesion);
        when(rsb003.conflict("El saldo del ordenante es insuficiente"))
            .thenReturn(ResponseEntity.status(409).build());

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).conflict("El saldo del ordenante es insuficiente");
        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    void cuandoCompraExitosaDebeRetornarOk() {
        UUID clave = UUID.randomUUID();
        RSB003 rsb003 = mock(RSB003.class);

        Sesion sesion = new Sesion();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        sesion.setUsuario(usuario);

        Bono bono = mock(Bono.class);
        Catalogo tipoPlazo = mock(Catalogo.class);

        when(bonoRepository.findByClave(clave)).thenReturn(Optional.of(bono));
        when(bono.getEstatus()).thenReturn(pendiente);
        when(bono.getTipoPlazo()).thenReturn(tipoPlazo);
        when(tipoPlazo.getNombre()).thenReturn("CTA14");
        when(bono.getInicio()).thenReturn(LocalDate.now());

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(200_00);
        cuentaOrigen.setClave(UUID.randomUUID());

        Cuenta cuentaInversion = new Cuenta();
        cuentaInversion.setId(2L);
        cuentaInversion.setSaldo(500_00);
        cuentaInversion.setClave(UUID.randomUUID());

        when(cuentaRepository.findByUsuario(1L)).thenReturn(Optional.of(cuentaOrigen));

        Bono003BzDTO dto = new Bono003BzDTO();
        dto.setBonoRepository(bonoRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setCompra(compra);
        dto.setCuentaInversion(cuentaInversion);

        bono003bz = new Bono003BZ(dto);

        RQSV004 rq = prepareRequest(clave, rsb003, sesion);

        when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bonoAhorroPeriodoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rsb003.ok(any(Transaccion.class))).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).ok(any(Transaccion.class));
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

}
