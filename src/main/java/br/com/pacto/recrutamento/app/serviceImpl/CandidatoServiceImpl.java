package br.com.pacto.recrutamento.app.serviceImpl;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoPersistido;
import br.com.pacto.recrutamento.app.ports.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.ports.candidato.CandidaturaDoCandidato;
import br.com.pacto.recrutamento.app.ports.candidato.PaginaCandidaturas;

import br.com.pacto.recrutamento.app.dtos.candidato.AtualizarCandidatoDTO;
import br.com.pacto.recrutamento.app.dtos.candidato.CandidatoDTO;
import br.com.pacto.recrutamento.app.dtos.candidato.CandidaturaResumoDTO;
import br.com.pacto.recrutamento.app.dtos.candidato.CriarCandidatoDTO;
import br.com.pacto.recrutamento.app.dtos.candidato.ListarMinhasCandidaturasDTO;
import br.com.pacto.recrutamento.app.services.CandidatoService;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidatoServiceImpl implements CandidatoService {
    private final CandidatoRepository candidatoRepository;

    public CandidatoServiceImpl(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    @Override
    public TypedResponse<CandidatoDTO> criarCandidato(CriarCandidatoDTO command) {
        if (command == null || command.getUsuarioId() == null) {
            return erro(400, "O usuario autenticado e obrigatorio");
        }
        if (candidatoRepository.existePorUsuarioId(command.getUsuarioId())) {
            return erro(409, "O usuario ja possui perfil de candidato");
        }
        CandidatoPersistido candidato = candidatoRepository.salvar(
                command.getUsuarioId(), command.getDataAdmissao());
        return new TypedResponse<>(201, "Candidato criado", paraDto(candidato));
    }

    @Override
    public TypedResponse<CandidatoDTO> atualizarCandidato(AtualizarCandidatoDTO command) {
        if (command == null || command.getUsuarioId() == null) {
            return erro(400, "O usuario autenticado e obrigatorio");
        }
        CandidatoPersistido candidato = candidatoRepository.buscarPorUsuarioId(command.getUsuarioId())
                .orElse(null);
        if (candidato == null) {
            return erro(404, "Perfil de candidato nao encontrado");
        }
        CandidatoPersistido atualizado = candidatoRepository.atualizar(candidato, command.getDataAdmissao());
        return new TypedResponse<>(200, "Candidato atualizado", paraDto(atualizado));
    }

    @Override
    public TypedPagedResponse<CandidaturaResumoDTO> listarMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query) {
        if (!consultaValida(query)) {
            return requisicaoInvalida(query);
        }
        PaginaCandidaturas pagina = candidatoRepository.listarCandidaturasDoUsuario(
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

    private CandidatoDTO paraDto(CandidatoPersistido candidato) {
        return new CandidatoDTO(candidato.getId(), candidato.getUsuarioId(), candidato.getDataAdmissao());
    }

    private CandidaturaResumoDTO paraResumo(CandidaturaDoCandidato candidatura) {
        return new CandidaturaResumoDTO(candidatura.getCandidaturaId(), candidatura.getVagaId(),
                candidatura.getTituloVaga(), candidatura.getStatus(), candidatura.getCriadaEm(),
                candidatura.getFeedback());
    }
}
