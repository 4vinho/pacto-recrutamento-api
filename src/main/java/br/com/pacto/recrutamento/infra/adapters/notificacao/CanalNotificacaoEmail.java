package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.ports.out.notificacao.CanalNotificacaoPort;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.core.entities.Usuario;
import br.com.pacto.recrutamento.infra.repositorys.usuario.UsuarioJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class CanalNotificacaoEmail implements CanalNotificacaoPort {
    private final JavaMailSender mailSender;
    private final UsuarioJpaRepository usuarios;
    private final String remetente;

    public CanalNotificacaoEmail(JavaMailSender mailSender, UsuarioJpaRepository usuarios,
                                 @Value("${notification.mail.from}") String remetente) {
        this.mailSender = mailSender;
        this.usuarios = usuarios;
        this.remetente = remetente;
    }

    @Override
    public void enviar(Notificacao notificacao) {
        Usuario usuario = usuarios.findById(notificacao.getUsuarioId())
                .orElseThrow(() -> new IllegalStateException("Destinatario da notificacao nao encontrado."));
        MimeMessage email = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(email, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(usuario.getEmail());
            helper.setSubject(notificacao.getTitulo());
            helper.setText(notificacao.getMensagem(), false);
            mailSender.send(email);
        } catch (MessagingException exception) {
            throw new MailSendException("Nao foi possivel montar o e-mail de notificacao.", exception);
        }
    }
}
