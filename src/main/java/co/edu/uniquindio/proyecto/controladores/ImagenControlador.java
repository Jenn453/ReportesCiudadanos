package co.edu.uniquindio.proyecto.controladores;

import co.edu.uniquindio.proyecto.dto.MensajeDTO;
import co.edu.uniquindio.proyecto.servicios.ImagenServicio;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/imagenes")
public class ImagenControlador {

    private final ImagenServicio imagenServicio;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Subir imagen reporte")
    public String subir(@RequestParam("imagen") MultipartFile imagen) throws Exception {
        Map<String, Object> response = imagenServicio.subirImagen(imagen);
        return response.get("url").toString();
    }

    public ResponseEntity<MensajeDTO<String>> eliminar(
            String id) throws Exception {
        imagenServicio.eliminarImagen(id);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Imagen eliminada correctamente"));

    }
}