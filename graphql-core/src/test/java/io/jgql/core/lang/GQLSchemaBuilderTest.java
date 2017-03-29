package io.jgql.core.lang;

import static io.joss.graphql.core.types.GQLTypes.builtins;
import static io.joss.graphql.core.types.GQLTypes.nonNull;
import static io.joss.graphql.core.types.GQLTypes.scalar;
import static io.joss.graphql.core.types.GQLTypes.structBuilder;
import static io.joss.graphql.core.types.GQLTypes.union;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Throwables;

import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.lang.DuplicateTypeNameException;
import io.joss.graphql.core.lang.GQLSchemaBuilder;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import io.joss.graphql.core.lang.UnresolvableTypeNameException;

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
			Throwables.throwIfInstanceOf(ex.getCause(), UnresolvableTypeNameException.class);
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
				.addField("moo", nonNull("String"))
				.build());

		final GQLTypeRegistry reg = builder.build();

		final GQLUnionTypeDeclaration union = reg.union("MyUnion");

		Assert.assertEquals(reg.scalar("String"), union.types().get(0).ref());

	}

}
