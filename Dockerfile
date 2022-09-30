FROM registry.cn-hangzhou.aliyuncs.com/canary2/maoniu-java:11-jdk-alpine
COPY target/*.jar hmetao.jar
EXPOSE 8972
ENTRYPOINT ["java","-jar","hmetao.jar"]