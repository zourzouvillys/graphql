# Java GraphQL

A Java 8+ implementation of GraphQL.

## Overview

The implementation is separated into a bunch of components:

The *core* which contains a model representation of GraphQL primitives, a type/value system, and a GraphQL document and schema parser. 

The *runtime* provides an execution engine that binds to java methods and uses reflection & annotations to locate handlers. 

The *server* contains a server side implementation that uses the JVM type system for building schemas directly in java.  It implements a basic query planner.

The *client* provides a client side binding for sending queries to a server and providing the response to the caller.

The *generator* contains a mechanism to take an input GraphQL schema and set of queries, and generate a typed Java interface to use with the client.

The *cli* provides a main class which can be used to generate and extract schemas from the command line.

# Status

JOSS GraphQL is currently a preview release.

It does not currently fully support:

- fragments
- mutations
- subscriptions

The scalar type system needs some work.
