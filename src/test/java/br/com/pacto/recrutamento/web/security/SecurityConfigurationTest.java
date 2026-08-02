package br.com.pacto.recrutamento.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfigurationTest.ContractController.class)
@Import({
        SecurityConfiguration.class,
        JwtAuthenticationFilter.class,
        SecurityConfigurationTest.ContractController.class
})
class SecurityConfigurationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void deveRestringirContratosPorPapel() throws Exception {
        devePermitir(HttpMethod.GET, "/templates-vaga", "ADMINISTRADOR");
        deveNegar(HttpMethod.GET, "/templates-vaga", "RESPONSAVEL_VAGA");
        devePermitir(HttpMethod.POST, "/vagas/1", "RESPONSAVEL_VAGA");
        deveNegar(HttpMethod.POST, "/vagas/1", "CANDIDATO");
        devePermitir(HttpMethod.POST, "/vagas/1/candidaturas", "CANDIDATO");
        deveNegar(HttpMethod.POST, "/vagas/1/candidaturas", "RESPONSAVEL_VAGA");
        devePermitir(HttpMethod.GET, "/vagas/1/candidaturas", "RESPONSAVEL_VAGA");
        deveNegar(HttpMethod.GET, "/vagas/1/candidaturas", "CANDIDATO");
        devePermitir(HttpMethod.POST, "/candidaturas/1/cancelamento", "CANDIDATO");
        deveNegar(HttpMethod.POST, "/candidaturas/1/cancelamento", "ADMINISTRADOR");
        devePermitir(HttpMethod.PATCH, "/candidaturas/1/status", "ADMINISTRADOR");
        deveNegar(HttpMethod.PATCH, "/candidaturas/1/status", "CANDIDATO");
    }

    @Test
    void deveNegarOperacaoNovaSemContratoExplicito() throws Exception {
        deveNegar(HttpMethod.POST, "/operacao-futura", "ADMINISTRADOR");
    }

    private void devePermitir(HttpMethod metodo, String rota, String papel) throws Exception {
        mvc.perform(request(metodo, rota).with(user("usuario").roles(papel)))
                .andExpect(status().isOk());
    }

    private void deveNegar(HttpMethod metodo, String rota, String papel) throws Exception {
        mvc.perform(request(metodo, rota).with(user("usuario").roles(papel)))
                .andExpect(status().isForbidden());
    }

    @RestController
    public static class ContractController {
        @RequestMapping({
                "/templates-vaga", "/vagas/{id}", "/vagas/{id}/candidaturas",
                "/candidaturas/{id}/cancelamento", "/candidaturas/{id}/status",
                "/operacao-futura"
        })
        public void endpoint() {
        }
    }
}
