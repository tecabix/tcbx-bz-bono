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

    /**
     * Repositorio para acceder a la entidad BonoAhorro.
     */
    private BonoAhorroRepository bonoAhorroRepository;

    /**
     * Mensaje bono no encontrado.
     */
    private static final String NO_SE_ENCONTRO_EL_BONO;

    static {
        NO_SE_ENCONTRO_EL_BONO = "No se encontró el bono.";
    }

    /**
     * Estado "activo" obtenido desde el catálogo.
     */
    private Catalogo activo;

    /**
     * Constructor que inicializa el repositorio de bono ahorro y el estado
     * activo desde el DTO.
     *
     * @param dto objeto de transferencia con los datos necesarios.
     */
    public Bono004BZ(final Bono004BzDTO dto) {
        this.bonoAhorroRepository = dto.getBonoAhorroRepository();
        this.activo = dto.getActivo();
    }

    /**
     * Obtiene un Bono de Ahorro (CTA) específico.
     * <p>
     * Busca el bono a partir de la clave proporcionada y verifica que est
     * activo y pertenezca al usuario de la sesión.
     * </p>
     *
     * @param rqsv005 objeto de solicitud que contiene la sesión, la clave del
     *                bono y el contenedor de respuesta
     *                {@link RQSV005#getRsb004()}
     * @return ResponseEntity con el objeto {@link RSB004} que encapsula el bono
     *         encontrado o un error en caso de no hallarlo.
     */
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
