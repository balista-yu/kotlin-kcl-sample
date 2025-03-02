#!/bin/sh
bash ./gradlew bootJar
bash ./gradlew bootRun -PskipDownload=true
