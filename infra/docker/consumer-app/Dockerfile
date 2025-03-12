FROM eclipse-temurin:21-jdk AS dev

WORKDIR /app

COPY /consumer-app /app

RUN chmod +x gradlew && ./gradlew clean getDependencies -x test --stacktrace

CMD ["sh", "start.sh"]

FROM gradle:8.10.2-jdk21 AS build

WORKDIR /app

COPY /consumer-app/build.gradle.kts /app/
COPY /consumer-app/settings.gradle.kts /app/
COPY /consumer-app/gradle /app/gradle

RUN gradle clean build -x test --parallel --no-daemon || return 0

COPY /consumer-app/src /app/src
RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:21-jdk AS prod

RUN useradd -m -u 1001 spring

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

RUN chown -R spring:spring /app

USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
