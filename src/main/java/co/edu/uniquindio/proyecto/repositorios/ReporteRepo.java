package co.edu.uniquindio.proyecto.repositorios;


import co.edu.uniquindio.proyecto.dto.reportes.ReporteDTO;
import co.edu.uniquindio.proyecto.modelo.documentos.Reporte;
import co.edu.uniquindio.proyecto.modelo.enums.EstadoReporte;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReporteRepo extends MongoRepository<Reporte, String> {

    Optional<Reporte> findByUbicacion_LatitudAndUbicacion_LongitudAndDescripcion(double latitud, double longitud, String descripcion);

    List<Reporte> findByUsuarioId(ObjectId usuarioId);


    List<Reporte> findByEstadoActual(EstadoReporte estado);

    @Aggregation({
            "{ $match: { _id: ?0 } }",
            "{ $addFields: { cantidadImportante: { $cond: { if: { $isArray: '$contadorImportante' }, then: { $size: '$contadorImportante' }, else: 0 } } } }",
            "{ $lookup: { from: 'categorias', localField: 'categoriaId', foreignField: '_id', as: 'categoria' } }",
            "{ $unwind: '$categoria' }",
            "{ $lookup: { from: 'usuarios', localField: 'usuarioId', foreignField: '_id', as: 'usuario' } }",
            "{ $unwind: '$usuario' }",
            "{ $project: { usuario: '$usuario.nombre', titulo: 1, categoria: '$categoria.nombre', descripcion: 1, ubicacion: 1, estadoActual: 1, imagenes: 1, fechaCreacion: 1, cantidadImportante: 1 } }"
    })
    ReporteDTO obtenerReporteId(ObjectId id);

    @Aggregation({
            "{ $match: { estadoActual: { $in: ['VERIFICADO', 'RECHAZADO', 'PENDIENTE'] } } }",
            "{ $addFields: { cantidadImportante: { $cond: { if: { $isArray: '$contadorImportante' }, then: { $size: '$contadorImportante' }, else: 0 } } } }",
            "{ $lookup: { from: 'categorias', localField: 'categoriaId', foreignField: '_id', as: 'categoria' } }",
            "{ $unwind: '$categoria' }",
            "{ $lookup: { from: 'usuarios', localField: 'usuarioId', foreignField: '_id', as: 'usuario' } }",
            "{ $unwind: '$categoria' }",
            "{ $project: { usuario: '$usuario.nombre', titulo: '$titulo', categoria: '$categoria.nombre', descripcion: '$descripcion', ubicacion: '$ubicacion', estadoActual: '$estadoActual', imagenes: '$imagenes', fechaCreacion: '$fechaCreacion' ,cantidadImportante: 1 } }"
    })
    List<ReporteDTO> obtenerReportes();

    @Aggregation({
            "{ $match: { usuarioId: ?0 } }",
            "{ $addFields: { cantidadImportante: { $cond: { if: { $isArray: '$contadorImportante' }, then: { $size: '$contadorImportante' }, else: 0 } } } }",
            "{ $lookup: { from: 'categorias', localField: 'categoriaId', foreignField: '_id', as: 'categoria' } }",
            "{ $unwind: '$categoria' }",
            "{ $lookup: { from: 'usuarios', localField: 'usuarioId', foreignField: '_id', as: 'usuario' } }",
            "{ $unwind: '$usuario' }",
            "{ $project: { usuario: '$usuario.nombre', titulo: '$titulo', categoria: '$categoria.nombre', descripcion: '$descripcion', ubicacion: '$ubicacion', estadoActual: '$estadoActual', imagenes: '$imagenes', fechaCreacion: '$fechaCreacion', cantidadImportante: 1 } }"
    })
    List<ReporteDTO> obtenerReportesUsuario(ObjectId usuarioId);

    @Aggregation(pipeline = {
            "{ $geoNear: { " +
                    "near: { type: 'Point', coordinates: [?0, ?1] }, " +
                    "distanceField: 'distancia', " +
                    "maxDistance: 5000, " +
                    "spherical: true } }",
            "{ $match: { estadoActual: { $in: ['VERIFICADO', 'RECHAZADO', 'PENDIENTE'] } } }",
            "{ $addFields: { cantidadImportante: { $cond: { if: { $isArray: '$contadorImportante' }, then: { $size: '$contadorImportante' }, else: 0 } } } }",
            "{ $lookup: { from: 'categorias', localField: 'categoriaId', foreignField: '_id', as: 'categoria' } }",
            "{ $unwind: '$categoria' }",
            "{ $lookup: { from: 'usuarios', localField: 'usuarioId', foreignField: '_id', as: 'usuario' } }",
            "{ $unwind: '$usuario' }",
            "{ $project: { usuario: '$usuario.nombre', titulo: '$titulo', categoria: '$categoria.nombre', descripcion: '$descripcion', ubicacion: '$ubicacion', estadoActual: '$estadoActual', imagenes: '$imagenes', fechaCreacion: '$fechaCreacion', distancia: '$distancia',cantidadImportante: 1 } }"
    })
    List<ReporteDTO> obtenerReportesCerca(double latitud, double longitud);

    @Aggregation({
            "{ $match: { estadoActual: { $in: ['VERIFICADO', 'RECHAZADO', 'PENDIENTE'] } } }",
            "{ $addFields: { cantidadImportante: { $cond: { if: { $isArray: '$contadorImportante' }, then: { $size: '$contadorImportante' }, else: 0 } } } }",
            "{ $sort: { cantidadImportante: -1 } }",
            "{ $limit: 10 }",
            "{ $lookup: { from: 'categorias', localField: 'categoriaId', foreignField: '_id', as: 'categoria' } }",
            "{ $unwind: '$categoria' }",
            "{ $lookup: { from: 'usuarios', localField: 'usuarioId', foreignField: '_id', as: 'usuario' } }",
            "{ $unwind: '$usuario' }",
            "{ $project: { usuario: '$usuario.nombre', titulo: 1, categoria: '$categoria.nombre', descripcion: 1, ubicacion: 1, estadoActual: 1, imagenes: 1, fechaCreacion: 1, cantidadImportante: 1 } }"
    })
    List<ReporteDTO> obtenerTopReportes();

}
