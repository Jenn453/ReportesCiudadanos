package co.edu.uniquindio.proyecto.modelo.documentos;

import co.edu.uniquindio.proyecto.modelo.enums.Ciudad;
import co.edu.uniquindio.proyecto.modelo.enums.EstadoReporte;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import co.edu.uniquindio.proyecto.modelo.vo.Ubicacion;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "reportes")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Reporte {

    @Id
    private ObjectId id;

    private ObjectId usuarioId;

    private String titulo;
    private String categoria;
    private String descripcion;
    private Ubicacion ubicacion;
    private Ciudad ciudad;
    private LocalDateTime fecha;
    private LocalDateTime fechaCreacion;
    private EstadoReporte estadoActual;
    private List<String> imagenes;
    private List<ObjectId> contadorImportante;
    private LocalDateTime fechaLimiteEdicion;
}