#Start with a base image containing Java runtime
FROM openjdk:17
# Add Maintainer Info
LABEL maintainer="Vladislav Zhuravsky <hexhoc@gmail.com>"
# The application's jar file
ARG JAR_FILE=build/libs/organization-service-0.0.1-SNAPSHOT.jar
# Add the application's jar to the container
COPY ${JAR_FILE} app.jar
# Startup script that is waiting other service befor start itself
COPY docker-startup-script.sh docker-startup-script.sh

WORKDIR /
#execute the application after container is run
ENTRYPOINT ["sh", \
"/docker-startup-script.sh", \
"http://configserver:8071/actuator/health", \
"5", \
"java -jar /app.jar"]