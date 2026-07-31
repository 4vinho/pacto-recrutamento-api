package br.com.pacto.recrutamento.app.ports.in.usuario;

import br.com.pacto.recrutamento.app.dtos.usuario.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;

public interface UsuarioUseCase {
    TypedResponse<UsuarioDTO> cadastrarUsuario(CadastrarUsuarioDTO command);

    TypedResponse<SessaoDTO> autenticarUsuario(AutenticarUsuarioDTO command);

    TypedResponse<SessaoDTO> renovarSessao(RenovarSessaoDTO command);

    TypedResponse<Void> encerrarSessao(EncerrarSessaoDTO command);

    TypedResponse<Void> solicitarRecuperacaoSenha(SolicitarRecuperacaoSenhaDTO command);

    TypedResponse<Void> redefinirSenha(RedefinirSenhaDTO command);
}
