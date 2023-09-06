#FROM maven:3.9.3 AS build
#COPY . .
#RUN mvn clean package -DskipTests

FROM openjdk:17-alpine
COPY ./target/*.jar blog-api.jar
ENTRYPOINT ["java","-jar","blog-api.jar"]
