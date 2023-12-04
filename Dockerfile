# open jdk 11 버전의 환경을 구성
FROM openjdk:11

COPY build/libs/VideoChatting-0.0.1-SNAPSHOT.jar app.jar


#ENTRYPOINT ["java", "-jar", "app.jar"]
# 시스템 진입점 정의
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-jar", "/app.jar"]