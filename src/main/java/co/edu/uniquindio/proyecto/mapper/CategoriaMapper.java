package co.edu.uniquindio.proyecto.mapper;

import co.edu.uniquindio.proyecto.dto.moderadores.CategoriaDTO;
import co.edu.uniquindio.proyecto.dto.moderadores.ObtenerCategoriaDTO;
import co.edu.uniquindio.proyecto.modelo.documentos.Categoria;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toDocument(CategoriaDTO categoriaDTO);

    void toDocument(CategoriaDTO categoriaDTO, @MappingTarget Categoria categoria) throws Exception;

    // ✅ Mapear ObjectId a String explícitamente
    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    ObtenerCategoriaDTO toDTO(Categoria categoria);

    @Named("objectIdToString")
    static String objectIdToString(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }
}
