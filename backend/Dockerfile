# ── Stage 1: Build ──────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests

# ── Stage 2: Runtime ─────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create upload/report directories
RUN mkdir -p /app/uploads/resumes /app/uploads/profiles /app/reports

# Copy the built jar
COPY --from=builder /app/target/resumeiq-backend-*.jar app.jar

# Non-root user for security
RUN addgroup -S resumeiq && adduser -S resumeiq -G resumeiq
RUN chown -R resumeiq:resumeiq /app
USER resumeiq

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/v1/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
