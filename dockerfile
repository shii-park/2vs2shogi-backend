# --- 1. ビルドステージ (Builder) ---
FROM gradle:8-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

COPY src ./src

RUN gradle bootJar --no-daemon -x test

# --- 2. 実行ステージ (Runner) ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app


COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]