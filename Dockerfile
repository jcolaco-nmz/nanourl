FROM adoptopenjdk/openjdk11:debian-slim as BUILD_IMAGE

ENV GRADLE_OPTS "-Dorg.gradle.daemon=false"
ENV APP_HOME /app
RUN mkdir $APP_HOME
WORKDIR $APP_HOME

ADD build.gradle.kts $APP_HOME
ADD settings.gradle.kts $APP_HOME
ADD gradlew $APP_HOME
ADD gradle $APP_HOME/gradle
RUN $APP_HOME/gradlew downloadDependenciesForDocker

# Build
ADD src $APP_HOME/src
RUN $APP_HOME/gradlew clean build -x test
