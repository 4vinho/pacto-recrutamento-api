package br.com.pacto.recrutamento.app.ports.in.curriculo;

import br.com.pacto.recrutamento.app.dtos.curriculo.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface CurriculoUseCase {
    TypedResponse<CurriculoDTO> enviarCurriculo(EnviarCurriculoDTO command);

    TypedResponse<CurriculoDTO> substituirCurriculo(SubstituirCurriculoDTO command);

    TypedResponse<UrlTemporariaCurriculoDTO> gerarUrlTemporariaCurriculo(
            GerarUrlTemporariaCurriculoDTO query);
}
