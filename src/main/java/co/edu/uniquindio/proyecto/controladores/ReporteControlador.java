package co.edu.uniquindio.proyecto.controladores;



import co.edu.uniquindio.proyecto.dto.MensajeDTO;
import co.edu.uniquindio.proyecto.dto.comentarios.ComentarioDTO;
import co.edu.uniquindio.proyecto.dto.reportes.*;
import co.edu.uniquindio.proyecto.modelo.documentos.Reporte;
import co.edu.uniquindio.proyecto.servicios.ReporteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/reportes")
public class ReporteControlador{

    private final ReporteServicio reporteServicio; // Inyectar servicio

    @PostMapping
    @Operation(summary = "Crear Reporte")
    public ResponseEntity<MensajeDTO<String>> crearReporte(@Valid @RequestBody CrearReporteDTO crearReporteDTO) throws Exception {
        reporteServicio.crearReporte(crearReporteDTO);
        return ResponseEntity.status(201).body(new MensajeDTO<>(false, "Reporte creado exitosamente"));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los Reportes")
    public ResponseEntity<MensajeDTO<List<ReporteDTO>>> obtenerReportes() throws Exception{
        List<ReporteDTO> reportes = reporteServicio.obtenerReportes();
        return ResponseEntity.ok(new MensajeDTO<>(false, reportes));
    }

    @GetMapping("/usuario")
    @Operation(summary = "Obtener todos los reportes del usuario")
    public ResponseEntity<MensajeDTO<List<ReporteDTO>>> obtenerReportesUsuario() throws Exception {
        List<ReporteDTO> reportes = reporteServicio.obtenerReportesUsuario();
        return ResponseEntity.ok(new MensajeDTO<>(false, reportes));
    }

    @GetMapping("/ubicacion")
    @Operation(summary = "Obtener reportes cerca a ubicación")
    public ResponseEntity<MensajeDTO<List<ReporteDTO>>> obtenerReportesCerca(
            @RequestParam double latitud,
            @RequestParam double longitud
    ) throws Exception {
        List<ReporteDTO> reportes = reporteServicio.obtenerReportesCerca(latitud, longitud);
        return ResponseEntity.ok(new MensajeDTO<>(false, reportes));
    }

    @GetMapping("/topImportantes")
    @Operation(summary = "Obtener top 10 reportes")
    public ResponseEntity<MensajeDTO<List<ReporteDTO>>> obtenerTopReportes() throws Exception {
        List<ReporteDTO> reportes = reporteServicio.obtenerTopReportes();
        return ResponseEntity.ok(new MensajeDTO<>(false, reportes));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar reportes")
    public ResponseEntity<MensajeDTO<String>> editarReporte(@PathVariable String id, @Valid @RequestBody EditarReporteDTO reporteDTO) throws Exception {
        reporteServicio.editarReporte(id, reporteDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Reporte actualizado correctamente"));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reportes")
    public ResponseEntity<MensajeDTO<String>> eliminarReporte(@PathVariable String id) throws Exception {
        reporteServicio.eliminarReporte(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Reporte eliminado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reporte dado el Id")
    public ResponseEntity<MensajeDTO<ReporteDTO>> obtenerReporte(@PathVariable String id) throws Exception {
        ReporteDTO reporte = reporteServicio.obtenerReporte(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, reporte));
    }


    @PutMapping("/{id}/importante")
    @Operation(summary = "Marcar reporte como importante")
    public ResponseEntity<MensajeDTO<Integer>> marcarImportante(@PathVariable String id) throws Exception {
        int contador = reporteServicio.marcarImportante(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, contador));
    }

    @PostMapping("/{id}/estado")
    @Operation(summary = "Editar estado de reporte")
    public ResponseEntity<MensajeDTO<String>> cambiarEstado(
            @PathVariable String id,
            @Valid @RequestBody EstadoReporteDTO estadoDTO) throws Exception {
        reporteServicio.cambiarEstado(id, estadoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Estado del reporte actualizado"));
    }

}

