# Integração do projeto com o SonarQube

## Como o SonarQube acessa o código

O SonarQube não lê diretamente uma URL como `github.com/usuario/meuprojeto`.

Quem lê e analisa o código é o **SonarScanner**. O scanner é executado sobre uma cópia do projeto disponível na máquina local ou em um pipeline de integração contínua. Ao final, ele envia o resultado da análise para o servidor SonarQube.

O fluxo é o seguinte:

```text
Repositório local ou checkout do GitHub
                ↓
          SonarScanner
                ↓
       Servidor SonarQube
                ↓
  Relatório e Quality Gate
```

## Análise local recomendada

Para este desafio técnico, o caminho mais simples é executar o scanner localmente com Maven.

### 1. Acessar o SonarQube

Com o servidor em execução, acesse normalmente:

```text
http://localhost:9000
```

### 2. Criar o projeto

No painel do SonarQube, acesse:

```text
Projects → Create project → Manually
```

Configuração sugerida:

```text
Project key: pacto-recrutamento-api
Display name: Pacto Recrutamento API
Main branch: main
```

O `project key` identifica o projeto dentro do SonarQube e deve ser único nessa instalação.

### 3. Gerar um token

No SonarQube, acesse:

```text
My Account → Security → Generate Token
```

Copie o token no momento da criação. Ele será utilizado pelo scanner para autenticar o envio da análise.

O token não deve ser armazenado no `pom.xml`, no `.env.example`, no código-fonte ou no histórico Git.

### 4. Executar a análise

Abra o PowerShell no diretório que contém o `pom.xml` do backend e defina as variáveis somente na sessão atual:

```powershell
$env:SONAR_TOKEN="seu-token"
$env:SONAR_HOST_URL="http://localhost:9000"

mvn clean verify sonar:sonar
```

O comando executa os testes, compila o projeto, gera o relatório de cobertura do
JaCoCo, analisa o código disponível localmente e envia o resultado para o
SonarQube. A chave e o nome do projeto já estão configurados no `pom.xml`.

Ao finalizar, o terminal deverá exibir uma mensagem de sucesso e o endereço do relatório. Também será possível abrir o projeto diretamente no painel do SonarQube.

### 5. Remover o token da sessão

Após a análise, o token pode ser removido da sessão atual do PowerShell:

```powershell
Remove-Item Env:SONAR_TOKEN
```

## Relação com o GitHub

Se o repositório foi clonado do GitHub, a análise local já avalia o conteúdo desse projeto. O SonarScanner não precisa baixar novamente o repositório, pois analisa o checkout disponível na máquina.

Entretanto, esse processo local não executa uma nova análise automaticamente a cada `push`. Para isso, é necessário integrar o SonarQube a um pipeline, como o GitHub Actions.

## Integração opcional com GitHub Actions

Uma integração automatizada pode executar o scanner a cada alteração na branch principal ou em pull requests.

Existem duas abordagens comuns:

- Utilizar o SonarQube Cloud, que possui integração direta com repositórios do GitHub.
- Publicar uma instalação própria do SonarQube em um endereço acessível pelo runner do GitHub Actions.

### Limitação do `localhost`

Um runner hospedado pelo GitHub não consegue acessar o endereço abaixo na máquina do desenvolvedor:

```text
http://localhost:9000
```

Para o runner, `localhost` representa a própria máquina temporária do GitHub Actions, não o computador em que o SonarQube foi iniciado.

Para utilizar um servidor próprio com GitHub Actions, é necessário:

- Disponibilizar o SonarQube em uma URL acessível pelo runner; ou
- Usar um runner próprio na mesma rede do servidor SonarQube.

### Segredos necessários no GitHub

Em um fluxo automatizado, valores sensíveis devem ser configurados em:

```text
Repository → Settings → Secrets and variables → Actions
```

Exemplos:

```text
SONAR_TOKEN
SONAR_HOST_URL
```

O workflow deve consumir esses valores por meio do contexto `secrets`, sem escrevê-los diretamente no arquivo YAML.

### Importação do repositório pelo SonarQube

Depois de configurar a integração do SonarQube Server com uma GitHub App, o repositório pode ser importado pelo painel:

```text
Projects → Create Project → Import from DevOps platforms → GitHub
```

Vincular o projeto ao GitHub permite associar o repositório ao projeto do SonarQube e publicar o resultado do Quality Gate em pull requests, conforme os recursos disponíveis na edição utilizada.

## Cobertura de testes com JaCoCo

Executar o scanner não produz automaticamente a cobertura dos testes. Para o SonarQube exibir cobertura, o projeto deve gerar previamente um relatório compatível, normalmente com o JaCoCo.

O fluxo esperado é:

```text
Testes Maven
     ↓
Relatório XML do JaCoCo
     ↓
SonarScanner
     ↓
Cobertura exibida no SonarQube
```

O `jacoco-maven-plugin` está configurado no `pom.xml` para gerar o relatório XML
durante o ciclo `verify`. O relatório é criado em
`target/site/jacoco/jacoco.xml` e importado pelo scanner durante a análise.

É recomendável medir principalmente a cobertura das regras de negócio, autenticação, autorização e transições de estado. O objetivo não deve ser aumentar artificialmente o percentual por meio de testes triviais ou exclusões excessivas.

## Diagnóstico de problemas comuns

### O projeto não aparece no SonarQube

Verifique:

- Se `SONAR_HOST_URL` aponta para o servidor correto.
- Se o `project key` informado no comando corresponde ao projeto criado.
- Se o comando foi executado no diretório que contém o `pom.xml`.
- Se a análise terminou com sucesso no terminal.

### Erro de autenticação

Verifique:

- Se o token ainda é válido.
- Se a variável `SONAR_TOKEN` foi definida na mesma sessão do PowerShell.
- Se o usuário que gerou o token possui permissão para analisar o projeto.

### Erro de conexão

Verifique:

- Se o container ou serviço do SonarQube está em execução.
- Se `http://localhost:9000` abre no navegador.
- Se a porta configurada corresponde à porta publicada pelo container.
- Se firewall, proxy ou certificado HTTPS estão bloqueando a conexão.

### A cobertura aparece como zero

Verifique:

- Se o JaCoCo está configurado no `pom.xml`.
- Se o relatório XML foi criado antes da análise.
- Se o comando executou a fase `verify` antes do objetivo `sonar`.
- Se o caminho do relatório está disponível para o scanner.

## Referências oficiais

- [SonarScanner para Maven](https://docs.sonarsource.com/sonarqube-server/analyzing-source-code/scanners/sonarscanner-for-maven)
- [Importação de repositórios do GitHub](https://docs.sonarsource.com/sonarqube-server/devops-platform-integration/github-integration/importing-github-repositories)

## Recomendação para este projeto

Para a entrega do desafio, recomenda-se iniciar pela análise local com Maven. Esse fluxo é suficiente para avaliar o código, identificar vulnerabilidades e code smells e demonstrar a utilização do SonarQube.

Como etapa seguinte, deve-se configurar o JaCoCo para apresentar a cobertura dos testes existentes. A integração automática com GitHub Actions pode ser documentada como evolução futura caso o servidor SonarQube permaneça disponível apenas localmente.
