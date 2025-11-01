package co.edu.uniquindio.proyecto.modelo.vo;


import co.edu.uniquindio.proyecto.modelo.enums.EstadoReporte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class HistorialReporte {
    @Id
    private ObjectId id;
    private ObjectId reporteId;
    private String motivo;
    private EstadoReporte estado;
    private LocalDateTime fecha;
    private LocalDateTime fechaLimiteEdicion;


}
