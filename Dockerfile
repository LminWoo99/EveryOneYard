# open jdk 11 버전의 환경을 구성
FROM openjdk:11

COPY build/libs/VideoChatting-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]