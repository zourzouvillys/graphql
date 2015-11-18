# GraphQL Java Client

Provides a Java implementation for interacting with a GraphQL server.

## GQLChannel

The primary mechanism for executing a query on a remote GraphQL server is the GQLChannel.  this abstracts way the underlying details, leaving just a simple execution api.

There are currently GQLChannel implementations for HTTP (using Unirest) and "loopback" (in memory) queries.  It's trivial to implement other channel types.

## Client Bindings

The client binder builder is used to convert a client stub into a fully fledged client.  The binder analyzes the methods and return types of them to automatically generate the queries: 

    ClientStub client = GQLClientBinderBuilder
        .forStub(MyTestClient.ClientStub.class)
        .withChannel(UnirestHttpChannelBuilder.forUri("http://my.endpoint/graphql").build())
        .build();
    
    // use the client stub as the interface
    QueryUserResult res = client.query("theo").execute();

## TODO

- mutations
- subscriptions

