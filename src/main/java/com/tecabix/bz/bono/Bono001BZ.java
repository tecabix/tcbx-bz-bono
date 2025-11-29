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

    /**
     * Repositorio para acceder a la entidad Bono.
     */
    private final BonoRepository bonoRepository;

    /**
     * Constructor de la clase {@code Bono001BZ}.
     *
     * @param repository el repositorio de bonos que se utilizará para acceder a
     *                   los datos de persistencia.
     */
    public Bono001BZ(final BonoRepository repository) {
        this.bonoRepository = repository;
    }

    /**
     * Busca y retorna los bonos en subasta.
     * <p>
     * Este método consulta el repositorio de bonos para obtener aquellos que se
     * encuentran en estado pendiente, utilizando un pageable que obtiene todos
     * los registros ordenados por el identificador de forma ascendente.
     * </p>
     *
     * @param rqsv002 objeto de solicitud que contiene el contenedor de
     *                respuesta {@link RQSV002#getRsb005()}
     * @return ResponseEntity con el objeto {@link RSB005} que encapsula la
     *                        lista de bonos en subasta.
     */
    public ResponseEntity<RSB005> buscarSubasta(final RQSV002 rqsv002) {
        RSB005 rsb005 = rqsv002.getRsb005();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        Page<Bono> page = bonoRepository.findByPendiente(pageable);
        return rsb005.ok(page);
    }
}
