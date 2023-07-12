FROM openjdk:11
COPY target/*.jar hmetao.jar

RUN yum update -y && \
    yum install -y gcc
EXPOSE 8972
ENTRYPOINT ["java","-jar","hmetao.jar"]