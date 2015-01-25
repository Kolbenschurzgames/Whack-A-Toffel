#!/bin/sh
./gradlew :server:npmInstall
./gradlew :server:npm_test
./gradlew :server:integration