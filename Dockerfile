FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ./target/rule-engine-0.0.1-SNAPSHOT.jar TxT-rule-engine.jar
ENTRYPOINT ["java", "-jar", "/TxT-rule-engine.jar"]
