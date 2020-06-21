# mtproto-tcp

## Build
```shell script
sbt clean compile
```

## Test
```shell script
sbt test
```

## Run executable jar
```shell script
sbt assembly
java [-Dport=8080] -jar target/scala_*/mtproto-tcp-assembly-0.1.0.jar
```
