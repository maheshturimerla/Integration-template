FROM openjdk:8-jdk
VOLUME /tmp
ADD ccc-cosc-0.0.1-SNAPSHOT.jar app.jar
VOLUME /etc
ADD /etc/ /etc/
ADD startService.sh /startService.sh
RUN chmod 700 /startService.sh
ENTRYPOINT ./startService.sh 
