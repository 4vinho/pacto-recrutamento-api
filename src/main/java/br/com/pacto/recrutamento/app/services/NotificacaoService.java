package br.com.pacto.recrutamento.app.services;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface NotificacaoService {
    TypedResponse<Void> processarCandidaturaCriada(CandidaturaCriadaDTO event);
    TypedResponse<Void> processarStatusCandidaturaAlterado(StatusCandidaturaAlteradoDTO event);
}
