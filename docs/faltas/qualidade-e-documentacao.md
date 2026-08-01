# Qualidade e documentação

## Testes que faltam

- Corrigir a suíte atual: `mvn test` executou 89 testes na auditoria, mas terminou
  com uma falha e um erro em `MapeamentoJpaTest`. O metamodelo JPA encontrou
  apenas `RequisitoTemplateVaga`, e o teste de persistência não conseguiu
  carregar o contexto. Enquanto isso não for corrigido, o backend não possui
  baseline verde de entrega.
- Testes da fronteira HTTP com MockMvc/WebMvcTest para rotas, validação,
  serialização, `401`, `403` e status de erro.
- Teste integrado com PostgreSQL real/Testcontainers e Flyway habilitado.
- Testes ponta a ponta dos fluxos login → vaga → candidatura → avaliação.
- Gate mínimo de cobertura automatizado no build; o relatório JaCoCo existe,
  mas não há regra de reprovação por cobertura.

## Documentação que falta ou precisa ser corrigida

- Diagrama/resumo da arquitetura e decisões técnicas no README principal.
- Instruções completas para cobertura, SonarQube e limitações conhecidas.
- Exemplos do contrato/fluxos principais além da interface Swagger.
- Explicação da lacuna de numeração Flyway `V12`.
- Atualização de `docs/MELHORIAS_BACKEND.md`: o documento ainda recomenda
  configurar JaCoCo, embora o `pom.xml` já possua essa configuração.
- Atualização da afirmação de “93 testes sem falhas” no mesmo documento, pois a
  execução auditada encontrou 89 testes, uma falha e um erro.

## Robustez ainda pendente

- Ampliar Bean Validation e limites declarativos nos DTOs.
- Padronizar também falhas inesperadas no tratamento global, sem expor detalhes.
- Melhorar logs de falhas de eventos sem registrar dados sensíveis.
