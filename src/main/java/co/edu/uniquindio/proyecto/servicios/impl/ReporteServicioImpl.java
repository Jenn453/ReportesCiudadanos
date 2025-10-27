package co.edu.uniquindio.proyecto.servicios.impl;


import co.edu.uniquindio.proyecto.dto.notificaciones.EmailDTO;
import co.edu.uniquindio.proyecto.dto.reportes.CrearReporteDTO;
import co.edu.uniquindio.proyecto.dto.notificaciones.NotificacionDTO;
import co.edu.uniquindio.proyecto.excepciones.*;
import co.edu.uniquindio.proyecto.dto.reportes.*;
import co.edu.uniquindio.proyecto.mapper.ReporteMapper;
import co.edu.uniquindio.proyecto.modelo.documentos.Categoria;
import co.edu.uniquindio.proyecto.modelo.documentos.Reporte;
import co.edu.uniquindio.proyecto.modelo.documentos.Usuario;

import co.edu.uniquindio.proyecto.modelo.enums.EstadoReporte;
import co.edu.uniquindio.proyecto.repositorios.CategoriaRepo;
import co.edu.uniquindio.proyecto.repositorios.ReporteRepo;
import co.edu.uniquindio.proyecto.repositorios.UsuarioRepo;
import co.edu.uniquindio.proyecto.servicios.EmailServicio;
import co.edu.uniquindio.proyecto.servicios.ReporteServicio;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReporteServicioImpl implements ReporteServicio {


    private final ReporteMapper reporteMapper;
    private final UsuarioRepo usuarioRepo;
    private final UsuarioServicioImpl usuarioServicio;
    private final CategoriaRepo categoriaRepo;
    private final ReporteRepo reporteRepo;

    private final EmailServicio emailServicio;

    @Override
    public void crearReporte(CrearReporteDTO crearReporteDTO) throws Exception {

        String id = usuarioServicio.obtenerIdSesion();

        // Mapear DTO a documento y guardar en la base de datos
        Reporte reporte = reporteMapper.toDocument(crearReporteDTO);
        reporte.setUsuarioId(new ObjectId(id));
        // Buscar al usuario por su ID
        Usuario usuario = usuarioRepo.findById(new ObjectId(id)).orElseThrow(() -> new Exception("Usuario no encontrado"));
        reporte.setUsuarioId(usuario.getId());

        reporte.setCategoria(crearReporteDTO.categoria());

        // Validar si el categoriaId es válido antes de intentar convertirlo
        //if (crearReporteDTO.categoria() == null || !ObjectId.isValid(crearReporteDTO.categoria())) {
          //  throw new CategoriaNoEncontradaException("Categoría no encontrada");
        //}

        // Validar que la categoría existe antes de asignarla al reporte
        //Categoria categoria = categoriaRepo.findByNombre(crearReporteDTO.categoria())
          //      .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada"));

        // Asignar la categoría al reporte
        //reporte.setCategoriaId(categoria.getId());

        reporteRepo.save(reporte);
        NotificacionDTO notificacionDTO = new NotificacionDTO(
                "Nuevo Reporte",
                "Se acaba de crear un nuevo reporte: " + reporte.getTitulo(),
                "reports"
        );

    }
    @Override
    public int marcarImportante(String id) throws Exception {

        Optional<Reporte> optionalReporte = reporteRepo.findById(id);

        if (optionalReporte.isEmpty()) {
            throw new ReporteNoEncontradoException("No existe el reporte con id: " + id);
        }

        Reporte reporte = optionalReporte.get();
        String usuarioIdString = usuarioServicio.obtenerIdSesion();
        ObjectId usuarioId = new ObjectId(usuarioIdString);
        List<ObjectId> listaUsuarios = reporte.getContadorImportante();

        if (listaUsuarios == null) {
            listaUsuarios = new ArrayList<>();
            listaUsuarios.add(usuarioId);
            reporte.setContadorImportante(listaUsuarios);
            reporteRepo.save(reporte);
            return 1;
        }

        if (!listaUsuarios.contains(usuarioId)) {

            listaUsuarios.add(usuarioId);
            reporte.setContadorImportante(listaUsuarios);
            reporteRepo.save(reporte);
        }

        return listaUsuarios.size();
    }

    @Override
    public List<ReporteDTO> obtenerReportes() {
        return reporteRepo.obtenerReportes();
    }

    public String obtenerNombre(ObjectId idCategoria){
        return categoriaRepo.findById(idCategoria).orElseThrow().getNombre();
    }

    @Override
    public List<ReporteDTO> obtenerReportesUsuario() throws Exception {

        String usuarioId = usuarioServicio.obtenerIdSesion();
        return reporteRepo.obtenerReportesUsuario(new ObjectId(usuarioId));

    }

    @Override
    public List<ReporteDTO> obtenerReportesCerca(double latitud, double longitud) {
        return reporteRepo.obtenerReportesCerca(latitud, longitud);
    }

    @Override
    public List<ReporteDTO> obtenerTopReportes() throws Exception {
        return reporteRepo.obtenerTopReportes();
    }

    @Override
    public void editarReporte(String id, EditarReporteDTO editarReporteDTO) throws Exception {
        if (!ObjectId.isValid(id)) {
            throw new ReporteNoEncontradoException("No se encontró el reporte con el id " + id);
        }

        Optional<Reporte> reporteOptional = reporteRepo.findById(id);
        if (reporteOptional.isEmpty()) {
            throw new ReporteNoEncontradoException("No se encontró el reporte con el id " + id);
        }

        Reporte reporte = reporteOptional.get();

        // Obtener ID del usuario autenticado
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        ObjectId usuarioAutenticadoId = new ObjectId(usernameAutenticado);

        // Verificar que el reporte le pertenezca
        if (!reporte.getUsuarioId().equals(usuarioAutenticadoId)) {
            throw new AccesoNoPermitidoException("No tienes permiso para editar este reporte.");
        }

        // Mapear y guardar
        reporteMapper.toDocument(editarReporteDTO, reporte);
        reporteRepo.save(reporte);
    }

    @Override
    public void eliminarReporte(String id) throws Exception {

        // Validamos el id del reporte
        if (!ObjectId.isValid(id)) {
            throw new ReporteNoEncontradoException("No se encontró el reporte con el id " + id);
        }

        Optional<Reporte> reporteOptional = reporteRepo.findById(id);

        if (reporteOptional.isEmpty()) {
            throw new ReporteNoEncontradoException("No se encontró el reporte con el id " + id);
        }

        // Obtenemos el reporte que se quiere eliminar
        Reporte reporte = reporteOptional.get();

        // Obtenemos el usuario autenticado
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        ObjectId usuarioAutenticadoId = new ObjectId(usernameAutenticado);

        // Verificamos si el reporte pertenece al usuario autenticado
        if (!reporte.getUsuarioId().equals(usuarioAutenticadoId)) {
            throw new AccesoNoPermitidoException("No tienes permiso para eliminar este reporte.");
        }

        // Eliminamos el reporte
        reporteRepo.deleteById(id);
    }

    @Override
    public ReporteDTO obtenerReporte(String id) throws Exception {

        // Validamos que el id sea válido
        if (!ObjectId.isValid(id)) {
            throw new ReporteNoEncontradoException("No se encontró el reporte con el id " + id);
        }

        return reporteRepo.obtenerReporteId(new ObjectId(id));
    }


    @Override
    public void cambiarEstado(String id, EstadoReporteDTO estadoDTO) throws Exception {
        Optional<Reporte> optionalReporte = reporteRepo.findById(id);

        if (optionalReporte.isEmpty()) {
            throw new ReporteNoEncontradoException("El reporte no existe");
        }

        Reporte reporte = optionalReporte.get();

        // Obtener ID y ROL del usuario autenticado
        String idUsuario = usuarioServicio.obtenerIdSesion();
        String rolUsuario = usuarioServicio.obtenerRolSesion();

        boolean esCreador = reporte.getUsuarioId().toHexString().equals(idUsuario);
        boolean esModerador = rolUsuario.equals("MODERADOR");

        if (!esCreador && !esModerador) {
            throw new AccesoNoPermitidoException("No tienes permiso para cambiar el estado de este reporte");
        }

        EstadoReporte nuevoEstado = EstadoReporte.valueOf(estadoDTO.nuevoEstado().toUpperCase());

        // Validación si el nuevo estado es RECHAZADO
        if (nuevoEstado == EstadoReporte.RECHAZADO) {
            if (!esModerador) {
                throw new AccesoNoPermitidoException("Solo un moderador puede rechazar un reporte");
            }
            if (estadoDTO.motivo() == null || estadoDTO.motivo().isBlank()) {
                throw new Exception("Debes proporcionar un motivo al rechazar el reporte");
            }

            // Establecer fecha límite de edición (5 días después del rechazo)
            LocalDateTime fechaLimite = LocalDateTime.now().plusMinutes(1);
            reporte.setFechaLimiteEdicion(fechaLimite);



        } else {
            // Limpiar fecha límite si no fue rechazado
            reporte.setFechaLimiteEdicion(null);
        }

        // Actualizar estado actual del reporte
        reporte.setEstadoActual(nuevoEstado);
        reporteRepo.save(reporte);
//
//        // Crear historial del cambio
//        HistorialReporte.HistorialReporteBuilder historialBuilder = HistorialReporte.builder()
//                .estado(nuevoEstado)
//                .motivo(estadoDTO.motivo())
//                .fecha(LocalDateTime.now())
//                .reporteId(reporte.getId());
//
//// Incluir fecha límite si el estado es RECHAZADO
//        if (nuevoEstado == EstadoReporte.RECHAZADO) {
//            historialBuilder.fechaLimiteEdicion(reporte.getFechaLimiteEdicion());
//            // Obtener email del creador del reporte
//            String emailDestinatario = usuarioRepo.findById(reporte.getUsuarioId())
//                    .map(Usuario::getEmail)
//                    .orElseThrow(() -> new EmailNoEncontradoException("No se pudo obtener el email del creador del reporte"));
//
//            String cuerpoCorreo = """
//        ¡Hola!
//
//        Tu reporte fue rechazado
//
//        Título del reporte: %s
//        Motivo: %s
//
//        Por favor revisa la plataforma para más detalles.
//
//        Saludos,
//        El equipo de Alertas Ciudadanas.
//        """.formatted(reporte.getTitulo(), estadoDTO.motivo());
//            EmailDTO emailDTO = new EmailDTO("Reporte rechazado", cuerpoCorreo, emailDestinatario);
//            emailServicio.enviarCorreo(emailDTO);
//        }
//
//// Construir historial
//        HistorialReporte historial = historialBuilder.build();
//
//// Añadir historial al reporte
//        if (reporte.getHistorialReporte() == null) {
//            reporte.setHistorialReporte(new ArrayList<>());
//        }
//
//        reporte.getHistorialReporte().add(historial);
//
//// Guardar el reporte con el historial actualizado
//        reporteRepo.save(reporte);

    }
}
