FROM eclipse-temurin:21-jre

MAINTAINER jeecgos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /jeecg-boot

WORKDIR /jeecg-boot

EXPOSE 8080

ADD ./target/jeecg-boot-module-system-2.4.6.jar ./

CMD sleep 60;java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED -Djava.security.egd=file:/dev/./urandom -jar jeecg-boot-module-system-2.4.6.jar
