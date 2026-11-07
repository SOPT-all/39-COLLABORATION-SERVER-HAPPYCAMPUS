FROM eclipse-temurin:21-jre 
# JAVA 21 실행 환경 사용

WORKDIR /app
# 컨테이너 내부 작업 경로

COPY app.jar app.jar
# 이미지 빌드 경로의 app.jar를 컨테이너에 포함

EXPOSE 8080
# 앱이 사용할 포트표시, 실제포트 연결은 다음 Compose 설정에서 

ENTRYPOINT [ "java", "-jar", "app.jar" ]
# 컨테이너 시작 시 앱 실행