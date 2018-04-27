FROM openjdk:8

RUN apt-get update
RUN apt-get install git 
RUN apt-get install scala -y
RUN wget -O spark-2.2.1-bin-hadoop2.7.tgz http://apache.volia.net/spark/spark-2.2.1/spark-2.2.1-bin-hadoop2.7.tgz
RUN tar -xzf spark-2.2.1-bin-hadoop2.7.tgz
RUN rm spark-2.2.1-bin-hadoop2.7.tgz
RUN mv /spark-2.2.1-bin-hadoop2.7/conf/log4j.properties.template /spark-2.2.1-bin-hadoop2.7/conf/log4j.properties
RUN sed -i s/INFO/ERROR/g /spark-2.2.1-bin-hadoop2.7/conf/log4j.properties
RUN mkdir ~/my_folder

USER root
WORKDIR ~

COPY target/triangles-1.0-SNAPSHOT-jar-with-dependencies.jar my_folder/triangles.jar
COPY src/main/resources/followers.txt my_folder/followers.txt

EXPOSE 4040 8080 8081

CMD /spark-2.2.1-bin-hadoop2.7/bin/spark-submit --class Main --master local my_folder/triangles.jar my_folder/followers.txt
