FROM eclipse-temurin:25-jdk AS local

WORKDIR /app

COPY /consumer-app /app

ENV TZ=Asia/Tokyo

RUN chmod +x gradlew && ./gradlew clean getDependencies -x test --stacktrace
