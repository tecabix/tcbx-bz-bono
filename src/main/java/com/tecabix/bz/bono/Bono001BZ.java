package com.tecabix.bz.bono;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Bono;
import com.tecabix.db.repository.BonoRepository;
import com.tecabix.res.b.RSB005;
import com.tecabix.sv.rq.RQSV002;

/**
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Bono001BZ {
	
	private BonoRepository bonoRepository;
	
	public Bono001BZ(BonoRepository repository) {
		if(repository == null) {
			
		}
		this.bonoRepository = repository;
	}

	public ResponseEntity<RSB005> buscarSubasta(final RQSV002 rqsv002) {
        RSB005 rsb005 = rqsv002.getRsb005();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        Page<Bono> page = bonoRepository.findByPendiente(pageable);
        return rsb005.ok(page);
    }
}
