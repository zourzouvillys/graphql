package io.zrz.graphql.core.binder;

import org.junit.Test;

import io.zrz.graphql.core.binder.TypeScanner;
import io.zrz.graphql.core.binder.testmodel.TestQueryRoot;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.lang.GQLSchemaBuilder;
import io.zrz.graphql.core.lang.GQLTypeRegistry;
import io.zrz.graphql.core.types.GQLTypes;
import io.zrz.graphql.core.utils.TypePrinter;

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
