FROM gradle:8.11.1-jdk21-jammy AS builder

WORKDIR /app
COPY ./build.gradle ./settings.gradle /app/
RUN gradle dependencies --no-daemon || true
COPY . .
RUN gradle clean build

FROM gradle:8.11.1-jdk21-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]