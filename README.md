# Java GraphQL

A Java implementation of GraphQL.

## Overview

The implementation is seperated into the *core* which contains a model representation and type system.

The *server* contains a server side implementation.

The *generator* contains some simple code generation.

The *cli* provides a main class which can be used to generate and extract schemas from the command line.

# Status

JOSS GraphQL is currently a preview release.

It does not support:

- fragments
- mutations
- subscriptions

The scalar type system needs some work.

