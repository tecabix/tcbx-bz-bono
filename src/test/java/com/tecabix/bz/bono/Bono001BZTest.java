package com.tecabix.bz.bono;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Bono;
import com.tecabix.db.repository.BonoRepository;
import com.tecabix.res.b.RSB005;
import com.tecabix.sv.rq.RQSV002;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
@ExtendWith(MockitoExtension.class)
class Bono001BZTest {

    @Mock
    private BonoRepository bonoRepository;

    @Mock
    private RSB005 rsb005;

    @Mock
    private RQSV002 rqsv002;

    @Mock
    private Page<Bono> bonoPage;

    private Bono001BZ bono001bz;

    @BeforeEach
    void setUp() {
        bono001bz = new Bono001BZ(bonoRepository);
    }

    @Test
    void buscarSubasta_retornaRespuestaExitosa() {
        List<Bono> bonos = Collections.singletonList(new Bono());
        bonoPage = new PageImpl<>(bonos);

        when(bonoRepository.findByPendiente(any(Pageable.class))).thenReturn(bonoPage);
        when(rqsv002.getRsb005()).thenReturn(rsb005);
        when(rsb005.ok(bonoPage)).thenReturn(ResponseEntity.ok(rsb005));

        ResponseEntity<RSB005> response = bono001bz.buscarSubasta(rqsv002);

        assertEquals(ResponseEntity.ok(rsb005), response);
        verify(bonoRepository, times(1)).findByPendiente(any(Pageable.class));
        verify(rsb005, times(1)).ok(bonoPage);
    }
}
