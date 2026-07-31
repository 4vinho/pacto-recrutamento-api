package br.com.pacto.recrutamento.app;

import br.com.pacto.recrutamento.app.dtos.candidatura.AtualizarStatusCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.CancelarCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.ConsultarCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.CriarCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.RegistrarRespostasDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.RegistrarRequisitosDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.app.dtos.usuario.EncerrarSessaoDTO;
import br.com.pacto.recrutamento.app.dtos.usuario.RedefinirSenhaDTO;
import br.com.pacto.recrutamento.app.dtos.usuario.RenovarSessaoDTO;
import br.com.pacto.recrutamento.app.dtos.usuario.SolicitarRecuperacaoSenhaDTO;
import br.com.pacto.recrutamento.app.usecases.candidatura.CandidaturaService;
import br.com.pacto.recrutamento.app.usecases.notificacao.NotificacaoService;
import br.com.pacto.recrutamento.app.usecases.usuario.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class TransacoesCasosUsoTest {
    @Test
    void operacoesCompostasDeUsuarioSaoTransacionais() throws Exception {
        assertTransacional(UsuarioService.class, "renovarSessao", RenovarSessaoDTO.class);
        assertTransacional(UsuarioService.class, "encerrarSessao", EncerrarSessaoDTO.class);
        assertTransacional(UsuarioService.class, "redefinirSenha", RedefinirSenhaDTO.class);
        assertTransacional(UsuarioService.class, "solicitarRecuperacaoSenha",
                SolicitarRecuperacaoSenhaDTO.class);
    }

    @Test
    void mutacoesDeCandidaturaSaoTransacionais() throws Exception {
        assertTransacional(CandidaturaService.class, "criarCandidatura", CriarCandidaturaDTO.class);
        assertTransacional(CandidaturaService.class, "registrarRespostas", RegistrarRespostasDTO.class);
        assertTransacional(CandidaturaService.class, "registrarRequisitos", RegistrarRequisitosDTO.class);
        assertTransacional(CandidaturaService.class, "atualizarStatusCandidatura",
                AtualizarStatusCandidaturaDTO.class);
        assertTransacional(CandidaturaService.class, "cancelarCandidatura",
                CancelarCandidaturaDTO.class);
        Transactional consulta = transacao(CandidaturaService.class, "consultarCandidatura",
                ConsultarCandidaturaDTO.class);
        assertThat(consulta.readOnly()).isTrue();
    }

    @Test
    void processamentoPersistenteDeNotificacoesETransacional() throws Exception {
        assertTransacional(NotificacaoService.class, "processarCandidaturaCriada",
                CandidaturaCriadaDTO.class);
        assertTransacional(NotificacaoService.class, "processarStatusCandidaturaAlterado",
                StatusCandidaturaAlteradoDTO.class);
    }

    private void assertTransacional(Class<?> tipo, String metodo, Class<?> parametro)
            throws Exception {
        assertThat(transacao(tipo, metodo, parametro)).isNotNull();
    }

    private Transactional transacao(Class<?> tipo, String metodo, Class<?> parametro)
            throws Exception {
        Method encontrado = tipo.getMethod(metodo, parametro);
        return encontrado.getAnnotation(Transactional.class);
    }
}
