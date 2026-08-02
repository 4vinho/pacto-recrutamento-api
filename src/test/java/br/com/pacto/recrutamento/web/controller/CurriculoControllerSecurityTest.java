package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.app.dtos.curriculo.UrlTemporariaCurriculoDTO;
import br.com.pacto.recrutamento.app.ports.in.curriculo.CurriculoUseCase;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.web.security.JwtAuthenticationFilter;
import br.com.pacto.recrutamento.web.security.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurriculoController.class)
@Import({SecurityConfiguration.class, JwtAuthenticationFilter.class})
class CurriculoControllerSecurityTest {
    private static final String USUARIO_ID = "11111111-1111-1111-1111-111111111111";
    private static final String CANDIDATURA_ID = "22222222-2222-2222-2222-222222222222";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CurriculoUseCase service;

    @Test
    void gerarUrlPermiteAdministradorResponsavelECandidato() throws Exception {
        when(service.gerarUrlTemporariaCurriculo(any())).thenReturn(new TypedResponse<>(
                200, "ok", new UrlTemporariaCurriculoDTO("https://storage/curriculo.pdf", OffsetDateTime.now())));

        devePermitirGerarUrl("ADMINISTRADOR");
        devePermitirGerarUrl("RESPONSAVEL_VAGA");
        devePermitirGerarUrl("CANDIDATO");
    }

    @Test
    void uploadContinuaRestritoParaCandidato() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "curriculo.pdf", "application/pdf", "%PDF-1.4".getBytes());

        mvc.perform(multipart("/candidaturas/{id}/curriculo", CANDIDATURA_ID)
                        .file(arquivo)
                        .with(user(USUARIO_ID).roles("ADMINISTRADOR")))
                .andExpect(status().isForbidden());
    }

    private void devePermitirGerarUrl(String papel) throws Exception {
        mvc.perform(get("/candidaturas/{id}/curriculo/url", UUID.fromString(CANDIDATURA_ID))
                        .with(user(USUARIO_ID).roles(papel)))
                .andExpect(status().isOk());
    }
}
