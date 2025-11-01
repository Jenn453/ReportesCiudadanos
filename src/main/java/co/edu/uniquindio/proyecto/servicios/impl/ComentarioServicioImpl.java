package co.edu.uniquindio.proyecto.servicios.impl;

import co.edu.uniquindio.proyecto.dto.comentarios.ComentarioDTO;
import co.edu.uniquindio.proyecto.dto.comentarios.CrearComentarioDTO;
import co.edu.uniquindio.proyecto.dto.notificaciones.EmailDTO;
import co.edu.uniquindio.proyecto.excepciones.*;
import co.edu.uniquindio.proyecto.mapper.ComentarioMapper;
import co.edu.uniquindio.proyecto.modelo.documentos.Comentario;
import co.edu.uniquindio.proyecto.modelo.documentos.Reporte;
import co.edu.uniquindio.proyecto.modelo.documentos.Usuario;
import co.edu.uniquindio.proyecto.repositorios.ComentarioRepo;
import co.edu.uniquindio.proyecto.repositorios.ReporteRepo;
import co.edu.uniquindio.proyecto.repositorios.UsuarioRepo;
import co.edu.uniquindio.proyecto.servicios.ComentarioServicio;
import co.edu.uniquindio.proyecto.servicios.EmailServicio;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ComentarioServicioImpl implements ComentarioServicio {

    private final ComentarioMapper comentarioMapper;
    private final ComentarioRepo comentarioRepo;
    private final ReporteRepo reporteRepo;
    private final UsuarioServicioImpl usuarioServicio;
    private final UsuarioRepo usuarioRepo;
    private final EmailServicio emailServicio;


    @Override
    public void agregarComentario(String idReporte, CrearComentarioDTO crearComentarioDTO) throws Exception {

        if (!ObjectId.isValid(idReporte)) {
            throw new ReporteNoEncontradoException("El reporte con ID " + idReporte + " no existe.");
        }

        // Obtener el reporte y validar existencia
        Reporte reporte = reporteRepo.findById(idReporte)
                .orElseThrow(() -> new ReporteNoEncontradoException("El reporte con ID " + idReporte + " no existe."));

        String idUsuario = usuarioServicio.obtenerIdSesion();
        String nombreUsuario = usuarioServicio.obtenerNombreUsuario();

        Comentario comentario = comentarioMapper.toDocument(crearComentarioDTO);
        comentario.setId(new ObjectId());
        comentario.setReporteId(reporte.getId());
        comentario.setClienteId(new ObjectId(idUsuario));
        comentario.setNombreUsuario(nombreUsuario);

        comentarioRepo.save(comentario);

        // Obtener email del creador del reporte
        String emailDestinatario = usuarioRepo.findById(reporte.getUsuarioId())
                .map(Usuario::getEmail)
                .orElseThrow(() -> new EmailNoEncontradoException("No se pudo obtener el email del creador del reporte"));

        String cuerpoCorreo = """
        ¡Hola!

        Has recibido un nuevo comentario en tu reporte.

        Título del reporte: %s
        Usuario que comentó: %s
        Comentario: %s

        Por favor revisa la plataforma para más detalles.

        Saludos,
        El equipo de Alertas Ciudadanas.
        """.formatted(reporte.getTitulo(), nombreUsuario, crearComentarioDTO.mensaje());
        EmailDTO emailDTO = new EmailDTO("Nuevo comentario en tu reporte", cuerpoCorreo, emailDestinatario);
        emailServicio.enviarCorreo(emailDTO);
    }


    @Override
    public List<ComentarioDTO> obtenerComentarios(String idReporte) throws Exception {

        // Verificar si el reporte existe en la base de datos
        if (!reporteRepo.existsById(idReporte)) {
            throw new ReporteNoEncontradoException("El reporte con ID " + idReporte + " no existe.");
        }

        // Buscar todos los comentarios que tengan el ID del reporte
        List<Comentario> comentarios = comentarioRepo.findByReporteId(new ObjectId(idReporte));

        // Convertir la lista de Comentario a ComentarioDTO usando el mapper
        return comentarios.stream()
                .map(comentarioMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public void eliminarComentario(String id) throws Exception {
        //Validamos el id
        if (!ObjectId.isValid(id)) {
            throw new CategoriaNoEncontradaException("No se encontró el comentario con el id "+id);
        }


        //Buscamos el usuario que se quiere obtener
        ObjectId objectId = new ObjectId(id);

        Optional<Comentario> comentarioOptional = comentarioRepo.findById(objectId);


        //Si no se encontró el usuario, lanzamos una excepción
        if(comentarioOptional.isEmpty()){
            throw new ComentarioNoEncontradoException("No se encontró el comentario con el id "+id);
        }


        //Obtenemos el usuario que se quiere eliminar y le asignamos el estado eliminado
        Comentario comentario = comentarioOptional.get();
        // Obtenemos el usuario autenticado
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        ObjectId usuarioAutenticadoId = new ObjectId(usernameAutenticado);

        // Verificamos si el reporte pertenece al usuario autenticado
        if (!comentario.getClienteId().equals(usuarioAutenticadoId)) {
            throw new AccesoNoPermitidoException("No tienes permiso para eliminar este comentario.");
        }

        comentarioRepo.delete(comentario);
    }
}
