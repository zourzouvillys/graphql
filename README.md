# Java GraphQL

A Java 8+ implementation of GraphQL.

## Overview

The implementation is separated into a bunch of components:

The *core* which contains a model representation of GraphQL primitives, a type/value system, and a GraphQL document and schema parser. 

The *runtime* provides an execution engine that binds to java methods and uses reflection & annotations to locate handlers. 

