cd "./configserver"
call gradlew.bat buildDockerImage

cd "../eurekaserver"
call gradlew.bat buildDockerImage

cd "../licensing-service"
call gradlew.bat buildDockerImage

cd "../organization-service"
call gradlew.bat buildDockerImage

cd "../gatewayserver"
call gradlew.bat buildDockerImage

cd "../docker"

call docker-compose stop
call docker-compose rm -f
call docker-compose up --build --no-start