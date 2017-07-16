package io.jgql.core.binder;

import org.junit.Test;

import io.jgql.core.binder.testmodel.TestQueryRoot;
import io.joss.graphql.core.binder.TypeScanner;
import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.lang.GQLSchemaBuilder;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import io.joss.graphql.core.types.GQLTypes;
import io.joss.graphql.core.utils.TypePrinter;

public class TypeScannerTest
{

  @Test
  public void test()
  {

    GQLSchemaBuilder builder = new GQLSchemaBuilder();

    builder.add(GQLTypes.builtins());

    TypeScanner scanner = new TypeScanner(builder);

    GQLTypeDeclaration root = scanner.add(TestQueryRoot.class);

    scanner.finish();

    GQLTypeRegistry reg = builder.build();

    reg.types().forEach(type -> {
      type.apply(new TypePrinter(System.out));
    });

  }

}
