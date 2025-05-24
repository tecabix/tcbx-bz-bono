package com.tecabix.bz.bono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono004BzDTO;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.res.b.RSB004;
import com.tecabix.sv.rq.RQSV005;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
public class Bono004BZ {

	private BonoAhorroRepository bonoAhorroRepository;
	
	private String NO_SE_ENCONTRO_EL_BONO = "No se encontró el bono.";
	
	private Catalogo activo;
	
    public Bono004BZ(Bono004BzDTO dto) {
		this.bonoAhorroRepository = dto.getBonoAhorroRepository();
		this.activo = dto.getActivo();
	}

	public ResponseEntity<RSB004> obtenerCTA(final RQSV005 rqsv005) {
        RSB004 rsb004 = rqsv005.getRsb004();
        Sesion sesion = rqsv005.getSesion();
        UUID clave = rqsv005.getClave();
        BonoAhorro bonoAhorro;
        bonoAhorro = bonoAhorroRepository.findByClave(clave).orElse(null);
        if (bonoAhorro == null) {
            return rsb004.notFound(NO_SE_ENCONTRO_EL_BONO);
        }
        if (!bonoAhorro.getEstatus().equals(activo)) {
            return rsb004.notFound(NO_SE_ENCONTRO_EL_BONO);
        }
        if (!bonoAhorro.getUsuario().equals(sesion.getUsuario())) {
            return rsb004.notFound(NO_SE_ENCONTRO_EL_BONO);
        }
        return rsb004.ok(bonoAhorro);
    }
}
