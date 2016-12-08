#!/bin/bash
pushd ../../../../
mvn install
popd

cp ../../../../target/twitter-clone-0.0.1.jar .
docker build -t hska/twitter-spring .

