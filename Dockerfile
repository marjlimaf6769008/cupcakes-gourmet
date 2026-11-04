# Usa uma imagem oficial do Java 17 (ou ajusta para a versão 21 se o teu projeto usar Java 21)
FROM eclipse-temurin:17-jdk-jammy

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia todos os arquivos do projeto para dentro do container
COPY . .

# Dá permissão de execução ao Maven Wrapper e compila o projeto sem rodar os testes
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Expõe a porta padrão que o Spring Boot usa
EXPOSE 8080

# Comando para iniciar a aplicação gerada na pasta target
CMD ["sh", "-c", "java -jar target/*.jar"]