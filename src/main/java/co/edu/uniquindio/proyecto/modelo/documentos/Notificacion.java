package co.edu.uniquindio.proyecto.modelo.documentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    private ObjectId id;
    private String email;
    private String asunto;
    private String mensaje;
    private LocalDateTime fecha;
    private String tipo;
    private ObjectId idUsuario;
    private ObjectId reporteId;
    private boolean leida;

}
