package co.edu.uniquindio.proyecto.servicios;

import co.edu.uniquindio.proyecto.dto.reportes.CrearReporteDTO;
import co.edu.uniquindio.proyecto.dto.reportes.EstadoReporteDTO;
import co.edu.uniquindio.proyecto.dto.reportes.UbicacionDTO;
import co.edu.uniquindio.proyecto.modelo.documentos.Categoria;
import co.edu.uniquindio.proyecto.modelo.documentos.Reporte;
import co.edu.uniquindio.proyecto.modelo.documentos.Usuario;
import co.edu.uniquindio.proyecto.modelo.enums.EstadoReporte;
import co.edu.uniquindio.proyecto.modelo.vo.Ubicacion;
import co.edu.uniquindio.proyecto.repositorios.CategoriaRepo;
import co.edu.uniquindio.proyecto.repositorios.ReporteRepo;
import co.edu.uniquindio.proyecto.repositorios.UsuarioRepo;
import co.edu.uniquindio.proyecto.servicios.impl.ReporteServicioImpl;
import co.edu.uniquindio.proyecto.servicios.impl.UsuarioServicioImpl;
import co.edu.uniquindio.proyecto.mapper.ReporteMapper;
import co.edu.uniquindio.proyecto.mapper.ComentarioMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReporteServicioTest {

    @Mock
    private ReporteRepo reporteRepo;
    @Mock
    private UsuarioRepo usuarioRepo;
    @Mock
    private UsuarioServicioImpl usuarioServicio;
    @Mock
    private CategoriaRepo categoriaRepo;
    @Mock
    private ReporteMapper reporteMapper;
    @Mock
    private ComentarioMapper comentarioMapper;
    @Mock
    private EmailServicio emailServicio;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter counter;

    @InjectMocks
    private ReporteServicioImpl reporteServicio;

    @BeforeEach
    void setUp() {
        // Configuración de mocks para Micrometer
        lenient().when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(counter);
    }

    @Test
    void crearReporteConNotificacionTest() throws Exception {
        // Arrange
        String usuarioId = new ObjectId().toHexString();
        String categoriaId = new ObjectId().toHexString();
        
        CrearReporteDTO dto = new CrearReporteDTO(
                "Reporte de prueba",
                "Descripción del reporte",
                new UbicacionDTO(4.6097, -74.0817),
                "Armenia",
                null,
                categoriaId
        );

        Usuario usuarioCreador = new Usuario();
        usuarioCreador.setId(new ObjectId(usuarioId));
        usuarioCreador.setNombre("Juan");

        Categoria categoria = new Categoria();
        categoria.setId(new ObjectId(categoriaId));

        Reporte reporte = new Reporte();
        reporte.setId(new ObjectId());
        reporte.setUsuarioId(usuarioCreador.getId());
        reporte.setUbicacion(new Ubicacion(4.6097, -74.0817));
        reporte.setTitulo(dto.titulo());
        reporte.setDescripcion(dto.descripcion());

        // Usuario cercano (a 100 metros aprox)
        Usuario usuarioCercano = new Usuario();
        usuarioCercano.setId(new ObjectId());
        usuarioCercano.setNombre("Vecino");
        usuarioCercano.setEmail("vecino@test.com");
        usuarioCercano.setUbicacion(new Ubicacion(4.6098, -74.0818));

        when(usuarioServicio.obtenerIdSesion()).thenReturn(usuarioId);
        when(usuarioRepo.findById(any(ObjectId.class))).thenReturn(Optional.of(usuarioCreador));
        when(categoriaRepo.findById(any(ObjectId.class))).thenReturn(Optional.of(categoria));
        when(reporteMapper.toDocument(any(CrearReporteDTO.class))).thenReturn(reporte);
        when(usuarioRepo.findAll()).thenReturn(List.of(usuarioCreador, usuarioCercano));

        // Act
        reporteServicio.crearReporte(dto);

        // Assert
        verify(reporteRepo, times(1)).save(reporte);
        verify(emailServicio, times(1)).enviarCorreo(any()); // Verificamos que se envió el correo al vecino
        verify(counter, atLeastOnce()).increment(); // Verificamos que se registraron métricas
    }

    @Test
    void cambiarEstadoTest() throws Exception {
        // Arrange
        String reporteId = new ObjectId().toHexString();
        String usuarioId = new ObjectId().toHexString();
        
        Reporte reporte = new Reporte();
        reporte.setId(new ObjectId(reporteId));
        reporte.setUsuarioId(new ObjectId(usuarioId));
        reporte.setTitulo("Reporte a resolver");
        reporte.setHistorialReporte(new ArrayList<>());

        EstadoReporteDTO estadoDTO = new EstadoReporteDTO("RESUELTO", "Se solucionó el problema");

        when(reporteRepo.findById(reporteId)).thenReturn(Optional.of(reporte));
        when(usuarioServicio.obtenerIdSesion()).thenReturn(usuarioId);
        when(usuarioServicio.obtenerRolSesion()).thenReturn("CLIENTE");

        // Act & Assert
        assertDoesNotThrow(() -> reporteServicio.cambiarEstado(reporteId, estadoDTO));
        
        verify(reporteRepo, atLeastOnce()).save(reporte);
        verify(counter, atLeastOnce()).increment(); // Verifica la métrica de cambio de estado
    }
}
