# Estágio de Build: Utiliza a imagem oficial do Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

# Copia o pom.xml e baixa as dependências (aproveita o cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e realiza o empacotamento pulando os testes
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio de Execução: Imagem leve JRE para rodar o arquivo compilado
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copia o .jar gerado no estágio de build
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]