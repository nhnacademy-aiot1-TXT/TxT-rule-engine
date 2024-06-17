# 공식 OpenJDK 이미지 사용
FROM openjdk:11-jre-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일을 컨테이너로 복사
COPY target/rule-engine-0.0.1-SNAPSHOT.jar /app/
COPY startup.sh /app/

# 사용하는 포트 노출
EXPOSE 9897

# startup.sh 스크립트를 실행 가능하게 설정
RUN chmod +x /app/startup.sh

# startup.sh 스크립트 실행
ENTRYPOINT ["/app/startup.sh"]
