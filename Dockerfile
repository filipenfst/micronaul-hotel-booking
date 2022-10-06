FROM ghcr.io/graalvm/jdk:java17-21.3.0

VOLUME /tmp
RUN mkdir -p /opt/app/
WORKDIR /opt/app/

COPY ./build/libs/*.jar app.jar

CMD java -jar -Xmx4096M -Xms1024M -XX:MaxMetaspaceSize=256M app.jar
