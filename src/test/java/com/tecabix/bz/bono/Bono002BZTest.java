package com.tecabix.bz.bono;

import com.tecabix.bz.bono.dto.Bono002BzDTO;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.BonoInversion;
import com.tecabix.db.entity.BonoPago;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.BonoInversionRepository;
import com.tecabix.db.repository.BonoPagoRepository;
import com.tecabix.res.b.RSB002;
import com.tecabix.sv.rq.RQSV003;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
@ExtendWith(MockitoExtension.class)
class Bono002BZTest {

    @Mock
    private BonoAhorroRepository ahorroRepository;

    @Mock
    private BonoInversionRepository inversionRepository;

    @Mock
    private BonoPagoRepository pagoRepository;

    @Mock
    private RSB002 rsb002;

    @Mock
    private RQSV003 rqsv003;

    @Mock
    private Sesion sesion;

    @Mock
    private Usuario usuario;

    private Bono002BZ bono002bz;

    @BeforeEach
    void setUp() {
        Bono002BzDTO dto = new Bono002BzDTO();
        dto.setBonoAhorroRepository(ahorroRepository);
        dto.setBonoInversionRepository(inversionRepository);
        dto.setBonoPagoRepository(pagoRepository);
        dto.setActivo(1);

        bono002bz = new Bono002BZ(dto);
    }

    @Test
    void obtenerBonoRetornaRespuestaExitosa() {
        Page<BonoAhorro> ahorros = new PageImpl<>(Collections.emptyList());
        Page<BonoInversion> inversiones = new PageImpl<>(Collections.emptyList());
        Page<BonoPago> pagares = new PageImpl<>(Collections.emptyList());

        when(usuario.getId()).thenReturn(1L);
        when(sesion.getUsuario()).thenReturn(usuario);
        when(rqsv003.getSesion()).thenReturn(sesion);
        when(rqsv003.getRsb002()).thenReturn(rsb002);

        when(ahorroRepository.findByUsuario(anyLong(), eq(1), any(Pageable.class))).thenReturn(ahorros);
        when(inversionRepository.findByUsuario(anyLong(), eq(1), any(Pageable.class))).thenReturn(inversiones);
        when(pagoRepository.findByUsuario(anyLong(), eq(1), any(Pageable.class))).thenReturn(pagares);
        when(rsb002.ok(ahorros, inversiones, pagares)).thenReturn(ResponseEntity.ok(rsb002));

        ResponseEntity<RSB002> response = bono002bz.obtenerBono(rqsv003);

        assertEquals(ResponseEntity.ok(rsb002), response);
        verify(ahorroRepository).findByUsuario(anyLong(), eq(1), any(Pageable.class));
        verify(inversionRepository).findByUsuario(anyLong(), eq(1), any(Pageable.class));
        verify(pagoRepository).findByUsuario(anyLong(), eq(1), any(Pageable.class));
        verify(rsb002).ok(ahorros, inversiones, pagares);
    }

}
