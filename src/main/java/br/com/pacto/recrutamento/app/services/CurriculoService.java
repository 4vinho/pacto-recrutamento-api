package br.com.pacto.recrutamento.app.services;

import br.com.pacto.recrutamento.app.dtos.curriculo.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface CurriculoService {
    TypedResponse<CurriculoDTO> enviarCurriculo(EnviarCurriculoDTO command);
    TypedResponse<CurriculoDTO> substituirCurriculo(SubstituirCurriculoDTO command);
    TypedResponse<UrlTemporariaCurriculoDTO> gerarUrlTemporariaCurriculo(
            GerarUrlTemporariaCurriculoDTO query);
}
