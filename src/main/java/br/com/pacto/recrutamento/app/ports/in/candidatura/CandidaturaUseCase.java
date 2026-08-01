package br.com.pacto.recrutamento.app.ports.in.candidatura;

import br.com.pacto.recrutamento.app.dtos.candidatura.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;

public interface CandidaturaUseCase {
    TypedPagedResponse<CandidaturaDTO> listarMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query);
    TypedResponse<ResumoCandidaturasDTO> resumirMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query);
    TypedPagedResponse<CandidaturaDTO> listarCandidaturasDaVaga(
            ListarCandidaturasDaVagaDTO query);

    TypedResponse<CandidaturaDTO> criarCandidatura(CriarCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> registrarRespostas(RegistrarRespostasDTO command);

    TypedResponse<CandidaturaDTO> registrarRequisitos(RegistrarRequisitosDTO command);

    TypedResponse<CandidaturaDTO> atualizarStatusCandidatura(
            AtualizarStatusCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> cancelarCandidatura(CancelarCandidaturaDTO command);

    TypedResponse<CandidaturaDTO> consultarCandidatura(ConsultarCandidaturaDTO query);
}
