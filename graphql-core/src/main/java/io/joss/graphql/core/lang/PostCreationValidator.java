package io.joss.graphql.core.lang;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;

public class PostCreationValidator implements GQLDeclarationVisitor<Void> {

	private GQLTypeRegistry reg;

	public PostCreationValidator(GQLTypeRegistry reg) {
		this.reg = reg;
	}

	@Override
	public Void visitUnion(GQLUnionTypeDeclaration type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitScalar(GQLScalarTypeDeclaration type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Any object on input type decl must be an input or scalar type.
	 */

	@Override
	public Void visitObject(GQLObjectTypeDeclaration type) {

		type.fields().forEach(field -> {

			field.args().stream().forEach(arg -> {

				if (!arg.type().apply(new OnlyReferenceInputTypes(reg))) {

					GQLDeclaration x = reg.resolve((GQLDeclarationRef)arg.type().apply(GQLTypeVisitors.rootType()));
					
					System.err.println(x);
					
					StringBuilder sb = new StringBuilder();

					sb
							.append(arg.name())
							.append(" of field '")
							.append(field.name())
							.append("' on ")
							.append(type.name())
							.append(" must be scalar or input type, but was '")
							.append(x)
							.append("'");

					throw new RuntimeException(sb.toString());
				}

			});

		});
		
		return null;
	}

	/**
	 * make sure that all the input types only have fields which reference
	 * scalars or other input types.
	 * 
	 * @param type
	 * @return
	 */

	@Override
	public Void visitInput(GQLInputTypeDeclaration type) {

		type.fields().forEach(field -> {

			if (!field.type().apply(new OnlyReferenceInputTypes(reg))) {
				throw new RuntimeException(
						String.format("%s on %s can only be a scalar or input type", field.name(), type.name()));
			}

		});
		return null;

		// throw new RuntimeException(String.format("'%s' can only refer to
		// scalars and other input types. not: '%s'", this.type.name(),
		// ref.name()));

	}

	/**
	 * 
	 */

	@Override
	public Void visitInterface(GQLInterfaceTypeDeclaration type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitEnum(GQLEnumDeclaration type) {
		// TODO Auto-generated method stub
		return null;
	}

}
