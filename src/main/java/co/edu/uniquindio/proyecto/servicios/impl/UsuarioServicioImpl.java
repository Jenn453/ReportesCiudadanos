package co.edu.uniquindio.proyecto.servicios.impl;


import co.edu.uniquindio.proyecto.dto.notificaciones.EmailDTO;
import co.edu.uniquindio.proyecto.dto.usuarios.*;
import co.edu.uniquindio.proyecto.excepciones.*;
import co.edu.uniquindio.proyecto.mapper.UsuarioMapper;
import co.edu.uniquindio.proyecto.modelo.enums.Ciudad;
import co.edu.uniquindio.proyecto.modelo.enums.EstadoUsuario;
import co.edu.uniquindio.proyecto.modelo.documentos.Usuario;
import co.edu.uniquindio.proyecto.repositorios.UsuarioRepo;
import co.edu.uniquindio.proyecto.servicios.EmailServicio;
import co.edu.uniquindio.proyecto.servicios.UsuarioServicio;

import co.edu.uniquindio.proyecto.modelo.vo.CodigoValidacion;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepo usuarioRepo;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailServicio emailServicio;

    @Override
    public void crearUsuario(CrearUsuarioDTO crearUsuarioDTO) throws Exception {

        if(existeEmail(crearUsuarioDTO.email())) throw new EmailRepetidoException("El email ya existe");

        String codigoGenerado=generarCodigoAleatorio();

        Usuario usuario = usuarioMapper.toDocument(crearUsuarioDTO);
        usuario.setPassword( passwordEncoder.encode(crearUsuarioDTO.password()));
        usuarioRepo.save(usuario);

        //Se crea un codigo de validacion
        CodigoValidacion codigo= new CodigoValidacion(
                LocalDateTime.now(),
                codigoGenerado
        );

        usuario.setCodigoValidacion(codigo);
        usuarioRepo.save(usuario);

        // Enviar el código de activación por correo
        String cuerpoCorreo = "Tu código de activación es: " + codigoGenerado;
        EmailDTO emailDTO = new EmailDTO("Código de Activación", cuerpoCorreo, usuario.getEmail());
        emailServicio.enviarCorreo(emailDTO); // Enviar el correo con el código

    }

    private boolean existeEmail(String email) {
        return usuarioRepo.existsByEmail(email);
    }

    public String generarCodigoAleatorio(){
        String digitos="0123456789";
        StringBuilder codigo=new StringBuilder();
        for(int i=0;i<4;i++){
            int indice=(int) (Math.random()*digitos.length());
            codigo.append(digitos.charAt(indice));
        }
        return codigo.toString();
    }

    @Override
    public void enviarCodigoActivacion(UsuarioNuevoCodigoDTO usuarioNuevoCodigoDTO) throws Exception {
        // 1. Generar un nuevo código de activación
        String nuevoCodigo = generarCodigoAleatorio();

        // 2. Buscar el usuario por su correo
        Optional<Usuario> usuarioOptional = usuarioRepo.findByEmail(usuarioNuevoCodigoDTO.email());
        if (usuarioOptional.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró un usuario con el email " + usuarioNuevoCodigoDTO.email());
        }

        // 3. Obtener el usuario
        Usuario usuario = usuarioOptional.get();

        // 4. Crear un nuevo código de validación
        CodigoValidacion codigo = new CodigoValidacion(
                LocalDateTime.now(),
                nuevoCodigo
        );

        // 5. Asignar el nuevo código al usuario
        usuario.setCodigoValidacion(codigo);
        usuarioRepo.save(usuario); // Guardar los cambios en la base de datos

        // 6. Enviar el código de activación por correo
        String cuerpoCorreo = "Tu código de activación es: " + nuevoCodigo;
        EmailDTO emailDTO = new EmailDTO("Código de Activación", cuerpoCorreo, usuarioNuevoCodigoDTO.email());
        emailServicio.enviarCorreo(emailDTO); // Enviar el correo con el código
    }

    @Override
    public void activarCuenta(UsuarioActivacionDTO usuarioActivacionDTO) throws Exception {

        // Buscamos el usuario por email
        Optional<Usuario> usuarioOptional = usuarioRepo.findByEmail(usuarioActivacionDTO.email());

        if (usuarioOptional.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró un usuario con el email " + usuarioActivacionDTO.email());
        }

        Usuario usuario = usuarioOptional.get();

        // Verificamos si el usuario tiene un código de validación
        CodigoValidacion codigoValidacion = usuario.getCodigoValidacion();

        if (codigoValidacion == null) {
            throw new DatosInvalidosException("No se ha generado un código de activación para este usuario.");
        }

        // Verificamos si el código ha expirado (15 minutos de validez)
        LocalDateTime tiempoCreacion = codigoValidacion.getFecha();
        LocalDateTime tiempoActual = LocalDateTime.now();

        if (tiempoCreacion.plusMinutes(15).isBefore(tiempoActual)) {
            throw new CodigoExpiradoException("El código de activación ha expirado. Solicite uno nuevo.");
        }

        // Verificamos si el código ingresado es correcto
        if (!codigoValidacion.getCodigo().equals(usuarioActivacionDTO.codigo())) {
            throw new DatosInvalidosException("El código de activación es incorrecto.");
        }

        // Activamos la cuenta y eliminamos el código de validación
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setCodigoValidacion(null);
        usuarioRepo.save(usuario);
    }

    @Override
    public void editar(EditarUsuarioDTO editarUsuarioDTO) throws Exception {

        String id = obtenerIdSesion();

        //Validamos el id
        if (!ObjectId.isValid(id)) {
            throw new UsuarioNoEncontradoException("No se encontró el usuario con el id "+id);
        }


        //Buscamos el usuario que se quiere actualizar
        ObjectId objectId = new ObjectId(id);
        Optional<Usuario> usuarioOptional = usuarioRepo.findById(objectId);


        //Si no se encontró el usuario, lanzamos una excepción
        if(usuarioOptional.isEmpty()){
            throw new Exception("No se encontró el usuario con el id "+id);
        }


        // Mapear los datos actualizados al usuario existente
        Usuario usuario = usuarioOptional.get();
        usuarioMapper.toDocument(editarUsuarioDTO, usuario);


        //Como el objeto usuario ya tiene un id, el save() no crea un nuevo registro sino que actualiza el que ya existe
        usuarioRepo.save(usuario);
    }

    @Override
    public void cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        String id = obtenerIdSesion();
        //Validamos el id
        if (!ObjectId.isValid(id)) {
            throw new UsuarioNoEncontradoException("No se encontró el usuario con el id "+id);
        }


        //Buscamos el usuario que se quiere actualizar
        ObjectId objectId = new ObjectId(id);
        Optional<Usuario> usuarioOptional = usuarioRepo.findById(objectId);


        //Si no se encontró el usuario, lanzamos una excepción
        if(usuarioOptional.isEmpty()){
            throw new Exception("No se encontró el usuario con el id "+id);
        }

        // Mapear los datos actualizados al usuario existente
        Usuario usuario = usuarioOptional.get();

        // 🔹 Validar que la contraseña actual coincida con la almacenada
        if (!passwordEncoder.matches(cambiarPasswordDTO.actualPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // 🔹 Encriptar la nueva contraseña antes de actualizarla
        String nuevaPasswordEncriptada = passwordEncoder.encode(cambiarPasswordDTO.nuevoPassword());

        // 🔹 Actualizar la contraseña del usuario
        usuario.setPassword(nuevaPasswordEncriptada);

        usuarioRepo.save(usuario);

    }

    @Override
    public void eliminar() throws Exception {
        String id = obtenerIdSesion();
        //Validamos el id
        if (!ObjectId.isValid(id)) {
            throw new UsuarioNoEncontradoException("No se encontró el usuario con el id "+id);
        }

        //Buscamos el usuario que se quiere obtener
        ObjectId objectId = new ObjectId(id);
        Optional<Usuario> usuarioOptional = usuarioRepo.findById(objectId);

        //Si no se encontró el usuario, lanzamos una excepción
        if(usuarioOptional.isEmpty()){
            throw new UsuarioNoEncontradoException("No se encontró el usuario con el id "+id);
        }

        //Obtenemos el usuario que se quiere eliminar y le asignamos el estado eliminado
        Usuario usuario = usuarioOptional.get();

        //Si el usuario se encuentra eliminado
        if(usuario.getEstado().equals(EstadoUsuario.INACTIVO)){
            throw new Exception("La cuenta ya esta eliminada.");
        }

        usuario.setEstado(EstadoUsuario.ELIMINADO);

        //Como el objeto usuario ya tiene un id, el save() no crea un nuevo registro sino que actualiza el que ya existe
        usuarioRepo.save(usuario);
    }

    @Override
    public UsuarioDTO obtener() throws Exception {

        String id = obtenerIdSesion();

        //Validamos el id
        if (!ObjectId.isValid(id)) {
            throw new UsuarioNoEncontradoException("No se encontró el usuario con el id "+id);
        }

        //Buscamos el usuario que se quiere obtener
        ObjectId objectId = new ObjectId(id);
        Optional<Usuario> usuarioOptional = usuarioRepo.findById(objectId);

        //Si no se encontró el usuario, lanzamos una excepción
        if(usuarioOptional.isEmpty()){
            throw new UsuarioNoEncontradoException("No se encontró el usuario con el id "+id);
        }

        //Retornamos el usuario encontrado convertido a DTO
        return usuarioMapper.toDTO(usuarioOptional.get());

    }

    public String obtenerIdSesion(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }

    public String obtenerNombreUsuario() throws Exception {

        String id = obtenerIdSesion();

        Usuario usuario = usuarioRepo.findById(new ObjectId(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        return usuario.getNombre();

    }

    public String obtenerRolSesion() throws Exception {
        String id = obtenerIdSesion();
        Usuario usuario = usuarioRepo.findById(new ObjectId(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        return usuario.getRol().toString();

    }

}