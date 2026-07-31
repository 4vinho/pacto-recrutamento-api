package br.com.pacto.recrutamento.app.services;

import br.com.pacto.recrutamento.app.dtos.candidato.*;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface CandidatoService {
    TypedResponse<CandidatoDTO> criarCandidato(CriarCandidatoDTO command);
    TypedResponse<CandidatoDTO> atualizarCandidato(AtualizarCandidatoDTO command);
    TypedPagedResponse<CandidaturaResumoDTO> listarMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query);
}
