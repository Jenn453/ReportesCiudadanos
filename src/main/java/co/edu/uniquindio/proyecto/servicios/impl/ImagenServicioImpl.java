package co.edu.uniquindio.proyecto.servicios.impl;

import co.edu.uniquindio.proyecto.servicios.ImagenServicio;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImagenServicioImpl implements ImagenServicio {

    private final Cloudinary cloudinary;

    public ImagenServicioImpl(){
        Map<String,String> config = new HashMap<>();
        config.put("cloud_name", "dmbcs2t8r");
        config.put("api_key", "267518385518919");
        config.put("api_secret","nQLZYgFmbGUGYvwbx7ufZcbAhXQ");

        cloudinary = new Cloudinary(config);
    }

    @Override
    public Map<String, Object> subirImagen(MultipartFile imagen) throws Exception {
        File file = convertir(imagen);

        Map<String, Object> opciones = ObjectUtils.asMap(
                "folder", "ProyectoAlertas" // Guardar en la carpeta ProyectiAlertas
        );

        Map<String, Object> resultado = cloudinary.uploader().upload(file, opciones);
        file.delete(); // Eliminar archivo temporal después de la subida
        return resultado;
    }



    @Override
    public Map eliminarImagen(String idImagen) throws Exception {
        return cloudinary.uploader().destroy(idImagen, ObjectUtils.emptyMap());
    }
    private File convertir(MultipartFile imagen) throws IOException {
        File file = File.createTempFile(imagen.getOriginalFilename(),null);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(imagen.getBytes());
        fos.close();
        return file;
    }
}
