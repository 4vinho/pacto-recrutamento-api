package br.com.pacto.recrutamento.app.serviceImpl;

import br.com.pacto.recrutamento.app.dtos.usuario.*;
import br.com.pacto.recrutamento.app.ports.usuario.*;
import br.com.pacto.recrutamento.app.services.UsuarioService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Papel;
import br.com.pacto.recrutamento.core.entities.RefreshToken;
import br.com.pacto.recrutamento.core.entities.Usuario;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private static final long MINUTOS_ACCESS_TOKEN = 15;
    private static final long DIAS_REFRESH_TOKEN = 7;
    private static final long MINUTOS_RECUPERACAO_SENHA = 30;
    private final UsuarioPort usuarios;
    private final PapelPort papeis;
    private final RefreshTokenPort refreshTokens;
    private final CodificadorSenha senhas;
    private final GeradorToken tokens;
    private final RecuperacaoSenhaPort recuperacoes;
    private final Clock relogio;

    public UsuarioServiceImpl(UsuarioPort usuarios, PapelPort papeis, RefreshTokenPort refreshTokens,
                              CodificadorSenha senhas, GeradorToken tokens,
                              RecuperacaoSenhaPort recuperacoes, Clock relogio) {
        this.usuarios = usuarios;
        this.papeis = papeis;
        this.refreshTokens = refreshTokens;
        this.senhas = senhas;
        this.tokens = tokens;
        this.recuperacoes = recuperacoes;
        this.relogio = relogio;
    }

    @Override
    public TypedResponse<UsuarioDTO> cadastrarUsuario(CadastrarUsuarioDTO command) {
        if (command == null) return resposta(400, "Comando de cadastro obrigat\u00f3rio.", null);
        String email = normalizarEmail(command.getEmail());
        if (emailInvalido(email) || senhaInvalida(command.getSenha()))
            return resposta(400, "Dados de cadastro inv\u00e1lidos.", null);
        if (usuarios.buscarPorEmail(email).isPresent()) return resposta(409, "E-mail j\u00e1 utilizado.", null);
        Optional<Papel> papel = papeis.buscarPorNome(NomePapel.CANDIDATO);
        if (!papel.isPresent()) return resposta(500, "N\u00e3o foi poss\u00edvel concluir o cadastro.", null);
        Usuario usuario = new Usuario(email, senhas.codificar(command.getSenha()));
        usuario.setTelefone(command.getTelefone());
        usuario.setPapeis(Collections.singleton(papel.get()));
        Usuario salvo = usuarios.salvar(usuario);
        return resposta(201, "Usu\u00e1rio cadastrado com sucesso.", usuarioDto(salvo));
    }

    @Override
    public TypedResponse<SessaoDTO> autenticarUsuario(AutenticarUsuarioDTO command) {
        if (command == null) return resposta(401, "Credenciais inv\u00e1lidas.", null);
        String email = normalizarEmail(command.getEmail());
        if (emailInvalido(email) || senhaInvalida(command.getSenha()))
            return resposta(401, "Credenciais inv\u00e1lidas.", null);
        Optional<Usuario> usuario = usuarios.buscarPorEmail(email);
        if (!usuario.isPresent() || !usuario.get().isAtivo() || !senhas.confere(command.getSenha(), usuario.get().getSenhaHash()))
            return resposta(401, "Credenciais inv\u00e1lidas.", null);
        return resposta(200, "Autentica\u00e7\u00e3o realizada com sucesso.", criarSessao(usuario.get(), UUID.randomUUID()));
    }

    @Override
    public TypedResponse<SessaoDTO> renovarSessao(RenovarSessaoDTO command) {
        if (command == null || tokenInvalido(command.getRefreshToken()))
            return resposta(401, "Refresh token inv\u00e1lido.", null);
        Optional<RefreshToken> encontrado = refreshTokens.buscarPorHash(tokens.calcularHash(command.getRefreshToken()));
        if (!encontrado.isPresent()) return resposta(401, "Refresh token inv\u00e1lido.", null);
        RefreshToken refresh = encontrado.get();
        OffsetDateTime agora = agora();
        if (!refresh.podeCriarSessao(agora)) {
            if (refresh.getUsadoEm() != null) refreshTokens.revogarFamilia(refresh.getFamiliaId(), agora);
            return resposta(401, "Refresh token inv\u00e1lido.", null);
        }
        Optional<Usuario> usuario = usuarios.buscarPorId(refresh.getUsuarioId());
        if (!usuario.isPresent() || !usuario.get().isAtivo())
            return resposta(401, "Refresh token inv\u00e1lido.", null);
        refresh.marcarComoUsado(agora);
        refreshTokens.salvar(refresh);
        return resposta(200, "Sess\u00e3o renovada com sucesso.", criarSessao(usuario.get(), refresh.getFamiliaId()));
    }

    @Override
    public TypedResponse<Void> encerrarSessao(EncerrarSessaoDTO command) {
        if (command == null || command.getUsuarioId() == null || tokenInvalido(command.getRefreshToken()))
            return resposta(401, "Refresh token inv\u00e1lido.", null);
        Optional<RefreshToken> encontrado = refreshTokens.buscarPorHash(tokens.calcularHash(command.getRefreshToken()));
        if (!encontrado.isPresent() || !encontrado.get().getUsuarioId().equals(command.getUsuarioId()))
            return resposta(401, "Refresh token inv\u00e1lido.", null);
        RefreshToken refresh = encontrado.get();
        refresh.revogar(agora());
        refreshTokens.salvar(refresh);
        return resposta(200, "Sess\u00e3o encerrada com sucesso.", null);
    }

    @Override
    public TypedResponse<Void> solicitarRecuperacaoSenha(SolicitarRecuperacaoSenhaDTO command) {
        if (command == null) return resposta(400, "Comando de recupera\u00e7\u00e3o obrigat\u00f3rio.", null);
        String email = normalizarEmail(command.getEmail());
        if (emailInvalido(email)) return resposta(200, mensagemRecuperacao(), null);
        Optional<Usuario> usuario = usuarios.buscarPorEmail(email);
        if (usuario.isPresent()) {
            String token = tokens.gerarTokenAleatorio();
            recuperacoes.salvar(usuario.get().getId(), tokens.calcularHash(token), agora().plusMinutes(MINUTOS_RECUPERACAO_SENHA));
        }
        return resposta(200, mensagemRecuperacao(), null);
    }

    @Override
    public TypedResponse<Void> redefinirSenha(RedefinirSenhaDTO command) {
        if (command == null) return resposta(400, "Comando de redefini\u00e7\u00e3o obrigat\u00f3rio.", null);
        if (senhaInvalida(command.getNovaSenha()))
            return resposta(400, "Dados para redefini\u00e7\u00e3o de senha inv\u00e1lidos.", null);
        if (tokenInvalido(command.getToken()))
            return resposta(401, "Token de recupera\u00e7\u00e3o inv\u00e1lido.", null);
        Optional<UUID> usuarioId = recuperacoes.consumirTokenValido(tokens.calcularHash(command.getToken()), agora());
        if (!usuarioId.isPresent()) return resposta(401, "Token de recupera\u00e7\u00e3o inv\u00e1lido.", null);
        Optional<Usuario> usuario = usuarios.buscarPorId(usuarioId.get());
        if (!usuario.isPresent()) return resposta(401, "Token de recupera\u00e7\u00e3o inv\u00e1lido.", null);
        usuario.get().setSenhaHash(senhas.codificar(command.getNovaSenha()));
        usuarios.salvar(usuario.get());
        refreshTokens.revogarTodosDoUsuario(usuario.get().getId(), agora());
        return resposta(200, "Senha redefinida com sucesso.", null);
    }

    private SessaoDTO criarSessao(Usuario usuario, UUID familia) {
        OffsetDateTime expiraAccessEm = agora().plusMinutes(MINUTOS_ACCESS_TOKEN);
        String refresh = tokens.gerarTokenAleatorio();
        refreshTokens.salvar(new RefreshToken(usuario.getId(), tokens.calcularHash(refresh), familia, agora().plusDays(DIAS_REFRESH_TOKEN)));
        return new SessaoDTO(tokens.gerarAccessToken(usuario, expiraAccessEm), refresh, expiraAccessEm);
    }

    private OffsetDateTime agora() {
        return OffsetDateTime.now(relogio);
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private boolean emailInvalido(String email) {
        return email == null || email.isEmpty() || !email.contains("@");
    }

    private boolean senhaInvalida(String senha) {
        return senha == null || senha.trim().isEmpty();
    }

    private boolean tokenInvalido(String token) {
        return token == null || token.trim().isEmpty();
    }

    private String mensagemRecuperacao() {
        return "Se o e-mail estiver cadastrado, voc\u00ea receber\u00e1 instru\u00e7\u00f5es para redefinir sua senha.";
    }

    private UsuarioDTO usuarioDto(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getEmail(), usuario.getTelefone());
    }

    private <T> TypedResponse<T> resposta(int status, String mensagem, T dados) {
        return new TypedResponse<>(status, mensagem, dados);
    }
}
