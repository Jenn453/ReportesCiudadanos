package co.edu.uniquindio.proyecto.mapper;

import co.edu.uniquindio.proyecto.dto.usuarios.CambiarPasswordDTO;
import co.edu.uniquindio.proyecto.dto.usuarios.CrearUsuarioDTO;
import co.edu.uniquindio.proyecto.dto.usuarios.EditarUsuarioDTO;
import co.edu.uniquindio.proyecto.dto.usuarios.UsuarioDTO;
import co.edu.uniquindio.proyecto.modelo.documentos.Usuario;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "rol", constant = "CLIENTE")
    @Mapping(target = "estado", constant = "INACTIVO")
    @Mapping(target = "fechaRegistro", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "password", ignore = true)
    Usuario toDocument(CrearUsuarioDTO usuarioDTO) throws Exception;

    UsuarioDTO toDTO(Usuario usuario);

    // Metodo para mapear de ObjectId a String
    default String map(ObjectId value) {
        return value != null ? value.toString() : null;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "codigoValidacion", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    void toDocument(EditarUsuarioDTO editarUsuarioDTO, @MappingTarget Usuario usuario) throws Exception;

    @Mapping(target = "password", source = "nuevoPassword") // Solo cambia la contraseña
    void actualizarPassword(@MappingTarget Usuario usuario, CambiarPasswordDTO cambiarPasswordDTO);

}