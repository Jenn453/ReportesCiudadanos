package co.edu.uniquindio.proyecto.repositorios;

import co.edu.uniquindio.proyecto.dto.reportes.HistorialReporteDTO;
import co.edu.uniquindio.proyecto.modelo.documentos.Reporte;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistorialRepo extends MongoRepository<Reporte, ObjectId> {


    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $unwind: '$historialReporte' }",
            "{ $project: { " +
                    "motivo: '$historialReporte.motivo', " +
                    "estado: '$historialReporte.estado', " +
                    "fecha: { $dateToString: { format: '%Y-%m-%d %H:%M:%S', date: '$historialReporte.fecha' } }, " +
                    "fechaLimiteEdicion: { $cond: { if: { $ifNull: [ '$historialReporte.fechaLimiteEdicion', false ] }, then: { $dateToString: { format: '%Y-%m-%d %H:%M:%S', date: '$historialReporte.fechaLimiteEdicion' } }, else: null } }" +
                    "} }"
    })
    List<HistorialReporteDTO> obtenerHistorial(ObjectId id);
}

