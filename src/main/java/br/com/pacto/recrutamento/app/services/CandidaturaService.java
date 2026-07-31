package br.com.pacto.recrutamento.app.services;

import br.com.pacto.recrutamento.app.dtos.candidatura.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface CandidaturaService {
    TypedResponse<CandidaturaDTO> criarCandidatura(CriarCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> registrarRespostas(RegistrarRespostasDTO command);

    TypedResponse<CandidaturaDTO> atualizarStatusCandidatura(
            AtualizarStatusCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> cancelarCandidatura(CancelarCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> consultarCandidatura(ConsultarCandidaturaDTO query);
}
