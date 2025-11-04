package co.edu.uniquindio.proyecto.servicios.impl;
import co.edu.uniquindio.proyecto.dto.notificaciones.EmailDTO;
import co.edu.uniquindio.proyecto.servicios.EmailServicio;
import lombok.RequiredArgsConstructor;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServicioImpl implements EmailServicio {

    @Async
    @Override
    public void enviarCorreo(EmailDTO emailDTO) {
        Email email = EmailBuilder.startingBlank()
                .from("moderadoralertas@gmail.com")
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp-relay.brevo.com",587,"9ac2c1001@smtp-brevo.com","bskV9K51G2286pc")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()){

            mailer.sendMail(email);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void enviarCorreoComentarioReporte(String nombreUsuario, String comentario, String destinatario) {
        String asunto = "Nuevo comentario en tu reporte";
        String cuerpoCorreo = "Hola,\n\nEl usuario " + nombreUsuario + " ha comentado en tu reporte:\n\n"
                + "\"" + comentario + "\"\n\n"
                + "Revisa el reporte para más detalles.";

        EmailDTO emailDTO = new EmailDTO(asunto, cuerpoCorreo, destinatario);
        enviarCorreo(emailDTO); // Usa el método ya existente
    }
}