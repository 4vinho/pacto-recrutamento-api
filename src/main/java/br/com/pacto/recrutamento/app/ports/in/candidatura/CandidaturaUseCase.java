package br.com.pacto.recrutamento.app.ports.in.candidatura;

import br.com.pacto.recrutamento.app.dtos.candidatura.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface CandidaturaUseCase {
    TypedResponse<CandidaturaDTO> criarCandidatura(CriarCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> registrarRespostas(RegistrarRespostasDTO command);

    TypedResponse<CandidaturaDTO> atualizarStatusCandidatura(
            AtualizarStatusCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> cancelarCandidatura(CancelarCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> consultarCandidatura(ConsultarCandidaturaDTO query);
}
