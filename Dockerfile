FROM <nexus-service-name>:<port>/alpine:jre17-20220714
USER 1002
WORKDIR /home/gw
COPY build/libs/*.jar app.jar
ENTRYPOINT exec java ${JAVA_OPTS} -jar /home/gw/app.jar