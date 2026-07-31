package br.com.pacto.recrutamento.app.services;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface VagaService {
    TypedResponse<VagaDTO> criarVaga(CriarVagaDTO command);
    TypedResponse<VagaDTO> atualizarVaga(AtualizarVagaDTO command);
    TypedResponse<VagaDTO> alterarStatusVaga(AlterarStatusVagaDTO command);
    TypedResponse<Void> excluirVaga(ExcluirVagaDTO command);
    TypedResponse<PerguntaVagaDTO> criarPerguntaDaVaga(SalvarPerguntaVagaDTO command);
    TypedResponse<PerguntaVagaDTO> atualizarPerguntaDaVaga(SalvarPerguntaVagaDTO command);
    TypedResponse<Void> excluirPerguntaDaVaga(ExcluirItemVagaDTO command);
    TypedResponse<RequisitoVagaDTO> criarRequisitoDaVaga(SalvarRequisitoVagaDTO command);
    TypedResponse<RequisitoVagaDTO> atualizarRequisitoDaVaga(SalvarRequisitoVagaDTO command);
    TypedResponse<Void> excluirRequisitoDaVaga(ExcluirItemVagaDTO command);
}
