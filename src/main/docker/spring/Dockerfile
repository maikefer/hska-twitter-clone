#FROM frolvlad/alpine-oraclejdk8:slim
FROM openjdk:8
VOLUME /tmp
ADD twitter-clone-0.0.1.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

