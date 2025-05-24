package com.tecabix.bz.bono;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono004BzDTO;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.res.b.RSB004;
import com.tecabix.sv.rq.RQSV005;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
@ExtendWith(MockitoExtension.class)
public class Bono004BZTest {

    @Mock 
    private BonoAhorroRepository bonoAhorroRepository;
    
    @Mock
    private Catalogo activo;
    
    @Mock
    private RQSV005 rqsv005;
    
    @Mock
    private RSB004 rsb004;
    
    @Mock
    private Sesion sesion;
    
    @Mock
    private BonoAhorro bonoAhorro;

    private Bono004BZ bono004bz;
    private UUID clave;

    @BeforeEach
    void setUp() {
        Bono004BzDTO dto = new Bono004BzDTO();
        dto.setActivo(activo);
        dto.setBonoAhorroRepository(bonoAhorroRepository);
        bono004bz = new Bono004BZ(dto);

        when(rqsv005.getRsb004()).thenReturn(rsb004);
        when(rqsv005.getSesion()).thenReturn(sesion);
        clave = UUID.randomUUID();
        when(rqsv005.getClave()).thenReturn(clave);
    }

    @Test
    void cuandoBonoNoExisteDebeRetornarNotFound() {
        // stub específico de este test
        ResponseEntity<RSB004> notFoundResponse =
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(rsb004.notFound("No se encontró el bono."))
            .thenReturn(notFoundResponse);

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.empty());

        ResponseEntity<RSB004> response = bono004bz.obtenerCTA(rqsv005);

        assertSame(notFoundResponse, response);
        verify(rsb004).notFound("No se encontró el bono.");
    }

    @Test
    void cuandoEstatusNoEsActivoDebeRetornarNotFound() {
        ResponseEntity<RSB004> notFoundResponse =
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(rsb004.notFound("No se encontró el bono."))
            .thenReturn(notFoundResponse);

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));
        when(bonoAhorro.getEstatus()).thenReturn(mock(Catalogo.class));

        ResponseEntity<RSB004> response = bono004bz.obtenerCTA(rqsv005);

        assertSame(notFoundResponse, response);
        verify(rsb004).notFound("No se encontró el bono.");
    }

    @Test
    void cuandoUsuarioNoCoincideDebeRetornarNotFound() {
        ResponseEntity<RSB004> notFoundResponse =
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(rsb004.notFound("No se encontró el bono."))
            .thenReturn(notFoundResponse);

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));
        when(bonoAhorro.getEstatus()).thenReturn(activo);
        when(bonoAhorro.getUsuario()).thenReturn(mock(Usuario.class));
        when(sesion.getUsuario()).thenReturn(mock(Usuario.class));

        ResponseEntity<RSB004> response = bono004bz.obtenerCTA(rqsv005);

        assertSame(notFoundResponse, response);
        verify(rsb004).notFound("No se encontró el bono.");
    }

    @Test
    void cuandoDatosValidosDebeRetornarOk() {
        ResponseEntity<RSB004> okResponse = ResponseEntity.ok().build();
        when(rsb004.ok(any(BonoAhorro.class))).thenReturn(okResponse);

        when(bonoAhorroRepository.findByClave(clave))
            .thenReturn(Optional.of(bonoAhorro));
        when(bonoAhorro.getEstatus()).thenReturn(activo);
        Usuario usuario = mock(Usuario.class);
        when(bonoAhorro.getUsuario()).thenReturn(usuario);
        when(sesion.getUsuario()).thenReturn(usuario);

        ResponseEntity<RSB004> response = bono004bz.obtenerCTA(rqsv005);

        assertSame(okResponse, response);
        verify(rsb004).ok(bonoAhorro);
    }
}
