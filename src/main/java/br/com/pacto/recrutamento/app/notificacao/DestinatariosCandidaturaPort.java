package br.com.pacto.recrutamento.app.notificacao;
import java.util.Optional;
import java.util.UUID;
public interface DestinatariosCandidaturaPort { Optional<DestinatariosCandidatura> buscarPorCandidatura(UUID candidaturaId); }
