# Marks Storefinder

Sample app to showcase Spring Boot exposing data from NoSQL DB as a RESTful API.
Marks store data has been exported from PMM ERP into simple CSV file (using Groovy) for import into MongoDB

## Quickstart

1. Install MongoDB (http://www.mongodb.org/downloads
2. Build and run the app (`mvn spring-boot:run`)
3. Access the root resource to use the API (`curl http://localhost:8080/api`)
4. Access the simple UI by pointing browser at the root context (`http://localhost:8080`)