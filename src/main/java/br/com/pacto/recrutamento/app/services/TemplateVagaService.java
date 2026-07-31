package br.com.pacto.recrutamento.app.services;

import br.com.pacto.recrutamento.app.dtos.templatevaga.*;
import br.com.pacto.recrutamento.app.dtos.vaga.VagaDTO;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface TemplateVagaService {
    TypedResponse<TemplateVagaDTO> criarTemplate(CriarTemplateVagaDTO command);
    TypedResponse<TemplateVagaDTO> atualizarTemplate(AtualizarTemplateVagaDTO command);
    TypedResponse<Void> excluirTemplate(ExcluirItemTemplateVagaDTO command);
    TypedResponse<PerguntaTemplateVagaDTO> criarPerguntaDoTemplate(
            SalvarPerguntaTemplateVagaDTO command);
    TypedResponse<PerguntaTemplateVagaDTO> atualizarPerguntaDoTemplate(
            SalvarPerguntaTemplateVagaDTO command);
    TypedResponse<Void> excluirPerguntaDoTemplate(ExcluirItemTemplateVagaDTO command);
    TypedResponse<RequisitoTemplateVagaDTO> criarRequisitoDoTemplate(
            SalvarRequisitoTemplateVagaDTO command);
    TypedResponse<RequisitoTemplateVagaDTO> atualizarRequisitoDoTemplate(
            SalvarRequisitoTemplateVagaDTO command);
    TypedResponse<Void> excluirRequisitoDoTemplate(ExcluirItemTemplateVagaDTO command);
    TypedResponse<VagaDTO> criarVagaAPartirDoTemplate(CriarVagaAPartirDoTemplateDTO command);
}
