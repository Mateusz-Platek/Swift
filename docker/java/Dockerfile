FROM eclipse-temurin:21 as buildstage
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src
ARG file="Interns_2025_SWIFT_CODES - Sheet1.csv"
COPY ${file} ${file}
RUN ./mvnw package
RUN cp target/*.jar app.jar

FROM eclipse-temurin:21
ENV DB_HOST="postgresql"
ARG file="Interns_2025_SWIFT_CODES - Sheet1.csv"
COPY ${file} ${file}
COPY --from=buildstage app.jar .
ENTRYPOINT ["java", "-jar", "app.jar"]
