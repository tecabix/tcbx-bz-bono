package com.tecabix.bz.bono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono003BzDTO;
import com.tecabix.bz.bono.dto.Bono003BzTrDTO;
import com.tecabix.bz.bono.tr.Bono003BzTr;
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

    /**
     * Repositorio simulado para pruebas de bonos.
     */
    @Mock
    private BonoRepository bonoRepository;

    /**
     * Repositorio simulado para pruebas de cuentas.
     */
    @Mock
    private CuentaRepository cuentaRepository;

    /**
     * Repositorio simulado para pruebas de bonos de ahorro.
     */
    @Mock
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Repositorio simulado para pruebas de periodos de bonos de ahorro.
     */
    @Mock
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Repositorio simulado para pruebas de transacciones.
     */
    @Mock
    private TransaccionRepository transaccionRepository;

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
     * Catálogo simulado que representa el tipo de operación 'compra'.
     */
    @Mock
    private Catalogo compra;

    /**
     * Cuenta simulada de inversión utilizada en pruebas.
     */
    @Mock
    private Cuenta cuentaInversion;

    /**
     * DTO simulado utilizado para inicializar la clase bajo prueba.
     */
    @Mock
    private Bono003BzDTO dto;

    /**
     * Instancia de la clase bajo prueba.
     */
    private Bono003BZ bono003bz;

    /**
     * Saldo de 20.
     */
    private static final int VEINTE = 20_00;

    /**
     * Saldo de 200.
     */
    private static final int DOSCIENTOS = 200_00;

    /**
     * Saldo de 500.
     */
    private static final int QUINIENTOS = 500_00;

    /**
     * Estatus Http 409.
     */
    private static final int CONFLICT = 409;

    /**
     * Estatus Http 404.
     */
    private static final int NOTFOUND = 404;

    /**
     * Estatus Http 400.
     */
    private static final int BADREQUEST = 400;

    /**
     * Cinco segundos en milisegundos.
     */
    private static final int CINCOMIL = 5000;

    /**
     * Configura el entorno de prueba antes de la ejecución de cada test.
     *
     * <p>Este método se ejecuta antes de cada método de prueba anotado
     * con {@code @Test}. Inicializa una instancia de {@code Bono003BZ}
     * con un objeto {@code Bono003BzDTO} que contiene las dependencias
     * necesarias como los repositorios y estados.</p>
     */
    @BeforeEach
    void setUp() {
        Bono003BzDTO dtoBono = new Bono003BzDTO();
        dtoBono.setBonoRepository(bonoRepository);
        dtoBono.setCuentaRepository(cuentaRepository);
        dtoBono.setBonoAhorroRepository(bonoAhorroRepository);
        dtoBono.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        dtoBono.setTransaccionRepository(transaccionRepository);
        dtoBono.setActivo(activo);
        dtoBono.setPendiente(pendiente);
        dtoBono.setEliminado(eliminado);
        dtoBono.setCompra(compra);
        dtoBono.setCuentaInversion(cuentaInversion);
        bono003bz = new Bono003BZ(dtoBono);
    }

    private RQSV004 prepareRequest(
            final UUID clave, final RSB003 rsb003, final Sesion sesion) {
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

        when(cuentaRepository.findByUsuario(anyLong()))
            .thenReturn(Optional.of(new Cuenta()));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(VEINTE);
        cuentaOrigen.setClave(UUID.randomUUID());

        Cuenta ctaInversion = new Cuenta();
        ctaInversion.setId(2L);
        ctaInversion.setSaldo(VEINTE);
        ctaInversion.setClave(UUID.randomUUID());

        when(cuentaRepository.findByUsuario(1L))
            .thenReturn(Optional.of(cuentaOrigen));

        Bono003BzDTO bonoDto = new Bono003BzDTO();
        bonoDto.setBonoRepository(bonoRepository);
        bonoDto.setCuentaRepository(cuentaRepository);
        bonoDto.setBonoAhorroRepository(bonoAhorroRepository);
        bonoDto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        bonoDto.setTransaccionRepository(transaccionRepository);
        bonoDto.setActivo(activo);
        bonoDto.setPendiente(pendiente);
        bonoDto.setEliminado(eliminado);
        bonoDto.setCompra(compra);
        bonoDto.setCuentaInversion(ctaInversion);

        bono003bz = new Bono003BZ(bonoDto);

        RQSV004 rq = prepareRequest(clave, rsb003, sesion);
        when(rsb003.conflict("El saldo del ordenante es insuficiente"))
            .thenReturn(ResponseEntity.status(CONFLICT).build());

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).conflict("El saldo del ordenante es insuficiente");
        assertEquals(CONFLICT, response.getStatusCodeValue());
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
        cuentaOrigen.setSaldo(DOSCIENTOS);
        cuentaOrigen.setClave(UUID.randomUUID());

        Cuenta inversion = new Cuenta();
        inversion.setId(2L);
        inversion.setSaldo(QUINIENTOS);
        inversion.setClave(UUID.randomUUID());

        when(cuentaRepository.findByUsuario(1L))
            .thenReturn(Optional.of(cuentaOrigen));

        Bono003BzDTO bonoDto = new Bono003BzDTO();
        bonoDto.setBonoRepository(bonoRepository);
        bonoDto.setCuentaRepository(cuentaRepository);
        bonoDto.setBonoAhorroRepository(bonoAhorroRepository);
        bonoDto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        bonoDto.setTransaccionRepository(transaccionRepository);
        bonoDto.setActivo(activo);
        bonoDto.setPendiente(pendiente);
        bonoDto.setEliminado(eliminado);
        bonoDto.setCompra(compra);
        bonoDto.setCuentaInversion(inversion);

        bono003bz = new Bono003BZ(bonoDto);

        RQSV004 rq = prepareRequest(clave, rsb003, sesion);

        when(transaccionRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

        when(bonoAhorroPeriodoRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

        when(rsb003.ok(any(Transaccion.class)))
            .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).ok(any(Transaccion.class));
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void cuandoPlazoEs27yNoHayCtaDestino() {

        UUID clave = UUID.randomUUID();
        RSB003 rsb003 = mock(RSB003.class);
        Sesion sesion = new Sesion();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        sesion.setUsuario(usuario);
        Bono bono = new Bono();
        bono.setEstatus(pendiente);
        Catalogo tipoPlazo = new Catalogo();
        tipoPlazo.setNombre("CTA27");
        bono.setTipoPlazo(tipoPlazo);
        bono.setEstatus(pendiente);
        when(bonoRepository.findByClave(clave)).thenReturn(Optional.of(bono));
        when(cuentaRepository.findByUsuario(anyLong()))
            .thenReturn(Optional.of(new Cuenta()));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(VEINTE);
        cuentaOrigen.setClave(UUID.randomUUID());

        when(cuentaRepository.findByUsuario(1L))
            .thenReturn(Optional.of(cuentaOrigen));

        Bono003BzDTO bonoDto = new Bono003BzDTO();
        bonoDto.setBonoRepository(bonoRepository);
        bonoDto.setCuentaRepository(cuentaRepository);
        bonoDto.setBonoAhorroRepository(bonoAhorroRepository);
        bonoDto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        bonoDto.setTransaccionRepository(transaccionRepository);
        bonoDto.setActivo(activo);
        bonoDto.setPendiente(pendiente);
        bonoDto.setEliminado(eliminado);
        bonoDto.setCompra(compra);

        bono003bz = new Bono003BZ(bonoDto);

        RQSV004 rq = prepareRequest(clave, rsb003, sesion);

        when(rsb003.notFound("No se encontró la cuenta destino."))
            .thenReturn(ResponseEntity.status(NOTFOUND).build());

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).notFound("No se encontró la cuenta destino.");
        assertEquals(NOTFOUND, response.getStatusCodeValue());
    }

    @Test
    void cuandoPlazoEs53CtaOrigenDestinoIguales() {

        UUID clave = UUID.randomUUID();
        RSB003 rsb003 = mock(RSB003.class);
        Sesion sesion = new Sesion();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        sesion.setUsuario(usuario);
        Bono bono = new Bono();
        bono.setEstatus(pendiente);
        Catalogo tipoPlazo = new Catalogo();
        tipoPlazo.setNombre("CTA53");
        bono.setTipoPlazo(tipoPlazo);
        bono.setEstatus(pendiente);
        when(bonoRepository.findByClave(clave)).thenReturn(Optional.of(bono));

        when(cuentaRepository.findByUsuario(anyLong()))
            .thenReturn(Optional.of(new Cuenta()));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(2L);
        cuentaOrigen.setSaldo(VEINTE);
        cuentaOrigen.setClave(UUID.randomUUID());

        when(cuentaRepository.findByUsuario(1L))
            .thenReturn(Optional.of(cuentaOrigen));

        Bono003BzDTO bonoDto = new Bono003BzDTO();
        bonoDto.setBonoRepository(bonoRepository);
        bonoDto.setCuentaRepository(cuentaRepository);
        bonoDto.setBonoAhorroRepository(bonoAhorroRepository);
        bonoDto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        bonoDto.setTransaccionRepository(transaccionRepository);
        bonoDto.setActivo(activo);
        bonoDto.setPendiente(pendiente);
        bonoDto.setEliminado(eliminado);
        bonoDto.setCompra(compra);
        bonoDto.setCuentaInversion(cuentaOrigen);

        bono003bz = new Bono003BZ(bonoDto);

        RQSV004 rq = prepareRequest(clave, rsb003, sesion);
        when(rsb003.badRequest("La cuenta de origen y destino son la misma."))
                .thenReturn(ResponseEntity.status(BADREQUEST).build());

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).badRequest("La cuenta de origen y destino son la misma.");
        assertEquals(BADREQUEST, response.getStatusCodeValue());
    }

    @Test
    void cuandoNumeroAporteCero() {

        UUID clave = UUID.randomUUID();
        RSB003 rsb003 = mock(RSB003.class);
        Sesion sesion = new Sesion();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        sesion.setUsuario(usuario);
        Bono bono = new Bono();
        bono.setEstatus(pendiente);
        Catalogo tipoPlazo = new Catalogo();
        tipoPlazo.setNombre("Prueba");
        bono.setTipoPlazo(tipoPlazo);
        bono.setEstatus(pendiente);
        when(bonoRepository.findByClave(clave)).thenReturn(Optional.of(bono));

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(2L);
        cuentaOrigen.setSaldo(VEINTE);
        cuentaOrigen.setClave(UUID.randomUUID());

        Cuenta inversion = new Cuenta();
        inversion.setId(2L);
        inversion.setSaldo(QUINIENTOS);
        inversion.setClave(UUID.randomUUID());

        Bono003BzDTO bonoDto = new Bono003BzDTO();
        bonoDto.setBonoRepository(bonoRepository);
        bonoDto.setCuentaRepository(cuentaRepository);
        bonoDto.setBonoAhorroRepository(bonoAhorroRepository);
        bonoDto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepository);
        bonoDto.setTransaccionRepository(transaccionRepository);
        bonoDto.setActivo(activo);
        bonoDto.setPendiente(pendiente);
        bonoDto.setEliminado(eliminado);
        bonoDto.setCompra(compra);
        bonoDto.setCuentaInversion(inversion);

        bono003bz = new Bono003BZ(bonoDto);

        RQSV004 rq = prepareRequest(clave, rsb003, sesion);

        when(rsb003.notFound("No se encontró el bono."))
            .thenReturn(ResponseEntity.status(NOTFOUND).build());

        ResponseEntity<?> response = bono003bz.comprarCTA(rq);

        verify(rsb003).notFound("No se encontró el bono.");
        assertEquals(NOTFOUND, response.getStatusCodeValue());
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
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        periodo.setBonoAhorro(bonoAhorro);

        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        Transaccion transaccion = new Transaccion();
        UUID claveTransaccion = UUID.randomUUID();
        transaccion.setClave(claveTransaccion);

        transaccion.setEstatus(activo);
        periodo.setTransaccion(transaccion);

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(eq(claveTransaccion)))
            .thenReturn(Optional.of(transaccion));

        Bono003BzTrDTO bonoDto = new Bono003BzTrDTO();
        bonoDto.setTransaccionRepository(trxRepository);
        bonoDto.setBonoAhorro(bonoAhorro);
        bonoDto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        bonoDto.setBonoAhorroRepository(bonoAhorroRepo);
        bonoDto.setActivo(activo);
        bonoDto.setPendiente(pendiente);

        Bono003BzTr hilo = new Bono003BzTr(bonoDto);

        hilo.run();

        verify(bonoAhorroPeriodoRepo).save(periodo);
        verify(bonoAhorroRepo).save(bonoAhorro);
    }

    @Test
    void cuandoRunEsEjecutadoDirectamenteElse() {

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
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        periodo.setBonoAhorro(bonoAhorro);

        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        Transaccion transaccion = new Transaccion();
        UUID claveTransaccion = UUID.randomUUID();
        transaccion.setClave(claveTransaccion);

        transaccion.setEstatus(pendiente);
        periodo.setTransaccion(transaccion);

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(eq(claveTransaccion)))
            .thenReturn(Optional.of(transaccion));

        Bono003BzTrDTO bonoDto = new Bono003BzTrDTO();
        bonoDto.setTransaccionRepository(trxRepository);
        bonoDto.setBonoAhorro(bonoAhorro);
        bonoDto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        bonoDto.setBonoAhorroRepository(bonoAhorroRepo);
        bonoDto.setActivo(activo);
        bonoDto.setPendiente(pendiente);

        Bono003BzTr hilo = new Bono003BzTr(bonoDto);

        hilo.run();
    }

    @Test
    void cuandoRunEsEjecutadoDirectamenteError() {

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
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        periodo.setBonoAhorro(bonoAhorro);

        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        Transaccion transaccion = new Transaccion();
        UUID claveTransaccion = UUID.randomUUID();
        transaccion.setClave(claveTransaccion);

        transaccion.setEstatus(pendiente);
        periodo.setTransaccion(transaccion);

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroPeriodoRepository bonoAhorroPeriodoRepo;
        bonoAhorroPeriodoRepo = mock(BonoAhorroPeriodoRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(any()))
            .thenThrow(new RuntimeException("Error forzado"));

        Bono003BzTrDTO bonoDto = new Bono003BzTrDTO();
        bonoDto.setTransaccionRepository(trxRepository);
        bonoDto.setBonoAhorro(bonoAhorro);
        bonoDto.setBonoAhorroPeriodoRepository(bonoAhorroPeriodoRepo);
        bonoDto.setBonoAhorroRepository(bonoAhorroRepo);
        bonoDto.setActivo(activo);
        bonoDto.setPendiente(pendiente);

        Bono003BzTr hilo = new Bono003BzTr(bonoDto);

        hilo.run();
    }

}
