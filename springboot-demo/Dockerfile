FROM openjdk:11-jre-slim
LABEL maintainer="demo@example.com"

WORKDIR /app

# Copy the built jar
COPY springboot-demo-start/target/springboot-demo-start-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]