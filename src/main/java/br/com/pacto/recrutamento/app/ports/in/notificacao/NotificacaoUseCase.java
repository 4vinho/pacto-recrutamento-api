package br.com.pacto.recrutamento.app.ports.in.notificacao;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface NotificacaoUseCase {
    TypedResponse<Void> processarCandidaturaCriada(CandidaturaCriadaDTO event);

    TypedResponse<Void> processarStatusCandidaturaAlterado(StatusCandidaturaAlteradoDTO event);
}
