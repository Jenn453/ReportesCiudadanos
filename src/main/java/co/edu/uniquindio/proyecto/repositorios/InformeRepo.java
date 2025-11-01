package co.edu.uniquindio.proyecto.repositorios;

import co.edu.uniquindio.proyecto.dto.moderadores.InformeDTO;
import co.edu.uniquindio.proyecto.modelo.documentos.Reporte;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface InformeRepo extends MongoRepository<Reporte, ObjectId> {


    @Aggregation(pipeline = {
            "{ $lookup: { from: 'categorias', localField: 'categoriaId', foreignField: '_id', as: 'categoriaInfo' } }",
            "{ $unwind: { path: '$categoriaInfo', preserveNullAndEmptyArrays: true } }", // <- ESTA ES LA CLAVE
            "{ $match: { $expr: { $and: [ " +
                    "{ $or: [ { $eq: [?0, null] }, { $eq: ['$ciudad', ?0] } ] }, " +
                    "{ $or: [ { $eq: [?1, null] }, { $eq: ['$categoriaInfo.nombre', ?1] }, { $and: [ { $eq: [?1, null] }, { $eq: ['$categoriaInfo', null] } ] } ] }, " +
                    "{ $gte: ['$fechaCreacion', ?2] }, { $lte: ['$fechaCreacion', ?3] } " +
                    "] } } }",
            "{ $group: { _id: { ciudad: '$ciudad', categoria: '$categoriaInfo.nombre' }, cantidad: { $sum: 1 } } }",
            "{ $project: { _id: 0, ciudad: '$_id.ciudad', categoria: '$_id.categoria', cantidad: 1 } }"
    })

    List<InformeDTO> findReportesByCiudadAndFecha(String ciudad,String categoria, Date fechaInicio, Date fechaFin);
}



