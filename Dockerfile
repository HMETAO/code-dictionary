FROM openjdk:11
COPY target/*.jar hmetao.jar
EXPOSE 8972
ENTRYPOINT ["java","-jar","hmetao.jar"]