package co.edu.uniquindio.proyecto.repositorios;

import co.edu.uniquindio.proyecto.modelo.documentos.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepo extends MongoRepository<Usuario, ObjectId>{
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(@Email @NotBlank String email);

    Optional<Usuario> findById(ObjectId id);


}
