#FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
#WORKDIR /app

# Copia pom y dependencias primero (para aprovechar cache)
#COPY pom.xml .
#RUN mvn dependency:go-offline

# Copia todo el código
#COPY . .

# Construye el jar
#RUN mvn clean package -DskipTests

# Imagen final con solo el jar
#FROM eclipse-temurin:21-jdk-alpine
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar

#ENTRYPOINT ["java", "-jar", "app.jar"]
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia pom y dependencias primero (para aprovechar cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia todo el código
COPY . .

# Construye el jar
RUN mvn clean package -DskipTests

# Imagen final con solo el jar
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
