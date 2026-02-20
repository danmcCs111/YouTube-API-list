#!/bin/bash
typeOs=`uname`
jarFiles=$(find target/dependency/ -name *.jar)

if [[ "$typeOs" == "Linux" ]]
then
	classpath=$(echo ${jarFiles[@]} | sed 's/ /:/g')":/target/YoutubeApiList-0.0.1-SNAPSHOT.jar"
else

	classpath=$(echo ${jarFiles[@]} | sed 's/ /;/g')";/target/YoutubeApiList-0.0.1-SNAPSHOT.jar"
fi

echo $classpath

java -cp "$classpath" YoutubeApiList.YoutubeApiList

