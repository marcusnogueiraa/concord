# Use a versão oficial do OpenJDK
FROM openjdk:17-jdk-slim

# Adicione o arquivo JAR gerado ao container
COPY build/libs/concordapi-0.0.1-SNAPSHOT.jar /app/concordapi-0.0.1-SNAPSHOT.jar

# Defina o comando para executar a aplicação
CMD ["java", "-jar", "/app/seu-arquivo.jar"]