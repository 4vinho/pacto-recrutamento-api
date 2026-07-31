package br.com.pacto.recrutamento.app.curriculo;

import br.com.pacto.recrutamento.app.dtos.curriculo.CurriculoDTO;
import br.com.pacto.recrutamento.app.dtos.curriculo.EnviarCurriculoDTO;
import br.com.pacto.recrutamento.app.dtos.curriculo.GerarUrlTemporariaCurriculoDTO;
import br.com.pacto.recrutamento.app.dtos.curriculo.SubstituirCurriculoDTO;
import br.com.pacto.recrutamento.app.dtos.curriculo.UrlTemporariaCurriculoDTO;
import br.com.pacto.recrutamento.app.services.CurriculoService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Curriculo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class CurriculoServiceImpl implements CurriculoService {
    private static final int TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;
    private static final Duration DURACAO_URL = Duration.ofMinutes(5);
    private static final String PDF = "application/pdf";

    private final CurriculoRepositorio repositorio;
    private final ArquivoStorage storage;
    private final CandidatoConsulta candidatos;
    private final RemocaoCurriculoPendente remocoesPendentes;
    private final Clock clock;

    public CurriculoServiceImpl(CurriculoRepositorio repositorio, ArquivoStorage storage,
                                CandidatoConsulta candidatos,
                                RemocaoCurriculoPendente remocoesPendentes, Clock clock) {
        this.repositorio = repositorio;
        this.storage = storage;
        this.candidatos = candidatos;
        this.remocoesPendentes = remocoesPendentes;
        this.clock = clock;
    }

    @Override
    public TypedResponse<CurriculoDTO> enviarCurriculo(EnviarCurriculoDTO command) {
        Arquivo recebido = Arquivo.de(command.getNomeOriginal(), command.getConteudo());
        if (!recebido.ehPdfValido()) return erro(400, "O currículo deve ser um PDF válido");
        Optional<UUID> candidatoId = candidatos.buscarIdPorUsuario(command.getUsuarioId());
        if (!candidatoId.isPresent()) return erro(404, "Candidato não encontrado");
        if (repositorio.buscarAtivoPorCandidato(candidatoId.get()).isPresent()) {
            return erro(409, "Já existe um currículo ativo");
        }
        return salvarNovo(candidatoId.get(), recebido);
    }

    @Override
    public TypedResponse<CurriculoDTO> substituirCurriculo(SubstituirCurriculoDTO command) {
        Arquivo recebido = Arquivo.de(command.getNomeOriginal(), command.getConteudo());
        if (!recebido.ehPdfValido()) return erro(400, "O currículo deve ser um PDF válido");
        Optional<UUID> candidatoId = candidatos.buscarIdPorUsuario(command.getUsuarioId());
        if (!candidatoId.isPresent()) return erro(404, "Candidato não encontrado");
        Optional<Curriculo> anterior = repositorio.buscarAtivoPorCandidato(candidatoId.get());
        if (!anterior.isPresent()) return erro(404, "Currículo ativo não encontrado");
        Curriculo novo = novoCurriculo(candidatoId.get(), recebido);
        try {
            storage.armazenar(novo.getStorageKey(), recebido.conteudo, PDF);
            repositorio.substituir(anterior.get(), novo, agora());
        } catch (RuntimeException e) {
            compensar(novo.getStorageKey());
            return erro(500, "Não foi possível substituir o currículo");
        }
        removerAnterior(anterior.get().getStorageKey());
        return sucesso(200, novo);
    }

    @Override
    public TypedResponse<UrlTemporariaCurriculoDTO> gerarUrlTemporariaCurriculo(
            GerarUrlTemporariaCurriculoDTO query) {
        Optional<Curriculo> curriculo = repositorio.buscarAtivoPorId(query.getCurriculoId());
        if (!curriculo.isPresent()) return new TypedResponse<UrlTemporariaCurriculoDTO>(404, "Currículo não encontrado", null);
        if (!candidatos.pertenceAoUsuario(curriculo.get().getCandidatoId(), query.getUsuarioSolicitanteId())) {
            return new TypedResponse<UrlTemporariaCurriculoDTO>(403, "Acesso não autorizado", null);
        }
        try {
            OffsetDateTime expiraEm = agora().plus(DURACAO_URL);
            String url = storage.gerarUrlTemporaria(curriculo.get().getStorageKey(), DURACAO_URL);
            return new TypedResponse<UrlTemporariaCurriculoDTO>(200, "URL temporária gerada",
                    new UrlTemporariaCurriculoDTO(url, expiraEm));
        } catch (RuntimeException e) {
            return new TypedResponse<UrlTemporariaCurriculoDTO>(500, "Não foi possível gerar a URL temporária", null);
        }
    }

    private TypedResponse<CurriculoDTO> salvarNovo(UUID candidatoId, Arquivo recebido) {
        Curriculo novo = novoCurriculo(candidatoId, recebido);
        try {
            storage.armazenar(novo.getStorageKey(), recebido.conteudo, PDF);
            repositorio.salvar(novo);
            return sucesso(201, novo);
        } catch (RuntimeException e) {
            compensar(novo.getStorageKey());
            return erro(500, "Não foi possível salvar o currículo");
        }
    }

    private Curriculo novoCurriculo(UUID candidatoId, Arquivo arquivo) {
        UUID id = UUID.randomUUID();
        return new Curriculo(candidatoId, "curriculos/" + candidatoId + "/" + id + ".pdf",
                arquivo.nomeOriginal, PDF, arquivo.conteudo.length, arquivo.checksum());
    }

    private void compensar(String storageKey) {
        try { storage.remover(storageKey); }
        catch (RuntimeException e) { remocoesPendentes.registrar(storageKey, e.getMessage()); }
    }

    private void removerAnterior(String storageKey) {
        try { storage.remover(storageKey); }
        catch (RuntimeException e) { remocoesPendentes.registrar(storageKey, e.getMessage()); }
    }

    private OffsetDateTime agora() { return OffsetDateTime.now(clock); }

    private TypedResponse<CurriculoDTO> sucesso(int status, Curriculo curriculo) {
        return new TypedResponse<CurriculoDTO>(status, "Currículo salvo", new CurriculoDTO(curriculo.getId(),
                curriculo.getNomeOriginal(), curriculo.getContentType(), curriculo.getTamanhoBytes(), curriculo.getChecksumSha256()));
    }

    private TypedResponse<CurriculoDTO> erro(int status, String mensagem) {
        return new TypedResponse<CurriculoDTO>(status, mensagem, null);
    }

    private static final class Arquivo {
        private final String nomeOriginal;
        private final byte[] conteudo;

        private Arquivo(String nomeOriginal, byte[] conteudo) {
            this.nomeOriginal = nomeOriginal;
            this.conteudo = conteudo;
        }

        private static Arquivo de(String nomeOriginal, byte[] conteudo) {
            return new Arquivo(nomeOriginal, conteudo == null ? new byte[0] : conteudo);
        }

        private boolean ehPdfValido() {
            return conteudo.length > 0 && conteudo.length <= TAMANHO_MAXIMO_BYTES
                    && conteudo.length >= 5 && conteudo[0] == '%' && conteudo[1] == 'P'
                    && conteudo[2] == 'D' && conteudo[3] == 'F' && conteudo[4] == '-';
        }

        private String checksum() {
            try {
                byte[] hash = MessageDigest.getInstance("SHA-256").digest(conteudo);
                return hexadecimal(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 indisponível", e);
            }
        }

        private String hexadecimal(byte[] bytes) {
            StringBuilder resultado = new StringBuilder();
            for (byte value : bytes) resultado.append(String.format("%02x", value));
            return resultado.toString();
        }
    }
}
