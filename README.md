# Java GraphQL

A Java 8+ implementation of GraphQL.

## Overview

The implementation is separated into a bunch of components:

The *core* which contains a model representation of GraphQL primitives, a type/value system, and a GraphQL document and schema parser. 

The *runtime* provides an execution engine that binds to java methods and uses reflection & annotations to locate handlers. 

## Getting Started

```

repositories {
  jcenter()
  maven { url 'https://jitpack.io' }
}


dependencies {
  implementation 'io.zrz.graphql:graphql-server-netty:master-SNAPSHOT'
  implementation 'io.zrz.graphql:graphql-jackson:master-SNAPSHOT'
}
  
```

Basic HTTP server:


```

import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.plugins.Jre8ZuluPlugin;
import io.zrz.graphql.zulu.schema.GQLSchema;
import io.zrz.zulu.server.netty.ZuluNettyServer;

public class FlowServer {

  public static void main(final String[] args) {

    // create engine
    final ZuluEngine engine = ZuluEngine.builder()
        .type(GQLSchema.class)
        .queryRoot(MyQueryRoot.class)
        .mutationRoot(MyMutationRoot.clsass)
        .plugin(new Jre8ZuluPlugin())
        .build();

    final InMemoryFlowRepository repo = new InMemoryFlowRepository();

    // start up.
    ZuluNettyServer.create(9999, engine)
        .bind(MyMutationRoot.class, new MyMutationRoot(repo))
        .bind(MyQueryRoot.class, new MyQueryRoot(repo))
        .startAsync()
        .awaitTerminated();

  }

}
```