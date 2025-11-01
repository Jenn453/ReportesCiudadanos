package co.edu.uniquindio.proyecto.servicios;

import co.edu.uniquindio.proyecto.dto.comentarios.ComentarioDTO;
import co.edu.uniquindio.proyecto.dto.comentarios.CrearComentarioDTO;

import java.util.List;

public interface ComentarioServicio {

    void agregarComentario(String idReporte, CrearComentarioDTO crearComentarioDTO) throws Exception ;
    List<ComentarioDTO> obtenerComentarios(String idReporte) throws Exception ;
    void eliminarComentario(String id) throws Exception ;
}
