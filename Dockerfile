FROM openjdk:21
EXPOSE 8080
MAINTAINER sam
COPY target/temp.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]