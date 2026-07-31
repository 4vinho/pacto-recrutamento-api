# Avaliação e oportunidades de melhoria do backend

## Visão geral

O backend apresenta uma base técnica sólida e compatível com o nível Pleno esperado pelo desafio. O projeto possui boa separação de responsabilidades, aplicação de arquitetura em camadas com portas e adaptadores, autenticação com JWT, persistência com PostgreSQL, migrations com Flyway, armazenamento de arquivos no MinIO, documentação OpenAPI e uma suíte relevante de testes automatizados.

No estado atual, a aplicação compila corretamente e a suíte automatizada executa **93 testes sem falhas ou testes ignorados**. As melhorias descritas abaixo não indicam que a solução esteja inadequada; elas representam oportunidades para aumentar a confiabilidade, a segurança, a observabilidade e a qualidade percebida durante a avaliação técnica.

## 1. Ampliar os testes da camada HTTP

Os testes existentes cobrem regras de negócio, entidades, adapters e restrições arquiteturais. Entretanto, ainda é importante testar o comportamento da aplicação na fronteira HTTP.

Recomenda-se adicionar testes com `MockMvc`, `@WebMvcTest` ou, para fluxos integrados, `@SpringBootTest`. Esses testes devem verificar:

- Rotas e métodos HTTP expostos pelos controllers.
- Serialização e desserialização dos DTOs.
- Status HTTP retornados em cenários de sucesso e erro.
- Validações de campos obrigatórios e payloads inválidos.
- Respostas produzidas pelo tratamento global de exceções.
- Acesso a endpoints protegidos sem autenticação.
- Comportamento com JWT inválido, expirado ou malformado.
- Diferença entre respostas `401 Unauthorized` e `403 Forbidden`.
- Upload multipart sem arquivo, com arquivo vazio ou acima do limite permitido.
- Fluxos principais, como autenticação, cadastro de vaga e candidatura.

Esses testes protegem o contrato utilizado pelo frontend e identificam problemas que testes unitários dos serviços não conseguem detectar, como uma anotação incorreta, uma rota divergente ou um status HTTP inesperado.

## 2. Integrar JaCoCo e SonarQube de forma completa

A integração com o SonarQube deve incluir a geração de cobertura pelo JaCoCo. Apenas executar a análise estática não permite que o Sonar avalie quais regras foram efetivamente exercitadas pelos testes.

Recomenda-se configurar:

- `jacoco-maven-plugin` no `pom.xml`.
- Geração do relatório XML durante o ciclo `verify`.
- Importação do relatório pelo SonarQube.
- Exclusões limitadas a classes estritamente estruturais ou geradas.
- Quality Gate com foco principalmente no código novo.
- Verificação de bugs, vulnerabilidades, code smells, duplicações e cobertura.
- Instruções de execução local no README.

Uma meta de cobertura deve servir como indicador, não como objetivo isolado. É mais valioso cobrir regras de autenticação, autorização, transições de estado e candidatura do que elevar o percentual testando somente getters, setters ou construtores triviais.

## 3. Validar a persistência com PostgreSQL real

Os testes de persistência utilizam H2 com o Flyway desativado. Essa estratégia torna a suíte rápida, mas não garante que todo o comportamento seja igual ao PostgreSQL usado pela aplicação.

Recomenda-se criar ao menos uma suíte de integração com Testcontainers e PostgreSQL para validar:

- Execução completa das migrations do Flyway.
- Constraints, índices e relacionamentos do banco.
- Consultas específicas do Spring Data JPA.
- Regras de unicidade e integridade referencial.
- Inicialização da aplicação em uma base vazia.

Não é necessário substituir todos os testes com H2. Uma pequena suíte de integração com PostgreSQL pode complementar os testes rápidos já existentes.

## 4. Melhorar a confiabilidade dos eventos e notificações

Algumas falhas de publicação de eventos são capturadas para que a operação principal continue. Essa decisão evita que uma candidatura válida seja desfeita por indisponibilidade no mecanismo de notificação, mas a falha pode ficar invisível.

Como melhoria imediata, recomenda-se:

- Registrar a falha com contexto suficiente para diagnóstico.
- Incluir identificadores da candidatura e do evento no log.
- Evitar registrar tokens, senhas, payloads sensíveis ou dados pessoais desnecessários.
- Definir uma estratégia de nova tentativa quando a notificação falhar.

Como evolução arquitetural, pode-se considerar o padrão Outbox, persistindo o evento na mesma transação da operação principal e processando-o posteriormente. Para o escopo do desafio, documentar essa evolução e garantir logs adequados já demonstra maturidade técnica.

## 5. Fortalecer as validações de entrada

As validações de e-mail e senha podem ser mais rigorosas. Verificar apenas a presença de `@` no e-mail ou aceitar qualquer senha não vazia permite entradas de baixa qualidade.

Recomenda-se combinar Bean Validation nos DTOs com regras essenciais na aplicação ou no domínio:

- `@NotBlank` para campos textuais obrigatórios.
- `@Email` para endereços de e-mail.
- `@Size` para limites de senha, título e descrição.
- Limites de tamanho para coleções e valores enviados pelo cliente.
- Normalização consistente de e-mail e outros campos pesquisáveis.
- Mensagens de validação uniformes e claras.

A validação declarativa reduz condicionais repetitivas nos serviços, enquanto as regras mantidas no domínio impedem a criação de entidades inválidas fora da camada HTTP.

## 6. Padronizar o tratamento de erros

O projeto já possui uma estrutura de resposta tipada e um tratamento global para alguns erros HTTP. Essa abordagem pode ser expandida para garantir que toda falha conhecida tenha uma resposta previsível.

Recomenda-se:

- Definir códigos ou identificadores de erro estáveis além da mensagem textual.
- Centralizar exceções de negócio relevantes.
- Tratar erros inesperados com resposta `500` genérica e log interno detalhado.
- Não expor stack traces, nomes de tabelas, consultas ou detalhes de infraestrutura ao cliente.
- Manter o mesmo formato de resposta para erros de autenticação, autorização, validação e negócio.
- Avaliar a adoção de `ProblemDetail` somente em uma futura migração para Spring Boot 3, pois a versão atual utiliza Spring Boot 2.7.

Isso facilita o tratamento no frontend e evita que detalhes técnicos sejam expostos acidentalmente.

## 7. Revisar segurança e configuração por ambiente

A configuração atual já utiliza variáveis de ambiente para credenciais e mantém o arquivo `.env` fora do versionamento. Como reforço, recomenda-se revisar:

- Tamanho mínimo e entropia da chave JWT.
- Tempo de expiração configurável para access e refresh tokens.
- Política de CORS explícita para os ambientes necessários.
- Rate limiting para login e recuperação de senha.
- Proteção contra tentativas repetidas de autenticação.
- Rotação e revogação de credenciais.
- Headers de segurança adequados ao cenário da API.
- Exposição mínima dos endpoints do Actuator.
- Ausência de segredos em logs, imagens Docker e histórico Git.

Os tempos de expiração definidos diretamente no serviço podem ser movidos para propriedades tipadas, permitindo ajustes por ambiente sem recompilar a aplicação.

## 8. Melhorar observabilidade e qualidade dos logs

Os logs devem facilitar o diagnóstico sem expor informações sensíveis. Recomenda-se:

- Padronizar níveis `INFO`, `WARN` e `ERROR`.
- Registrar falhas inesperadas com a exceção original.
- Adicionar um identificador de correlação por requisição.
- Evitar stack traces extensos para falhas esperadas em testes e fluxos controlados.
- Registrar eventos importantes, como autenticação malsucedida, alteração de status e falha de integração.
- Nunca registrar senhas, tokens completos ou conteúdo de currículos.

Também é recomendável revisar o teste de substituição de currículo que gera uma stack trace esperada durante o build. O teste passa, mas o ruído pode dificultar a identificação de erros reais no pipeline.

## 9. Aprimorar a documentação técnica

O README já contém instruções para execução com Docker e Maven. Ele pode ser complementado com:

- Resumo da arquitetura e responsabilidade de cada camada.
- Diagrama simples dos principais componentes.
- Decisões técnicas e respectivos trade-offs.
- Fluxo de autenticação e renovação de sessão.
- Estratégia de armazenamento dos currículos.
- Como executar testes, cobertura, SonarQube e Quality Gate.
- Como acessar Swagger, Actuator, PostgreSQL e MinIO.
- Limitações conhecidas e evoluções futuras.
- Exemplos mínimos de variáveis de ambiente sem valores sensíveis.

Uma documentação objetiva ajuda o avaliador a compreender rapidamente as decisões que não são evidentes apenas pela leitura do código.

## 10. Revisar migrations e histórico de versionamento

A sequência de migrations não contém uma versão `V12`. O Flyway aceita lacunas, portanto isso não impede a execução, mas pode gerar dúvida durante a revisão.

Se a migration nunca foi publicada em nenhum ambiente, pode-se ajustar a numeração. Caso a versão tenha existido ou sido reservada, recomenda-se documentar o motivo e preservar o histórico para não alterar migrations que já possam ter sido aplicadas.

Também é recomendável manter commits pequenos e descritivos, preferencialmente seguindo Conventional Commits. Mensagens claras facilitam a revisão e demonstram organização do processo de desenvolvimento.

## 11. Avaliar atualizações tecnológicas como evolução futura

Spring Boot 2.7 e Java 8 atendem ao escopo solicitado, portanto uma migração não deve ser tratada como requisito para a entrega. Ainda assim, ambos possuem limitações quando comparados às versões atuais.

Como evolução futura, pode-se planejar:

- Migração para uma versão LTS mais recente do Java.
- Atualização para Spring Boot 3.
- Adequação dos imports de `javax` para `jakarta`.
- Atualização das bibliotecas e análise de vulnerabilidades das dependências.

Essa mudança deve ser feita separadamente e com testes de regressão. Realizá-la próximo da entrega aumentaria o risco sem necessariamente agregar valor proporcional ao desafio.

## Ordem de prioridade sugerida

1. Adicionar testes HTTP para os fluxos críticos e para a segurança.
2. Configurar JaCoCo, SonarQube e Quality Gate.
3. Adicionar integração com PostgreSQL por Testcontainers.
4. Melhorar logs e tratamento de falhas em eventos.
5. Fortalecer validações e padronizar erros.
6. Complementar documentação e revisar configurações de segurança.
7. Registrar melhorias tecnológicas maiores como evolução futura.

## Conclusão

O backend demonstra domínio de conceitos importantes para uma posição de nível Pleno: separação de responsabilidades, testes automatizados, autenticação, autorização, persistência relacional, migrations e integração com armazenamento de objetos.

As melhorias de maior impacto não exigem reescrever a arquitetura. O foco deve ser aumentar a confiança no contrato HTTP, comprovar a compatibilidade com PostgreSQL, tornar falhas operacionais observáveis e integrar métricas de qualidade de forma transparente. Com esses ajustes, a solução ficará mais preparada tanto para a avaliação técnica quanto para uma evolução próxima de um ambiente de produção.
