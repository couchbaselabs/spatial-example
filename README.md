A simple spatial example with shops
===================================

Simple download and run:

1. Grab it from here (the jar) https://github.com/daschl/spatial-example/releases/tag/0.1-pre
2. Go to step 3 below


How to build and run it:

** Note that you need a 2.1-SNAPSHOT from couchbase-java-client to build it. **

1. Clone the repository
2. Run `mvn package`
3. Run the fat jar that got built:

```
michael@daschlbook-3 ~/tmp/spatial-example $ java -jar target/geo-1.0-SNAPSHOT.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v1.2.1.RELEASE)
...
```

4. Navigate to `localhost:8080` and play around!
