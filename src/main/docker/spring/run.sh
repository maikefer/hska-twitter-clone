#!/bin/bash
docker run -d -p 8080:8080 --name spring_1 --link redis hska/twitter-spring
