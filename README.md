# Widget Assignment

This microservice manages widgets.

## Technical Information

### Stack
- Spring Boot
- Kotlin
- Maven

### General Information

The service was implement using DDD to isolate the business logic in the core layer from the external dependencies in the adapter layer.

There are two different adapter implementation in memory storage and in memory DB, the DB chosen was H2, while the in memory storage stores the data on a Map data structure in a singleton class.

This service has high coverage on both, unit and integration tests.

It was developed taking into consideration low complexity and high performance to make sure it is highly scalable.

## Guide
To execute it just run a Maven clean install and execute the WidgetApplication main method.

To switch between different adapters change the property database.active in the application.yaml file, when set to true it uses H2, when false it will use the singleton storage class, both implementation shall produce the same results.

In the resources/postman folder there is an example of each request, also examples of how to paginate, sort, change rate limits and should be used as a guide to interact with this service.

## Contact
- Developer: Felippe Alzuguir
- Email: felippealzuguir@gmail.com

You can contact me about any doubts or suggestions