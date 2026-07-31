package br.com.pacto.recrutamento.app.usecases.curriculo;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.app.dtos.curriculo.*;
import br.com.pacto.recrutamento.app.ports.in.curriculo.CurriculoUseCase;
import br.com.pacto.recrutamento.app.ports.out.curriculo.ArquivoStoragePort;
import br.com.pacto.recrutamento.app.ports.out.curriculo.CandidatoConsultaPort;
import br.com.pacto.recrutamento.app.ports.out.curriculo.CurriculoPort;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Curriculo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CurriculoService implements CurriculoUseCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurriculoService.class);
    private static final int TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;
    private static final Duration DURACAO_URL = Duration.ofMinutes(5);
    private static final String PDF = "application/pdf";

    private final CurriculoPort repositorio;
    private final ArquivoStoragePort storage;
    private final CandidatoConsultaPort candidatos;
    private final Clock clock;

    public CurriculoService(CurriculoPort repositorio, ArquivoStoragePort storage,
                            CandidatoConsultaPort candidatos, Clock clock) {
        this.repositorio = repositorio;
        this.storage = storage;
        this.candidatos = candidatos;
        this.clock = clock;
    }

    @Override
    public TypedResponse<CurriculoDTO> enviarCurriculo(EnviarCurriculoDTO command) {
        if (command == null || command.getUsuarioId() == null) {
            return erro(400, DADOS_INVALIDOS);
        }
        Arquivo recebido = Arquivo.de(command.getNomeOriginal(), command.getConteudo());
        if (!recebido.nomeValido()) return erro(400, NOME_ARQUIVO_CURRICULO_INVALIDO);
        if (!recebido.ehPdfValido()) return erro(400, CURRICULO_PDF_INVALIDO);
        Optional<UUID> candidatoId = candidatos.buscarIdPorUsuario(command.getUsuarioId());
        if (!candidatoId.isPresent()) return erro(404, CANDIDATO_NAO_ENCONTRADO);
        if (repositorio.buscarAtivoPorCandidato(candidatoId.get()).isPresent()) {
            return erro(409, CURRICULO_ATIVO_EXISTENTE);
        }
        return salvarNovo(candidatoId.get(), recebido);
    }

    @Override
    public TypedResponse<CurriculoDTO> substituirCurriculo(SubstituirCurriculoDTO command) {
        if (command == null || command.getUsuarioId() == null) {
            return erro(400, DADOS_INVALIDOS);
        }
        Arquivo recebido = Arquivo.de(command.getNomeOriginal(), command.getConteudo());
        if (!recebido.nomeValido()) return erro(400, NOME_ARQUIVO_CURRICULO_INVALIDO);
        if (!recebido.ehPdfValido()) return erro(400, CURRICULO_PDF_INVALIDO);
        Optional<UUID> candidatoId = candidatos.buscarIdPorUsuario(command.getUsuarioId());
        if (!candidatoId.isPresent()) return erro(404, CANDIDATO_NAO_ENCONTRADO);
        Optional<Curriculo> anterior = repositorio.buscarAtivoPorCandidato(candidatoId.get());
        if (!anterior.isPresent()) return erro(404, CURRICULO_ATIVO_NAO_ENCONTRADO);
        Curriculo novo = novoCurriculo(candidatoId.get(), recebido);
        try {
            storage.armazenar(novo.getStorageKey(), recebido.conteudo, PDF);
            repositorio.substituir(anterior.get(), novo, agora());
        } catch (RuntimeException e) {
            compensar(novo.getStorageKey());
            return erro(500, SUBSTITUICAO_CURRICULO_FALHOU);
        }
        removerAnterior(anterior.get().getStorageKey());
        return sucesso(200, novo);
    }

    @Override
    public TypedResponse<UrlTemporariaCurriculoDTO> gerarUrlTemporariaCurriculo(
            GerarUrlTemporariaCurriculoDTO query) {
        if (query == null || query.getUsuarioSolicitanteId() == null
                || query.getCurriculoId() == null) {
            return new TypedResponse<UrlTemporariaCurriculoDTO>(400, DADOS_INVALIDOS, null);
        }
        Optional<Curriculo> curriculo = repositorio.buscarAtivoPorId(query.getCurriculoId());
        if (!curriculo.isPresent())
            return new TypedResponse<UrlTemporariaCurriculoDTO>(404, CURRICULO_NAO_ENCONTRADO, null);
        if (!candidatos.pertenceAoUsuario(curriculo.get().getCandidatoId(), query.getUsuarioSolicitanteId())) {
            return new TypedResponse<UrlTemporariaCurriculoDTO>(403, ACESSO_NAO_AUTORIZADO_ACENTUADO, null);
        }
        try {
            OffsetDateTime expiraEm = agora().plus(DURACAO_URL);
            String url = storage.gerarUrlTemporaria(curriculo.get().getStorageKey(), DURACAO_URL);
            return new TypedResponse<UrlTemporariaCurriculoDTO>(200, "URL temporária gerada",
                    new UrlTemporariaCurriculoDTO(url, expiraEm));
        } catch (RuntimeException e) {
            return new TypedResponse<UrlTemporariaCurriculoDTO>(500, GERACAO_URL_TEMPORARIA_FALHOU, null);
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
            return erro(500, SALVAMENTO_CURRICULO_FALHOU);
        }
    }

    private Curriculo novoCurriculo(UUID candidatoId, Arquivo arquivo) {
        UUID id = UUID.randomUUID();
        return new Curriculo(candidatoId, "curriculos/" + candidatoId + "/" + id + ".pdf",
                arquivo.nomeOriginal, PDF, arquivo.conteudo.length, arquivo.checksum());
    }

    private void compensar(String storageKey) {
        try {
            storage.remover(storageKey);
        } catch (RuntimeException e) {
            LOGGER.error("Não foi possível remover o arquivo {} durante a compensação", storageKey, e);
        }
    }

    private void removerAnterior(String storageKey) {
        try {
            storage.remover(storageKey);
        } catch (RuntimeException e) {
            LOGGER.error("Não foi possível remover o arquivo anterior {}", storageKey, e);
        }
    }

    private OffsetDateTime agora() {
        return OffsetDateTime.now(clock);
    }

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

        private boolean nomeValido() {
            if (nomeOriginal == null) return false;
            String nome = nomeOriginal.trim();
            if (nome.isEmpty() || nome.length() > 255 || nome.equals(".") || nome.equals("..")) {
                return false;
            }
            for (int i = 0; i < nome.length(); i++) {
                char caractere = nome.charAt(i);
                if (caractere == '/' || caractere == '\\' || Character.isISOControl(caractere)) {
                    return false;
                }
            }
            return true;
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
