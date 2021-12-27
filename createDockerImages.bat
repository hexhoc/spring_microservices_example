cd "./Configuration server"
call gradlew.bat buildDockerImage

cd "../Eureka Server"
call gradlew.bat buildDockerImage

cd "../License Service"
call gradlew.bat buildDockerImage

cd "../Organization Service"
call gradlew.bat buildDockerImage

cd "../API Gateway server"
call gradlew.bat buildDockerImage

cd "../docker"
call docker-compose rm -f
call docker-compose up --build -d