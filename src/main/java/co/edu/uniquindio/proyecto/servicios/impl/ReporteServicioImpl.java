package co.edu.uniquindio.proyecto.servicios.impl;


import co.edu.uniquindio.proyecto.dto.reportes.CrearReporteDTO;
import co.edu.uniquindio.proyecto.dto.notificaciones.NotificacionDTO;
import co.edu.uniquindio.proyecto.excepciones.*;
import co.edu.uniquindio.proyecto.dto.reportes.*;
import co.edu.uniquindio.proyecto.mapper.ReporteMapper;
import co.edu.uniquindio.proyecto.modelo.documentos.Categoria;
import co.edu.uniquindio.proyecto.modelo.documentos.Reporte;
import co.edu.uniquindio.proyecto.modelo.documentos.Usuario;

import co.edu.uniquindio.proyecto.repositorios.CategoriaRepo;
import co.edu.uniquindio.proyecto.repositorios.ReporteRepo;
import co.edu.uniquindio.proyecto.repositorios.UsuarioRepo;
import co.edu.uniquindio.proyecto.servicios.EmailServicio;
import co.edu.uniquindio.proyecto.servicios.ReporteServicio;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

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
}
