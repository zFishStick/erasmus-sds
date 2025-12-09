
FROM eclipse-temurin:23-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:23-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
