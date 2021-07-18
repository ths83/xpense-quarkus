# XPENSES

## Why ?

This application allows several users to track and share their expenses.

Please notice the first version can manage up to 2 users, as the next versions will be available to more users.

_Some configuration files are deliberately missing._

### Creating a native executable

You can create a native executable using:

```shell script
mvnci -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

_Note : quarkus.native.native-image-xmx args must be set for macos and Windows in order to allocate enough memory when
building the image._

```shell script
mvnci -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.native-image-xmx=4g
```

### Running the image

```shell script
docker run -i --rm -p 8080:8080 quarkus/xpenses-quarkus  
```

#### Native executable code

https://quarkus.io/guides/writing-native-applications-tips

The following dependency must be added for serialization

```xml

<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-jsonb</artifactId>
</dependency>
```

- Entities must be annotated by ```@RegisterForReflection``` to be included in the native executable.
- Entities must contain at least a default constructor and a constructor with all parameters.
- Entities constructors cannot be generated from Lombok annotations.

### NEW ! Kubernetes Kafka setup

Simple setup https://strimzi.io/quickstarts

YAML :

- [[kafka-consumer](service/src/main/kubernetes/kafka-consumer.yaml)]
- [[kafka-producer](service/src/main/kubernetes/kafka-producer.yaml)]
- [[xpenses-quarkus](service/src/main/kubernetes/xpenses-quarkus.yaml)]

#### Docker build image

```shell
 # Enable storage to minikube for built images
 eval $(minikube -p minikube docker-env)
     
 mvn package 
 docker build -f service/src/main/docker/Dockerfile.jvm .
 
 # Expose pod by creating a k8s service
 kubectl expose pod -n kafka xpenses-quarkus --port=8080 --type=NodePort
 
 # Get minikube tunnel info to use the service
 minikube service -n kafka xpenses-quarkus
 
 # Use the localhost ip to connect to the application
```

### NEW ! Kamel Kafka

Create a Kamel application to listen from the given kafka topic and performs actions following the Enterprise
Integration Patterns.

#### Install the Kamel CLI to deploy the application in K8s

__MacOs__

```shell
# Install the CLI
brew install kamel

# Add the CLI to the cluster and given namespace
kamel install -n kafka

# Deploy the application to the namespace
kamel run kamel/src/main/java/org/thoms/KafkaRoute.java -n kafka
```

__Useful links__

- https://camel.apache.org/camel-k/latest/cli/modeline.html
- https://camel.apache.org/camel-k/latest/running/running.html
- https://camel.apache.org/camel-k/latest/installation/installation.html#procedure
- https://camel.apache.org/components/3.11.x/kafka-component.html