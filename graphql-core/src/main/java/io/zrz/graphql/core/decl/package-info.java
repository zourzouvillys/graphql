/**
 * model that represents GraphQL type declarations from a parsed schema.
 *
 * The types are referenced by way of using a GQLTypeReference. There are 3
 * implementations: NonNullModifier, ListModifier, and NamedType. NamedType may
 * only point to a declaration registered with the type reference.
 *
 */

package io.zrz.graphql.core.decl;