package co.edu.uniquindio.proyecto.repositorios;


import co.edu.uniquindio.proyecto.modelo.documentos.Categoria;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepo extends MongoRepository<Categoria, ObjectId> {

    boolean existsByNombre(String nombre);

    List<Categoria> findAll();

    Categoria findById(String id);

    List<Categoria> nombre(String nombre);

    Categoria findByNombre(String nombre);

}
