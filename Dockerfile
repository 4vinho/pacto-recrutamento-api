FROM maven:3.8.8-eclipse-temurin-8 AS dependencies

WORKDIR /workspace

COPY pom.xml .
RUN mvn dependency:go-offline -B

FROM dependencies AS development

COPY src ./src

EXPOSE 8080

CMD ["mvn", "spring-boot:run", "-Dmaven.test.skip=true"]

FROM dependencies AS build

COPY src ./src
RUN mvn clean package -B

FROM eclipse-temurin:8-jre

WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring

COPY --from=build --chown=spring:spring /workspace/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
