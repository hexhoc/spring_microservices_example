#Start with a base image containing Java runtime
FROM openjdk:17
# Add Maintainer Info
LABEL maintainer="Vladislav Zhuravsky <hexhoc@gmail.com>"
# The application's jar file
ARG JAR_FILE=build/libs/licensing-service-0.0.1-SNAPSHOT.jar
# Add the application's jar to the container
COPY ${JAR_FILE} app.jar
WORKDIR /
#execute the application after container is run
ENTRYPOINT java --add-opens java.base/java.nio.charset=ALL-UNNAMED -jar /app.jar