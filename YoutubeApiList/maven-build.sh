#!/bin/bash
orgDir=`pwd`
cd "$(dirname "$0")"
mvn clean install dependency:copy-dependencies
cd $orgDir
