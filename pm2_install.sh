#!/usr/bin/env bash
pm2 stop ranking
pm2 delete ranking
./gradlew clean
./gradlew stage
pm2 start pm2.config.js