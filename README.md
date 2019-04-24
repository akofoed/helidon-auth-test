
# Helidon Auth Test.

This example implements a simple Hello World REST service with with two resources using two different authorization mechanism - both active at the same time:
* HTTP Basic Authentication
* JWT

Based on the quickstart-se.


## Prerequisites

1. Maven 3.5 or newer
2. Java SE 8 or newer
3. Docker 17 or newer to build and run docker images
4. Kubernetes minikube v0.24 or newer to deploy to Kubernetes (or access to a K8s 1.7.4 or newer cluster)
5. Kubectl 1.7.4 or newer to deploy to Kubernetes

Verify prerequisites
```
java -version
mvn --version
docker --version
minikube version
kubectl version --short
```

## Build

```
mvn package
```

## Start the application

```
java -jar target/quickstart-se.jar
```

I just use VScode and start it with F5.


## Get a new JWT token (no auth required)

```
curl -X GET http://localhost:8080/greet/new
{
"token": "eyJhbGciOiJIUzI1NiIsImtpZCI6IkpXVCJ9.eyJpc3MiOiJoZWxpZG9uIiwiZXhwIjoxNTU2MTQzMDczLCJpYXQiOjE1NTYxMzk0NzMsIm5iZiI6MTU1NjEzOTQ3MSwic3ViIjoiNTQ1NjQ2NDU2NDY0NjUiLCJ1cG4iOiI1NDU2NDY0NTY0NjQ2NSIsImF1ZCI6WyJDdXN0b21lcjIiXSwianRpIjoiZmU1NTQzZWItY2I4YS00YzQzLTgyMzgtYzZjN2NjNmFjYjgxIn0=.zol3uu0AjsqQVCPVSwLuOm_15hkC1YicG1Av1F0vwug="
}
```

## Using username and password (akofoed/password) to acccess to /greet (HTTP Basic Authentication)
HTTP header: authorization: Basic YWtvZm9lZDpwYXNzd29yZA==
```
curl -X GET -H "authorization: Basic YWtvZm9lZDpwYXNzd29yZA==" http://localhost:8080/greet/
{"message":"Hello akofoed World!"}
```

## Access using a JWT token
HTTP header: authorization: bearer eyJhbGciOiJIUzI1NiIsImtpZCI6IkpXVCJ9.eyJpc3MiOiJoZWxpZG9uIiwiZXhwIjoxNTU2MTQzMDczLCJpYXQiOjE1NTYxMzk0NzMsIm5iZiI6MTU1NjEzOTQ3MSwic3ViIjoiNTQ1NjQ2NDU2NDY0NjUiLCJ1cG4iOiI1NDU2NDY0NTY0NjQ2NSIsImF1ZCI6WyJDdXN0b21lcjIiXSwianRpIjoiZmU1NTQzZWItY2I4YS00YzQzLTgyMzgtYzZjN2NjNmFjYjgxIn0=.zol3uu0AjsqQVCPVSwLuOm_15hkC1YicG1Av1F0vwug=

```
curl -X GET -H "authorization: bearer eyJhbGciOiJIUzI1NiIsImtpZCI6IkpXVCJ9.eyJpc3MiOiJoZWxpZG9uIiwiZXhwIjoxNTU2MTQzMDczLCJpYXQiOjE1NTYxMzk0NzMsIm5iZiI6MTU1NjEzOTQ3MSwic3ViIjoiNTQ1NjQ2NDU2NDY0NjUiLCJ1cG4iOiI1NDU2NDY0NTY0NjQ2NSIsImF1ZCI6WyJDdXN0b21lcjIiXSwianRpIjoiZmU1NTQzZWItY2I4YS00YzQzLTgyMzgtYzZjN2NjNmFjYjgxIn0=.zol3uu0AjsqQVCPVSwLuOm_15hkC1YicG1Av1F0vwug=" http://localhost:8080/greet/Joe
{"message":"Hello Joe!"}
```




