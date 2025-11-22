#!/bin/sh
bash ./gradlew -t :classes &
bash ./gradlew bootRun -PskipDownload=true
