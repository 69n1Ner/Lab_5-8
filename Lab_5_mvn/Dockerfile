# ─────────────────────────────────────────────────────────
# Stage 1: Сборка проекта (Maven + JDK)
# ─────────────────────────────────────────────────────────
# ✅ Правильный тег: без -jammy в конце!
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Кэшируем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────────────────────
# Stage 2: Минимальный рантайм (только JRE)
# ─────────────────────────────────────────────────────────
# ✅ Правильный тег: с -jammy в конце!
FROM eclipse-temurin:17-jre-jammy

# Создаём непривилегированного пользователя
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

WORKDIR /app

# Копируем собранный JAR из builder-стадии
COPY --from=builder --chown=appuser:appgroup /build/target/lab5_8-server.jar app.jar

# Настройка окружения
ENV SERVER_PORT=9898 \
    SERVER_HOST=0.0.0.0 \
    DATA_DIR=/app/data

# Создаём папку для данных
RUN mkdir -p ${DATA_DIR} && chown appuser:appgroup ${DATA_DIR}

EXPOSE 9898/udp

# Запуск
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar \"$@\"", "--"]