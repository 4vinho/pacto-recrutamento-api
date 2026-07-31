# SonarQube

## Executar a análise

Com o SonarQube disponível em `http://localhost:9002`, abra o PowerShell na
pasta do backend e execute:

```powershell
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar `
  "-Dsonar.projectKey=pacto-recrutamento-api" `
  "-Dsonar.projectName=Pacto Recrutamento API" `
  "-Dsonar.host.url=http://localhost:9002" `
  "-Dsonar.token=SEU_TOKEN"
```

Também é possível executar tudo em uma linha:

```powershell
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar "-Dsonar.projectKey=pacto-recrutamento-api" "-Dsonar.projectName=Pacto Recrutamento API" "-Dsonar.host.url=http://localhost:9002" "-Dsonar.token=SEU_TOKEN"
```

No PowerShell, a quebra de linha é feita com crase (`` ` ``), e não com a barra
invertida (`\`) utilizada em terminais Linux.

## Resultado

O comando executa os testes, gera a cobertura com JaCoCo e envia a análise para
o SonarQube. O resultado pode ser acessado em:

```text
http://localhost:9002/dashboard?id=pacto-recrutamento-api
```

O relatório XML de cobertura também fica disponível em:

```text
target/site/jacoco/jacoco.xml
```

## Token

O token é gerado em `My Account → Security` no SonarQube. Substitua
`SEU_TOKEN` no comando e não salve o valor real no código ou no Git.
