FROM gradle:8.10.2-jdk21 AS build

WORKDIR /app

COPY /backend/build.gradle.kts /app/
COPY /backend/settings.gradle.kts /app/
COPY /backend/gradle /app/gradle

RUN gradle clean build -x test --parallel --no-daemon || return 0

COPY /backend/src /app/src
RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:21-jdk AS dev

WORKDIR /app

COPY /backend /app

EXPOSE 8080

RUN chmod +x gradlew
RUN chmod +x start.sh

# -x testでビルド中のテストをskip
RUN ./gradlew clean getDependencies -x test --stacktrace

CMD ["sh", "start.sh"]

FROM eclipse-temurin:21-jdk AS prod

RUN useradd -m -u 1001 spring

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

RUN chown -R spring:spring /app

USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
