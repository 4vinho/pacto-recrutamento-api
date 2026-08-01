package br.com.pacto.recrutamento.infra.adapters.usuario;

import br.com.pacto.recrutamento.app.ports.out.usuario.CanalRecuperacaoSenhaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.OffsetDateTime;

@Component
public class CanalRecuperacaoSenhaEmail implements CanalRecuperacaoSenhaPort {
    private final JavaMailSender mailSender;
    private final String remetente;
    private final String urlRedefinicao;

    public CanalRecuperacaoSenhaEmail(JavaMailSender mailSender,
                                      @Value("${notification.mail.from}") String remetente,
                                      @Value("${notification.mail.password-reset-url}") String urlRedefinicao) {
        this.mailSender = mailSender;
        this.remetente = remetente;
        this.urlRedefinicao = urlRedefinicao;
    }

    @Override
    public void enviar(String destinatario, String token, OffsetDateTime expiraEm) {
        String link = UriComponentsBuilder.fromUriString(urlRedefinicao).queryParam("token", token)
                .build().encode().toUriString();
        MimeMessage email = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(email, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject("Redefinicao de senha");
            helper.setText("Use o link para redefinir sua senha: " + link
                    + "\nEste link expira em " + expiraEm + ".", false);
            mailSender.send(email);
        } catch (MessagingException exception) {
            throw new MailSendException("Nao foi possivel montar o e-mail de recuperacao.", exception);
        }
    }
}
