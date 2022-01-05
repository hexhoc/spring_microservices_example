cd "./configserver" || exit
sh gradlew buildDockerImage

cd "../eurekaserver" || exit
sh gradlew buildDockerImage

cd "../licensing-service" || exit
sh gradlew buildDockerImage

cd "../organization-service" || exit
sh gradlew buildDockerImage

cd "../gatewayserver" || exit
sh gradlew buildDockerImage

cd "../docker" || exit

docker-compose stop
docker-compose rm -f
docker-compose up --build -d