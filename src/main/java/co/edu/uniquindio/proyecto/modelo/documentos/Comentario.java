package co.edu.uniquindio.proyecto.modelo.documentos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comentarios")
@AllArgsConstructor
@NoArgsConstructor // Necesario para el mapeo
@Getter
@Setter
public class Comentario {
    @Id
    private ObjectId id;
    private ObjectId clienteId;
    private ObjectId reporteId;
    private String nombreUsuario;
    private LocalDateTime fechaCreacion;
    private String mensaje;  // Este es el campo correcto


}