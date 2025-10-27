package co.edu.uniquindio.proyecto.servicios;

import co.edu.uniquindio.proyecto.dto.reportes.CrearReporteDTO;
import co.edu.uniquindio.proyecto.dto.reportes.EditarReporteDTO;
import co.edu.uniquindio.proyecto.dto.reportes.EstadoReporteDTO;
import co.edu.uniquindio.proyecto.dto.reportes.ReporteDTO;

import java.util.List;

public interface ReporteServicio {

    void crearReporte(CrearReporteDTO crearReporteDTO) throws Exception ;

    int marcarImportante( String id) throws Exception ;

    List<ReporteDTO> obtenerReportes() throws Exception;

    List<ReporteDTO> obtenerReportesUsuario() throws Exception ;

    List<ReporteDTO> obtenerReportesCerca(double latitud, double longitud) throws Exception ;

    List<ReporteDTO> obtenerTopReportes() throws Exception ;

    void editarReporte(String id, EditarReporteDTO reporteDTO) throws Exception ;

    void eliminarReporte( String id) throws Exception ;

    ReporteDTO obtenerReporte(String id) throws Exception;

    void cambiarEstado(String id, EstadoReporteDTO estadoDTO) throws Exception ;



}
