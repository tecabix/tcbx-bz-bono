package com.tecabix.bz.bono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.tecabix.bz.bono.dto.Bono005BzTrDTO;
import com.tecabix.bz.bono.tr.Bono005BzTr;
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

    /**
     * Repositorio simulado para pruebas de bonos de ahorro.
     */
    @Mock
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Repositorio simulado para pruebas de transacciones.
     */
    @Mock
    private TransaccionRepository transaccionRepository;

    /**
     * Repositorio simulado para pruebas de cuentas.
     */
    @Mock
    private CuentaRepository cuentaRepository;

    /**
     * Repositorio simulado para pruebas de periodos de bonos de ahorro.
     */
    @Mock
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Catálogo simulado que representa el estado 'activo'.
     */
    @Mock
    private Catalogo activo;

    /**
     * Catálogo simulado que representa el estado 'pendiente'.
     */
    @Mock
    private Catalogo pendiente;

    /**
     * Catálogo simulado que representa el estado 'eliminado'.
     */
    @Mock
    private Catalogo eliminado;

    /**
     * Catálogo de aportaciones utilizado en las pruebas.
     */
    @Mock
    private Catalogo aportacion;

    /**
     * Cuenta de inversión asociada al bono.
     */
    @Mock
    private Cuenta cuentaInversion;

    /**
     * Instancia de la clase bajo prueba.
     */
    private Bono005BZ bono005bz;

    /**
     * Estatus Http OK.
     */
    private static final int OK = 200;

    /**
     * Estatus Http NOTFOUND.
     */
    private static final int NOTFOUND = 404;

    /**
     * Estatus Http CONFLICT.
     */
    private static final int CONFLICT = 409;

    /**
     * Representa al número cinco mil.
     */
    private static final int CINCOMIL = 5000;

    /**
     * Representa al saldo cuando vale 200.
     */
    private static final int SALDODOSCIENTOS = 200_00;

    /**
     * Representa al saldo cuando vale 500.
     */
    private static final int SALDOQUINIENTOS = 500_00;

    /**
     * Representa el saldo cuando vale 1000.
     */
    private static final int SALDOMIL = 1000;

    /**
     * Representa al número tres.
     */
    private static final int TRES = 3;

    /**
     * Representa al número catorce.
     */
    private static final int CATORCE = 14;

    /**
     * Valor de la tasa por defecto.
     */
    private static final int TASA = 10;



    /**
     * Configura los mocks y la instancia de Bono005BZ antes de cada prueba.
     */
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
    void cuandoNoSeEncuentraElBonoRetornaNotFound() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(mock(Sesion.class));
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.empty());

        ResponseEntity<RSB003> esperado;
        esperado = ResponseEntity.status(NOTFOUND).build();
        when(rsb.notFound("No se encontró el bono.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("No se encontró el bono.");
    }

    @Test
    void cuandoBonoNoPerteneceASesionRetornaNotFound() {

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
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        ResponseEntity<RSB003> esperado;
        esperado = ResponseEntity.status(NOTFOUND).build();
        when(rsb.notFound("El bono no pertenece a la sesion.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("El bono no pertenece a la sesion.");
    }

    @Test
    void cuandoEstatusNoEsActivoRetornaNotFound() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);
        BonoAhorro bonoAhorro = mock(BonoAhorro.class);

        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        when(bonoAhorro.getUsuario()).thenReturn(usuario);
        when(bonoAhorro.getEstatus()).thenReturn(pendiente);

        ResponseEntity<RSB003> esperado;
        esperado = ResponseEntity.status(NOTFOUND).build();
        when(rsb.notFound("No esta activo el bono.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("No esta activo el bono.");
    }

    @Test
    void cuandoNoHayPeriodoPendienteRetornaNotFound() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);
        BonoAhorro bonoAhorro = mock(BonoAhorro.class);

        List<BonoAhorroPeriodo> periodos = bonoAhorro.getPeriodos();
        periodos.add(new BonoAhorroPeriodo());
        LocalDate hoy = LocalDate.now();
        periodos.get(0).setFechaProgramada(hoy.plusDays(2));
        periodos.get(0).setTransaccion(null);
        periodos.get(0).setNumero((short) 0);
        periodos.get(0).setEstatus(pendiente);
        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        when(bonoAhorro.getUsuario()).thenReturn(usuario);
        when(bonoAhorro.getEstatus()).thenReturn(activo);
        when(bonoAhorro.getPeriodos()).thenReturn(Collections.emptyList());

        ResponseEntity<RSB003> esperado;
        esperado = ResponseEntity.status(NOTFOUND).build();
        when(rsb.notFound("No coincide el periodo del ahorro.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);
        assertTrue(bono005bz.isPeriodoCTA(periodos.get(0), hoy));

        periodos.get(0).setFechaProgramada(hoy.plusDays(CATORCE));
        periodos.get(0).setTransaccion(null);
        periodos.get(0).setNumero((short) 0);
        periodos.get(0).setEstatus(pendiente);
        assertFalse(bono005bz.isPeriodoCTA(periodos.get(0), hoy));

        periodos.get(0).setFechaProgramada(hoy.minusDays(2));
        periodos.get(0).setTransaccion(null);
        periodos.get(0).setNumero((short) 0);
        periodos.get(0).setEstatus(pendiente);
        assertFalse(bono005bz.isPeriodoCTA(periodos.get(0), hoy));

        periodos.get(0).setFechaProgramada(hoy.plusDays(2));
        periodos.get(0).setTransaccion(null);
        periodos.get(0).setNumero((short) 0);
        periodos.get(0).setEstatus(activo);
        assertFalse(bono005bz.isPeriodoCTA(periodos.get(0), hoy));

        Transaccion trx = new Transaccion();
        periodos.get(0).setFechaProgramada(hoy.plusDays(2));
        periodos.get(0).setTransaccion(trx);
        periodos.get(0).setNumero((short) 0);
        periodos.get(0).setEstatus(pendiente);
        assertFalse(bono005bz.isPeriodoCTA(periodos.get(0), hoy));

        periodos.get(0).setFechaProgramada(hoy.plusDays(2));
        periodos.get(0).setTransaccion(null);
        periodos.get(0).setNumero((short) 1);
        periodos.get(0).setEstatus(pendiente);
        assertFalse(bono005bz.isPeriodoCTA(periodos.get(0), hoy));

        assertSame(esperado, actual);
        verify(rsb).notFound("No coincide el periodo del ahorro.");
    }

    @Test
    void cuandoNoSeEncuentraCuentaOrigenRetornaNotFound() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);
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
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.empty());

        ResponseEntity<RSB003> esperado;
        esperado = ResponseEntity.status(NOTFOUND).build();
        when(rsb.notFound("No se encontró la cuenta de origen.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("No se encontró la cuenta de origen.");
    }

    @Test
    void cuandoSaldoInsuficienteRetornaConflict() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setClave(UUID.randomUUID());
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);

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
        origen.setSaldo(SALDOMIL);
        origen.setClave(UUID.randomUUID());
        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.of(origen));

        Cuenta inversion = new Cuenta();
        inversion.setId(2L);
        inversion.setSaldo(SALDOQUINIENTOS);
        inversion.setClave(UUID.randomUUID());

        Bono005BzDTO dto = new Bono005BzDTO();
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        dto.setTransaccionRepository(transaccionRepository);
        dto.setCuentaRepository(cuentaRepository);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setAportacion(aportacion);
        dto.setCuentaInversion(inversion);
        Bono005BZ bono005Bz = new Bono005BZ(dto);

        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        ResponseEntity<RSB003> esperado;
        esperado = ResponseEntity.status(CONFLICT).build();
        when(rsb.conflict("El saldo del ordenante es insuficiente")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005Bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).conflict("El saldo del ordenante es insuficiente");
    }

    @Test
    void cuandoTodoEsValidoRetornaOkConTransaccion() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsbReal = new RSB003();
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        Catalogo tipoPlazo = mock(Catalogo.class);
        when(tipoPlazo.getNombre()).thenReturn("PLAZO_TEST");
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setTransaccion(null);
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(SALDODOSCIENTOS);
        cuentaOrigen.setClave(UUID.randomUUID());

        Cuenta inversion = new Cuenta();
        inversion.setId(2L);
        inversion.setSaldo(SALDOQUINIENTOS);
        inversion.setClave(UUID.randomUUID());
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
        dto.setCuentaInversion(inversion);
        Bono005BZ bono005Bz = new Bono005BZ(dto);

        when(rq.getRsb003()).thenReturn(rsbReal);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        when(transaccionRepository.save(any(Transaccion.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<RSB003> respuesta = bono005Bz.aportarCTA(rq);

        assertNotNull(respuesta, "La respuesta no debe ser null");
        assertEquals(OK, respuesta.getStatusCodeValue(), "Debe retornar HTTP 200 OK");
        assertNotNull(respuesta.getBody(), "El cuerpo de RSB003 no debe ser null");
        verify(transaccionRepository).save(any(Transaccion.class));
        verify(bonoAhorroPeriodoRepository).save(periodo);
    }

    @Test
    void cuandoNoSeEncuentraDestino() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        Catalogo tipoPlazo = mock(Catalogo.class);
        when(tipoPlazo.getNombre()).thenReturn("PLAZO_TEST");
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setTransaccion(null);
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(SALDODOSCIENTOS);
        cuentaOrigen.setClave(UUID.randomUUID());
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

        Bono005BZ bono005Bz = new Bono005BZ(dto);

        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        ResponseEntity<RSB003> esperado;
        esperado = ResponseEntity.status(NOTFOUND).build();
        when(rsb.notFound("No se encontró la cuenta destino.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005Bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).notFound("No se encontró la cuenta destino.");

    }

    @Test
    void cuandoCuentaOrigenDestinoIguales() {

        RQSV006 rq = mock(RQSV006.class);
        RSB003 rsb = mock(RSB003.class);
        UUID clave = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        Catalogo tipoPlazo = mock(Catalogo.class);
        when(tipoPlazo.getNombre()).thenReturn("PLAZO_TEST");
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setTransaccion(null);
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(SALDODOSCIENTOS);
        cuentaOrigen.setClave(UUID.randomUUID());
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
        dto.setCuentaInversion(cuentaOrigen);

        Bono005BZ bono005Bz = new Bono005BZ(dto);

        when(rq.getRsb003()).thenReturn(rsb);
        when(rq.getSesion()).thenReturn(sesion);
        when(rq.getClave()).thenReturn(clave);
        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));

        ResponseEntity<RSB003> esperado;
        esperado = ResponseEntity.status(CONFLICT).build();
        when(rsb.conflict("La cuenta de origen y destino son la misma.")).thenReturn(esperado);

        ResponseEntity<RSB003> actual = bono005Bz.aportarCTA(rq);

        assertSame(esperado, actual);
        verify(rsb).conflict("La cuenta de origen y destino son la misma.");

    }

    @Test
    void cuandoRunEsEjecutadoDirectamente() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now().minusWeeks(2));
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        periodo.setBonoAhorro(bonoAhorro);
        periodo.setClave(UUID.randomUUID());

        BonoAhorroPeriodo periodo2 = new BonoAhorroPeriodo();
        periodo2.setFechaProgramada(LocalDate.now());
        periodo2.setNumero((short) TRES);
        periodo2.setEstatus(pendiente);
        periodo2.setBonoAhorro(bonoAhorro);
        periodo.setClave(UUID.randomUUID());

        Transaccion transaccion = new Transaccion();
        transaccion.setClave(UUID.randomUUID());
        transaccion.setEstatus(activo);
        periodo2.setTransaccion(transaccion);

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(any()))
            .thenReturn(Optional.of(transaccion));

        Bono005BzTrDTO dto = new Bono005BzTrDTO();
        dto.setPeriodoActual(periodo2);
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setIdUsuario(usuario.getId());
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setPeriodoActual(periodo2);

        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo, periodo2)));

        // Ejecutar directamente run()
        Bono005BzTr hilo = new Bono005BzTr(dto);
        hilo.run();
    }

    @Test
    void cuandoRunEsEjecutadoDirectamenteCatch() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now().minusWeeks(2));
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        periodo.setBonoAhorro(bonoAhorro);

        BonoAhorroPeriodo periodo2 = new BonoAhorroPeriodo();
        periodo2.setFechaProgramada(LocalDate.now());
        periodo2.setNumero((short) TRES);
        periodo2.setEstatus(pendiente);
        periodo2.setBonoAhorro(bonoAhorro);

        Transaccion transaccion = new Transaccion();
        transaccion.setClave(UUID.randomUUID());
        transaccion.setEstatus(activo);
        periodo2.setTransaccion(transaccion);

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(any()))
            .thenThrow(new RuntimeException("Error forzado"));

        Bono005BzTrDTO dto = new Bono005BzTrDTO();
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);
        dto.setPeriodoActual(periodo2);

        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo, periodo2)));

        // Ejecutar directamente run()
        Bono005BzTr hilo = new Bono005BzTr(dto);

        hilo.run();
    }

    @Test
    void cuandoRunEsEjecutadoDirectamenteTrxPendiente() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now().minusWeeks(2));
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        periodo.setBonoAhorro(bonoAhorro);
        periodo.setClave(UUID.randomUUID());

        BonoAhorroPeriodo periodo2 = new BonoAhorroPeriodo();
        periodo2.setFechaProgramada(LocalDate.now());
        periodo2.setNumero((short) TRES);
        periodo2.setEstatus(pendiente);
        periodo2.setBonoAhorro(bonoAhorro);
        periodo.setClave(UUID.randomUUID());

        Transaccion transaccion = new Transaccion();
        transaccion.setClave(UUID.randomUUID());
        transaccion.setEstatus(pendiente);
        periodo2.setTransaccion(transaccion);

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(any()))
            .thenReturn(Optional.of(transaccion));

        Bono005BzTrDTO dto = new Bono005BzTrDTO();
        dto.setPeriodoActual(periodo2);
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setIdUsuario(usuario.getId());
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setPeriodoActual(periodo2);

        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo, periodo2)));

        // Ejecutar directamente run()
        Bono005BzTr hilo = new Bono005BzTr(dto);
        hilo.run();
    }

    @Test
    void cuandoRunEsEjecutadoTransaccionNoPresente() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(pendiente);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setNumero((short) 1);
        periodo.setEstatus(pendiente);
        periodo.setBonoAhorro(bonoAhorro);
        periodo.setClave(UUID.randomUUID());

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);

        Transaccion transaccion = new Transaccion();
        transaccion.setClave(UUID.randomUUID());
        periodo.setTransaccion(transaccion);

        when(trxRepository.findByClave(any()))
            .thenReturn(Optional.empty());

        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        Bono005BzTrDTO dto = new Bono005BzTrDTO();
        dto.setPeriodoActual(periodo);
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setIdUsuario(usuario.getId());
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);

        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        // Ejecutar directamente run()
        Bono005BzTr hilo = new Bono005BzTr(dto);
        hilo.run();
    }

    @Test
    void cuandoRunEsEjecutadoTransaccionNPresenteYActivo() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now().minusWeeks(2));
        periodo.setNumero((short) 0);
        periodo.setEstatus(activo);
        periodo.setBonoAhorro(bonoAhorro);
        periodo.setClave(UUID.randomUUID());

        BonoAhorroPeriodo periodo2 = new BonoAhorroPeriodo();
        periodo2.setFechaProgramada(LocalDate.now());
        periodo2.setNumero((short) TRES);
        periodo2.setEstatus(activo);
        periodo2.setBonoAhorro(bonoAhorro);
        periodo.setClave(UUID.randomUUID());

        Transaccion transaccion = new Transaccion();
        transaccion.setClave(UUID.randomUUID());
        transaccion.setEstatus(activo);
        periodo2.setTransaccion(transaccion);

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(any()))
            .thenReturn(Optional.of(transaccion));

        Bono005BzTrDTO dto = new Bono005BzTrDTO();
        dto.setPeriodoActual(periodo2);
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setIdUsuario(usuario.getId());
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);
        dto.setPeriodoActual(periodo2);

        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo, periodo2)));

        // Ejecutar directamente run()
        Bono005BzTr hilo = new Bono005BzTr(dto);
        hilo.run();
    }

    @Test
    void cuandoPeriodosEstaVacioNoSeEjecutaElFor() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);
        bono.setTasa(TASA);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usuario);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);
        bonoAhorro.setPeriodos(new ArrayList<>());

        BonoAhorroPeriodo periodoActual = new BonoAhorroPeriodo();
        periodoActual.setFechaProgramada(LocalDate.now());
        periodoActual.setNumero((short) 1);
        periodoActual.setEstatus(pendiente);
        periodoActual.setBonoAhorro(bonoAhorro);
        periodoActual.setClave(UUID.randomUUID());

        Transaccion transaccion = new Transaccion();
        transaccion.setClave(UUID.randomUUID());
        transaccion.setEstatus(activo);
        periodoActual.setTransaccion(transaccion);

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(any()))
            .thenReturn(Optional.of(transaccion));

        Bono005BzTrDTO dto = new Bono005BzTrDTO();
        dto.setPeriodoActual(periodoActual);
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setIdUsuario(usuario.getId());
        dto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);
        dto.setPendiente(pendiente);
        dto.setEliminado(eliminado);

        // Ejecutar run()
        Bono005BzTr hilo = new Bono005BzTr(dto);
        hilo.run();

        verify(
            bonoAhorroPeriodoRepo,
            never()
        ).save(argThat(p -> eliminado.equals(p.getEstatus())));
    }

}
