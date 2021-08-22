# XPENSES

_Branch for testing purpose: please use Docker or Podman as Containers env._

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