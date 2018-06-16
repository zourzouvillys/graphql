package io.zrz.graphql.zulu.schema;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import io.zrz.graphql.core.decl.GQLDirectiveLocation;
import io.zrz.graphql.zulu.annotations.GQLOutputType;
import io.zrz.graphql.zulu.executable.ExecutableType;

@GQLOutputType(name = "__Directive")
public class GQLSchemaDirective {

  public GQLSchemaDirective(ExecutableType directive) {
    // TODO Auto-generated constructor stub
  }

  // name: String!
  public String name() {
    return null;
  }

  // description: String
  public String description() {
    return null;
  }

  // locations:[__DirectiveLocation!]!

  public @NonNull List<@NonNull GQLDirectiveLocation> locations() {
    return Collections.emptyList();
  }

  // args:[__InputValue!]!
  public @NonNull List<io.zrz.graphql.zulu.schema.GQLSchemaInputValue> args() {
    return Collections.emptyList();
  }

}
