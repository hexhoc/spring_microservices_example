#Start with a base image containing Java runtime
FROM openjdk:17
# Add Maintainer Info
LABEL maintainer="Vladislav Zhuravsky <hexhoc@gmail.com>"
# The application's jar file
ARG JAR_FILE=build/libs/*.jar
# Add the application's jar to the container
COPY ${JAR_FILE} app.jar
#execute the application after container is run
ENTRYPOINT ["java","-jar","/app.jar"]