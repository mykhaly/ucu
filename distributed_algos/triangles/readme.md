command to build: mvn package 
comman to run: path/to/spark-submit --class Main --master local path/to/builded.jar path/to/followers.txt
command to build docker image: docker build -t your_name_here path/to/triangles
command to run docker image: docker run your_name_here
or simply: docker run mykhalchuky/scala_spark_triangles
