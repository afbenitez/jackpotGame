FROM openjdk:17-jdk-slim

# Install Maven and curl
RUN apt-get update && apt-get install -y maven curl && apt-get clean

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src src
RUN mvn clean package -DskipTests

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/jackpot-game-0.0.1-SNAPSHOT.jar"]
