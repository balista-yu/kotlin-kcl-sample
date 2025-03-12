#!/bin/sh
bash ./gradlew -t :bootJar &
bash ./gradlew bootRun -PskipDownload=true
