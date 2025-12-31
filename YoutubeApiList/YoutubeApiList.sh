#!/bin/bash
jarFiles=$(find target/dependency/ -name *.jar)

#for jar in ${jarFiles[@]}
#do
#	echo $jar	
#done

classpath=$(echo ${jarFiles[@]} | sed 's/ /;/g')";/target/YoutubeApiList-0.0.1-SNAPSHOT.jar"
echo $classpath

java -cp "$classpath" YoutubeApiList.YoutubeApiList

