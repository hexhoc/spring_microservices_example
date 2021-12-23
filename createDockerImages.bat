cd "./Configuration server"
call gradlew.bat buildDockerImage
cd ..
cd "./Eureka Server"
call gradlew.bat buildDockerImage
cd ..
set root="./License Service"
cd %root%
call gradlew.bat buildDockerImage
cd ..
set root="./Organization Service"
cd %root%
call gradlew.bat buildDockerImage