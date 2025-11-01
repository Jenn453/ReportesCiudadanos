package co.edu.uniquindio.proyecto.servicios;

import co.edu.uniquindio.proyecto.dto.moderadores.CategoriaDTO;
import co.edu.uniquindio.proyecto.dto.moderadores.InformeDTO;
import co.edu.uniquindio.proyecto.dto.moderadores.ObtenerCategoriaDTO;
import co.edu.uniquindio.proyecto.dto.reportes.HistorialReporteDTO;

import java.time.LocalDate;
import java.util.List;

public interface ModeradorServicio {

    void crearCategoria(CategoriaDTO categoriaDTO) throws Exception;

    List<ObtenerCategoriaDTO> obtenerCategorias() throws Exception;

    void editarCategoria( String id, CategoriaDTO categoriaDTO) throws Exception;

    void eliminarCategoria(String id) throws Exception;

    String obtenerColorByCategoria(String nombreCategoria) throws Exception;

    List<InformeDTO> generarInforme(String ciudad, String categoria, LocalDate fechaInicio, LocalDate fechaFin)throws Exception;

    List<HistorialReporteDTO> obtenerHistorial(String idReporte) throws Exception ;

}
