package com.tecabix.bz.bono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono006BzDTO;
import com.tecabix.bz.bono.dto.Bono006BzTrDTO;
import com.tecabix.bz.bono.tr.Bono006BzTr;
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
import com.tecabix.sv.rq.RQSV007;

@ExtendWith(MockitoExtension.class)
public class Bono006BZTest {

    /**
     * Repositorio simulado para pruebas de bonos de ahorro.
     */
    @Mock
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Repositorio simulado para pruebas de cuentas.
     */
    @Mock
    private CuentaRepository cuentaRepository;

    /**
     * Repositorio simulado para pruebas de transacciones.
     */
    @Mock
    private TransaccionRepository transaccionRepository;

    /**
     * Repositorio simulado para pruebas de periodos de bonos de ahorro.
     */
    @Mock
    private BonoAhorroPeriodoRepository bonoAhorroPeriodoRepository;

    /**
     * Mock del catálogo con estado "activo".
     */
    @Mock
    private Catalogo activo;

    /**
     * Mock del catálogo con estado "venta".
     */
    @Mock
    private Catalogo venta;

    /**
     * Mock del catálogo con estado "pendiente"..
     */
    @Mock
    private Catalogo pendiente;

    /**
     * Mock del catálogo con estado "eliminado".
     */
    @Mock
    private Catalogo eliminado;

    /**
     * Mock del servicio RQSV007.
     * Se utiliza para simular la lógica de negocio.
     */
    @Mock
    private RQSV007 rqsv007;

    /**
     * Mock del servicio RSB003.
     * Simula la interacción con otro componente o servicio del sistema.
     */
    @Mock
    private RSB003 rsb003;

    /**
     * Mock de la sesión del usuario.
     * Permite simular el contexto de sesión durante las pruebas.
     */
    @Mock
    private Sesion sesion;

    /**
     * Mock del usuario.
     * Representa al usuario autenticado en el sistema durante las pruebas.
     */
    @Mock
    private Usuario usuario;

    /**
     * Saldo vale 100000.
     */
    private static final int SALDOCIENMIL = 100000;

    /**
     * Saldo con valor 10.
     */
    private static final int SALDODIEZ = 10;

    /**
     * Representa al número 1000.
     */
    private static final int MIL = 1000;

    /**
     * Representa al número 5000.
     */
    private static final int CINCOMIL = 5000;

    /**
     * Saldo con valor 1000000.
     */
    private static final int SALDOMILLON = 1_000_000;

    @BeforeEach
    void setUp() {

    }

    @Test
    void cuandoNoSeEncuentraElBonoRetornaNotFound() {

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

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.empty());

        when(rsb003.notFound(anyString()))
            .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(rsb003));

        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).notFound("No se encontró el bono.");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cuandoElBonoNoPerteneceALaSesionRetornaNotFound() {

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

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(ben));

        Usuario usuarioOtro = new Usuario();
        usuarioOtro.setClave(UUID.randomUUID());

        when(ben.getUsuario()).thenReturn(usuarioOtro);

        when(rsb003.notFound(anyString()))
            .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(rsb003));

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

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(ben));

        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(eliminado);

        when(rsb003.notFound(anyString()))
            .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(rsb003));

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

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(ben));

        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.empty());

        when(rsb003.notFound(anyString()))
            .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(rsb003));

        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).notFound("No se encontró la cuenta de origen.");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cuandoNoSeEncuentraLaCuentaDestinoRetornaNotFound() {
        Cuenta destino = new Cuenta();
        destino.setSaldo(SALDOCIENMIL);
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

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(ben));
        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.empty());

        when(rsb003.notFound(anyString()))
            .thenAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(rsb003));

        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).notFound("No se encontró la cuenta destino.");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cuandoCuentasSonIgualesRetornaConflict() {
        Cuenta origen = new Cuenta();
        origen.setSaldo(SALDOCIENMIL);
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

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(ben));

        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.of(origen));

        when(rsb003.conflict(anyString()))
            .thenAnswer(inv -> ResponseEntity.status(HttpStatus.CONFLICT)
            .body(rsb003));

        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).conflict("La cuenta de origen y destino son la misma.");
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void cuandoSaldoInsuficienteRetornaConflict() {
        Cuenta origen = new Cuenta();
        origen.setSaldo(SALDODIEZ);
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
        destino.setSaldo(SALDOMILLON);
        Bono tipoBono = mock(Bono.class);
        Catalogo tipoPlazo = mock(Catalogo.class);

        when(tipoPlazo.getNombre()).thenReturn("PLAZO");
        when(tipoBono.getTipoPlazo()).thenReturn(tipoPlazo);

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(ben));

        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);
        when(ben.getAportaciones()).thenReturn(MIL);

        when(cuentaRepository.findByUsuario(usuario.getId()))
            .thenReturn(Optional.of(destino));

        when(rsb003.conflict(anyString()))
            .thenAnswer(inv -> ResponseEntity.status(HttpStatus.CONFLICT)
            .body(rsb003));
        ResponseEntity<RSB003> resp = bono006bz.borrarCTA(rqsv007);

        verify(rsb003).conflict("El saldo del ordenante es insuficiente");
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void cuandoTodoValidoRetornaOk() {

        Cuenta origen = new Cuenta();
        origen.setSaldo(SALDOMILLON);
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
        BonoAhorro bonoAhorro = mock(BonoAhorro.class);

        List<BonoAhorroPeriodo> periodos = bonoAhorro.getPeriodos();
        periodos.add(new BonoAhorroPeriodo());
        LocalDate hoy = LocalDate.now();
        periodos.get(0).setFechaProgramada(hoy.plusDays(2));
        periodos.get(0).setTransaccion(null);
        periodos.get(0).setNumero((short) 0);
        periodos.get(0).setEstatus(activo);

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
        destino.setSaldo(SALDOMILLON);
        Bono tipoBono = mock(Bono.class);
        Catalogo tipoPlazo = mock(Catalogo.class);

        when(tipoPlazo.getNombre()).thenReturn("PLAZO");
        when(tipoBono.getTipoPlazo()).thenReturn(tipoPlazo);

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(ben));

        when(ben.getUsuario()).thenReturn(usuario);
        when(ben.getEstatus()).thenReturn(activo);
        when(ben.getBono()).thenReturn(tipoBono);
        when(ben.getAportaciones()).thenReturn(MIL);
        when(ben.getPeriodos()).thenReturn(periodos);

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

    @Test
    void cuandoRunEsEjecutadoDirectamente() {

        Usuario usr = new Usuario();
        usr.setId(1L);
        Sesion sesionTest = new Sesion();
        sesionTest.setUsuario(usr);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);

        Transaccion transaccion = new Transaccion();
        UUID claveTransaccion = UUID.randomUUID();
        transaccion.setClave(claveTransaccion);
        transaccion.setEstatus(pendiente);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usr);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);
        bonoAhorro.setTransaccion(transaccion);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setNumero((short) 0);
        periodo.setEstatus(activo);
        periodo.setTransaccion(transaccion);
        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        // Repositorios mockeados
        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(eq(claveTransaccion)))
            .thenReturn(Optional.of(transaccion));

        Bono006BzTrDTO dto = new Bono006BzTrDTO();
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);

        // Ejecutar directamente run()
        Bono006BzTr hilo = new Bono006BzTr(dto);
        hilo.run();
    }

    @Test
    void cuandoRunEsEjecutadoDirectamenteElse() {

        Usuario usr = new Usuario();
        usr.setId(1L);
        Sesion sesionTest = new Sesion();
        sesionTest.setUsuario(usr);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);

        Transaccion transaccion = new Transaccion();
        UUID claveTransaccion = UUID.randomUUID();
        transaccion.setClave(claveTransaccion);
        transaccion.setEstatus(activo);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usr);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);
        bonoAhorro.setTransaccion(transaccion);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setNumero((short) 0);
        periodo.setEstatus(activo);
        periodo.setTransaccion(transaccion);
        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(eq(claveTransaccion)))
            .thenReturn(Optional.of(transaccion));

        Bono006BzTrDTO dto = new Bono006BzTrDTO();
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);

        // Ejecutar directamente run()
        Bono006BzTr hilo = new Bono006BzTr(dto);
        hilo.run();
    }

    @Test
    void cuandoRunEsEjecutadoDirectamenteError() {

        Usuario usr = new Usuario();
        usr.setId(1L);
        Sesion sesionTest = new Sesion();
        sesionTest.setUsuario(usr);

        Catalogo tipoPlazo = mock(Catalogo.class);
        Bono bono = new Bono();
        bono.setTipoPlazo(tipoPlazo);

        Transaccion transaccion = new Transaccion();
        UUID claveTransaccion = UUID.randomUUID();
        transaccion.setClave(claveTransaccion);
        transaccion.setEstatus(activo);

        BonoAhorro bonoAhorro = new BonoAhorro();
        bonoAhorro.setUsuario(usr);
        bonoAhorro.setEstatus(activo);
        bonoAhorro.setAportacion((short) CINCOMIL);
        bonoAhorro.setBono(bono);
        bonoAhorro.setTransaccion(transaccion);

        BonoAhorroPeriodo periodo = new BonoAhorroPeriodo();
        periodo.setFechaProgramada(LocalDate.now());
        periodo.setNumero((short) 0);
        periodo.setEstatus(pendiente);
        periodo.setTransaccion(transaccion);
        bonoAhorro.setPeriodos(new ArrayList<>(List.of(periodo)));

        TransaccionRepository trxRepository;
        trxRepository = mock(TransaccionRepository.class);
        BonoAhorroRepository bonoAhorroRepo;
        bonoAhorroRepo = mock(BonoAhorroRepository.class);

        when(trxRepository.findByClave(eq(claveTransaccion)))
            .thenThrow(new RuntimeException("Error forzado"));

        Bono006BzTrDTO dto = new Bono006BzTrDTO();
        dto.setTransaccionRepository(trxRepository);
        dto.setBonoAhorro(bonoAhorro);
        dto.setBonoAhorroRepository(bonoAhorroRepo);
        dto.setActivo(activo);

        // Ejecutar directamente run()
        Bono006BzTr hilo = new Bono006BzTr(dto);
        hilo.run();

    }
}
