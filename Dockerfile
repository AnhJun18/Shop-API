FROM gradle:6.4.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:11.0.16-jre
COPY --from=build /home/gradle/src/api/build/libs/*.jar /app/app.jar

EXPOSE 8085
ENTRYPOINT ["java","-jar","/app/app.jar"]