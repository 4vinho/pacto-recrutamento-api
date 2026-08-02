package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.web.config.OpenApiConfiguration;
import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import br.com.pacto.recrutamento.web.support.HttpResponses;

import br.com.pacto.recrutamento.app.dtos.curriculo.*;
import br.com.pacto.recrutamento.app.ports.in.curriculo.CurriculoUseCase;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.BusinessException;
import br.com.pacto.recrutamento.core.common.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.ARQUIVO_MUITO_GRANDE;
import static br.com.pacto.recrutamento.core.common.ErrorMessages.ARQUIVO_OBRIGATORIO;

@RestController
@RequestMapping("/candidaturas/{candidaturaId}/curriculo")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class CurriculoController {
    private static final long TAMANHO_MAXIMO = 5L * 1024L * 1024L;
    private final CurriculoUseCase service;

    public CurriculoController(CurriculoUseCase service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<TypedResponse<CurriculoDTO>> enviar(
            Authentication authentication, @PathVariable UUID candidaturaId,
            @RequestPart("arquivo") MultipartFile arquivo,
            @RequestParam(defaultValue = "false") boolean substituir) throws IOException {
        validarArquivo(arquivo);
        UUID usuarioId = AuthenticatedUser.id(authentication);
        if (substituir) {
            return HttpResponses.from(service.substituirCurriculo(new SubstituirCurriculoDTO(
                    usuarioId, candidaturaId, arquivo.getOriginalFilename(), arquivo.getContentType(), arquivo.getBytes())));
        }
        return HttpResponses.from(service.enviarCurriculo(new EnviarCurriculoDTO(
                usuarioId, candidaturaId, arquivo.getOriginalFilename(), arquivo.getContentType(), arquivo.getBytes())));
    }

    @GetMapping("/url")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA', 'CANDIDATO')")
    public ResponseEntity<TypedResponse<UrlTemporariaCurriculoDTO>> gerarUrl(
            Authentication authentication, @PathVariable UUID candidaturaId) {
        return HttpResponses.from(service.gerarUrlTemporariaCurriculo(
                new GerarUrlTemporariaCurriculoDTO(AuthenticatedUser.id(authentication), candidaturaId)));
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException(422, ErrorCode.VALIDATION_ERROR, ARQUIVO_OBRIGATORIO);
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new BusinessException(422, ErrorCode.VALIDATION_ERROR, ARQUIVO_MUITO_GRANDE);
        }
    }
}
