# Type Extensions

As a project gets larger, your roots might start to get large.  You can seperate out parts of a exposed GraphQL type using @GQLExtension.

To use it, annotate the type (or specific methods) with @GQLExtension, then register the type using builder.extension(TheType.class).

Each method must the the type being extended as the first parameter.
