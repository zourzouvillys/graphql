package io.zrz.graphql.zulu.doc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.zrz.graphql.core.doc.GQLFragmentDefinition;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.doc.GQLSelectedOperation;
import io.zrz.zulu.types.ZAnnotation;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZPrimitiveScalarType;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.types.ZTypeUse;
import io.zrz.zulu.values.ZStructValue;
import io.zrz.zulu.values.ZValue;

/**
 * a prepared GQL operation, which has performed rudimentary variable resolution and binding.
 */

class DefaultGQLPreparedOperation implements GQLPreparedOperation {

  private GQLSelectedOperation op;
  private GQLTypeResolver resolver;
  private DefaultGQLPreparedDocument doc;

  public DefaultGQLPreparedOperation(DefaultGQLPreparedDocument doc, GQLTypeResolver resolver, GQLSelectedOperation op) {
    this.doc = doc;
    this.resolver = resolver;
    this.op = op;
  }

  /**
   * the name of this operation, if defined.
   */

  @Override
  public Optional<String> operationName() {
    return Optional.ofNullable(op.operationName());
  }

  GQLOperationDefinition operation() {
    return this.op.operation();
  }

  /**
   * the type of operation, e.g query, mutation, or subscription.
   */

  @Override
  public GQLOpType type() {
    return op.operation().type();
  }

  /**
   * directives applied on the operation itself.
   */

  @Override
  public List<ZAnnotation> annotations() {
    return op.operation().directives().stream().map(x -> new ZAnnotation() {

      @Override
      public Optional<ZStructValue> value() {
        return null;
      }

      @Override
      public String name() {
        return x.name();
      }

    })
        .collect(Collectors.toList());
  }

  /**
   * the expected input variables, as an anonymous {@link ZStructType} instance.
   */

  @Override
  public ZStructType inputType() {

    return new ZStructType() {

      @Override
      public Map<String, ZField> fields() {
        return op.operation().vars()
            .stream()
            .collect(Collectors.toMap(x -> x.name(), x -> new ZField() {

              @Override
              public ZTypeUse fieldType() {
                return ZTypeUse.of(ZPrimitiveScalarType.STRING);
              }

              @Override
              public Optional<ZValue> defaultValue() {
                return x.defaultValue().apply(new ConstantZValueValueExtractor(DefaultGQLPreparedOperation.this, null, null));
              }

              @Override
              public List<ZAnnotation> annotations() {
                // TODO Auto-generated method stub
                return Collections.emptyList();
              }

            }));
      }

    };

  }

  /**
   * each of the selections.
   */

  @Override
  public List<DefaultGQLPreparedSelection> selection() {
    return op
        .operation()
        .selections()
        .stream()
        .flatMap(x -> x.apply(new SelectionGenerator(this)))
        .collect(Collectors.toList());
  }

  GQLFragmentDefinition fragment(String name) {
    return this.op.doc().fragment(name);
  }

  GQLTypeResolver typeResolver() {
    return this.resolver;
  }

  @Override
  public String toString() {
    return this.operation().type() + " " + this.operationName().orElse("<unnamed>");
  }

  @Override
  public GQLPreparedDocument document() {
    return this.doc;
  }

}
