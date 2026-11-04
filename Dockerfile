# Estágio de Build: Utiliza o JDK 27 para compilar o projeto com Maven
FROM eclipse-temurin:27-jdk-alpine AS build

WORKDIR /build

# Instala o Maven manualmente no container para garantir compatibilidade com a versão 27
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e compila pulando os testes
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio de Execução: Roda a aplicação com o JRE 27
FROM eclipse-temurin:27-jre-alpine

WORKDIR /app

# Copia o jar gerado no estágio anterior
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]