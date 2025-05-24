package com.tecabix.bz.bono;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import com.tecabix.bz.bono.dto.Bono002BzDTO;
import com.tecabix.db.entity.BonoAhorro;
import com.tecabix.db.entity.BonoInversion;
import com.tecabix.db.entity.BonoPago;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.repository.BonoAhorroRepository;
import com.tecabix.db.repository.BonoInversionRepository;
import com.tecabix.db.repository.BonoPagoRepository;
import com.tecabix.res.b.RSB002;
import com.tecabix.sv.rq.RQSV003;

/**
*
* @author Ramirez Urrutia Angel Abinadi
*/
public class Bono002BZ {

	private BonoAhorroRepository bonoAhorroRepository;
	
	private BonoInversionRepository bonoInversionRepository;
	
	private BonoPagoRepository bonoPagoRepository;
	
	private Integer activo;
	
	public Bono002BZ(Bono002BzDTO dto) {
		
		this.bonoAhorroRepository = dto.getBonoAhorroRepository();
		this.bonoInversionRepository = dto.getBonoInversionRepository();
		this.bonoPagoRepository = dto.getBonoPagoRepository();
		this.activo = dto.getActivo();
	}

	public ResponseEntity<RSB002> obtenerBono(final RQSV003 rqsv003) {
        RSB002 rsb002 = rqsv003.getRsb002();
        Sesion sesion = rqsv003.getSesion();
        int estatus = activo;
        int maxValue = Integer.MAX_VALUE;
        long usr = sesion.getUsuario().getId();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(0, maxValue, sort);
        Page<BonoAhorro> ahorros;
        ahorros = bonoAhorroRepository.findByUsuario(usr, estatus, page);
        Page<BonoInversion> inversiones;
        inversiones = bonoInversionRepository.findByUsuario(usr, estatus, page);
        Page<BonoPago> pagares;
        pagares = bonoPagoRepository.findByUsuario(usr, estatus, page);

        return rsb002.ok(ahorros, inversiones, pagares);
    }
}
