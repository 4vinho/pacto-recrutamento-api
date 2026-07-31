package br.com.pacto.recrutamento.app.usecases.candidato;

import br.com.pacto.recrutamento.app.dtos.candidato.*;
import br.com.pacto.recrutamento.app.ports.in.candidato.CandidatoUseCase;
import br.com.pacto.recrutamento.app.ports.out.candidato.CandidatoPort;
import br.com.pacto.recrutamento.app.ports.out.candidato.model.CandidaturaDoCandidato;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Candidato;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

@Service
public class CandidatoService implements CandidatoUseCase {
    private final CandidatoPort candidatoRepository;

    public CandidatoService(CandidatoPort candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    @Override
    public TypedResponse<CandidatoDTO> criarCandidato(CriarCandidatoDTO command) {
        if (command == null || command.getUsuarioId() == null) {
            return erro(400, USUARIO_AUTENTICADO_OBRIGATORIO);
        }
        if (candidatoRepository.existePorUsuarioId(command.getUsuarioId())) {
            return erro(409, PERFIL_CANDIDATO_EXISTENTE);
        }
        Candidato candidato = candidatoRepository.salvar(
                new Candidato(command.getUsuarioId(), command.getDataAdmissao()));
        return new TypedResponse<>(201, "Candidato criado", paraDto(candidato));
    }

    @Override
    public TypedResponse<CandidatoDTO> atualizarCandidato(AtualizarCandidatoDTO command) {
        if (command == null || command.getUsuarioId() == null) {
            return erro(400, USUARIO_AUTENTICADO_OBRIGATORIO);
        }
        Candidato candidato = candidatoRepository.buscarPorUsuarioId(command.getUsuarioId())
                .orElse(null);
        if (candidato == null) {
            return erro(404, PERFIL_CANDIDATO_NAO_ENCONTRADO);
        }
        candidato.setDataAdmissao(command.getDataAdmissao());
        Candidato atualizado = candidatoRepository.salvar(candidato);
        return new TypedResponse<>(200, "Candidato atualizado", paraDto(atualizado));
    }

    @Override
    public TypedPagedResponse<CandidaturaResumoDTO> listarMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query) {
        if (!consultaValida(query)) {
            return requisicaoInvalida(query);
        }
        PaginaGenerico<CandidaturaDoCandidato> pagina =
                candidatoRepository.listarCandidaturasDoUsuario(
                        query.getUsuarioId(), query.getPage(), query.getPageSize());
        List<CandidaturaResumoDTO> candidaturas = pagina.getItens().stream()
                .map(this::paraResumo)
                .collect(Collectors.toList());
        return new TypedPagedResponse<>(200, "Candidaturas encontradas", candidaturas,
                query.getPage(), query.getPageSize(), pagina.getTotalItens());
    }

    private boolean consultaValida(ListarMinhasCandidaturasDTO query) {
        return query != null
                && query.getUsuarioId() != null
                && query.getPage() >= 0
                && query.getPageSize() > 0;
    }

    private TypedPagedResponse<CandidaturaResumoDTO> requisicaoInvalida(
            ListarMinhasCandidaturasDTO query) {
        int page = query != null && query.getPage() >= 0 ? query.getPage() : 0;
        int pageSize = query != null && query.getPageSize() > 0 ? query.getPageSize() : 1;
        return new TypedPagedResponse<>(400, "Consulta de candidaturas invalida",
                Collections.<CandidaturaResumoDTO>emptyList(), page, pageSize, 0);
    }

    private TypedResponse<CandidatoDTO> erro(int statusCode, String mensagem) {
        return new TypedResponse<>(statusCode, mensagem, null);
    }

    private CandidatoDTO paraDto(Candidato candidato) {
        return new CandidatoDTO(candidato.getId(), candidato.getUsuarioId(), candidato.getDataAdmissao());
    }

    private CandidaturaResumoDTO paraResumo(CandidaturaDoCandidato candidatura) {
        return new CandidaturaResumoDTO(candidatura.getCandidaturaId(), candidatura.getVagaId(),
                candidatura.getTituloVaga(), candidatura.getStatus(), candidatura.getCriadaEm(),
                candidatura.getFeedback());
    }
}
