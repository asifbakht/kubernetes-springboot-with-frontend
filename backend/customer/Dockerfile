FROM openjdk:23-jdk-oraclelinux7
LABEL Asif Bakht
COPY target/customer-0.0.1-SNAPSHOT.jar customer.jar
EXPOSE $CONTAINER_PORT
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","customer.jar"]

