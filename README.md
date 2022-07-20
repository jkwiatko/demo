# Demo app
___

### 1. Run app services using docker-compose

 ```
 docker-compose -f docker/demo-app-services/docker-compose.local.yml up -d
 ```

### 2. Build project with gradle

```
   ./gradlew build
```

### 3. Run using java

```
    java -jar ./build/libs/demo-0.0.1-SNAPSHOT.jar
```