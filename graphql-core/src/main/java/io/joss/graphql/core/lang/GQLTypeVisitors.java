package io.joss.graphql.core.lang;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
import lombok.experimental.UtilityClass;

@UtilityClass

public class GQLTypeVisitors {

	/**
	 * Visitor which returns true if the type is a scalar.
	 */

	public IsScalarTypeVisitor isScalar() {
		return new IsScalarTypeVisitor();
	}

	/**
	 * Visitor which returns true if the type is a list.
	 */

	public IsListTypeVisitor isList() {
		return new IsListTypeVisitor();
	}

	/**
	 * Visitors which returns true if the type is not nullable.
	 */

	public static IsNonNullTypeVisitor isNotNull() {
		return new IsNonNullTypeVisitor();
	}

	/**
	 * resolves concrete references. it will fail (on purpose) if the type is
	 * non-null or list. So only an unwrapped type must be provided.
	 * 
	 * @param typeRegistry
	 *            The registry to resolve named types.
	 */

	public static TypeResolver resolver(GQLTypeRegistry typeRegistry) {
		return new TypeResolver(typeRegistry);
	}

	/**
	 * Fetches the root declaration type - if it's a list it's the inner type,
	 * if it's a non null, then it's the actual type
	 */

	public static GQLTypeVisitor<GQLDeclaration> rootType() {

		return new GQLTypeVisitor<GQLDeclaration>() {

			@Override
			public GQLDeclaration visitNonNull(GQLNonNullType type) {
				return type.type().apply(this);
			}

			@Override
			public GQLDeclaration visitList(GQLListType type) {
				return type.type().apply(this);
			}

			@Override
			public GQLDeclaration visitDeclarationRef(GQLDeclarationRef type) {
				return type;
			}

		};
	}

}
