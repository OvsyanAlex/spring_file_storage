#FROM gradle:jdk-21-jammy AS build
#COPY --chown=gradle:gradle . /home/gradle/src
#WORKDIR /home/gradle/src
#RUN gradle build --no-daemon
#
#FROM openjdk:21-jdk-slim
#
#ENV port 9200
#
#RUN mldir /app
#COPY --from=
#LABEL authors="User"
#
#ENTRYPOINT ["top", "-b"]