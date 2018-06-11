package io.zrz.graphql.core.lang;

import static io.zrz.graphql.core.types.GQLTypes.builtins;
import static io.zrz.graphql.core.types.GQLTypes.nonNull;
import static io.zrz.graphql.core.types.GQLTypes.scalar;
import static io.zrz.graphql.core.types.GQLTypes.structBuilder;
import static io.zrz.graphql.core.types.GQLTypes.union;

import org.junit.Assert;
import org.junit.Test;

import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLParameterableFieldDeclaration;

public class GQLSchemaBuilderTest {

	@Test(expected = DuplicateTypeNameException.class)
	public void testFailOnDuplicateNames() {
		final GQLSchemaBuilder builder = new GQLSchemaBuilder();
		builder.add(scalar("ID"));
		builder.add(scalar("ID"));
		builder.build(); // throws.
	}

	@Test
	public void testRegisterBuiltints() {
		final GQLSchemaBuilder builder = new GQLSchemaBuilder();
		builder.add(builtins());
		final GQLTypeRegistry reg = builder.build();
		Assert.assertNotNull(reg.scalar("String"));
		Assert.assertNotNull(reg.scalar("Int"));
		Assert.assertNotNull(reg.scalar("Float"));
		Assert.assertNotNull(reg.scalar("Boolean"));
		Assert.assertNotNull(reg.scalar("ID"));
	}

	/**
	 * make sure a definition which references an unknown type throws.
	 */

	@Test(expected = UnresolvableTypeNameException.class)
	public void testRefInvalidTypeName() {
		final GQLSchemaBuilder builder = new GQLSchemaBuilder();
		builder.add(builtins());
		builder.add(union("MyUnion", "unknbownBLOOP", "Int"));
		try {
			builder.build(); // throws, as it doesn't exist.
		} catch (RuntimeException ex) {
			if (ex.getCause() instanceof UnresolvableTypeNameException) {
				throw (UnresolvableTypeNameException) ex.getCause();
			}
			throw ex;
		}
	}

	/**
	 * make sure all type references are updated.
	 */

	@Test
	public void testRefReplacement() {

		final GQLSchemaBuilder builder = new GQLSchemaBuilder();

		builder.add(builtins());
		builder.add(union("MyUnion", "String", "Int"));
		builder.add(structBuilder("MyStruct")
				// .iface(ifaceBuilder("xxx").addField("moo",
				// nonNull("String")).build())
				.addFields(
						ImmutableGQLParameterableFieldDeclaration.builder().name("moo").type(nonNull("String")).build())
				// .addFields("moo", nonNull("String"))
				.build());

		final GQLTypeRegistry reg = builder.build();

		final GQLUnionTypeDeclaration union = reg.union("MyUnion");

		Assert.assertEquals(reg.scalar("String"), union.types().get(0).ref());

	}

}
