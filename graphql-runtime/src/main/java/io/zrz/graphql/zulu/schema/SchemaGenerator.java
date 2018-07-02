package io.zrz.graphql.zulu.schema;

import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.zrz.graphql.zulu.executable.ExecutableInputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputFieldParam;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.graphql.zulu.executable.ExecutableScalarType;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

public class SchemaGenerator {

  private final ExecutableSchema j;

  public SchemaGenerator(final ExecutableSchema j) {
    this.j = j;
  }

  public String generate() {

    final StringBuilder sb = new StringBuilder();

    this.j.types()
        .forEach(nt -> {

          switch (nt.logicalKind()) {
            case OUTPUT:
              this.generate((ExecutableOutputType) nt, sb);
              break;
            case SCALAR:
              this.generate((ExecutableScalarType) nt, sb);
            case ENUM:
            case INPUT:
            case INTERFACE:
            case UNION:
            default:
              break;
          }

        });

    if (!this.j.operationTypes().isEmpty()) {
      sb.append("schema {\n");

      this.j.operationTypes()
          .forEach((op, type) -> {
            sb.append("  ");
            sb.append(op);
            sb.append(": ");
            sb.append(type.typeName());
            sb.append("\n");
          });

      sb.append("}\n\n");
    }

    return sb.toString();

  }

  private void generate(final ExecutableScalarType nt, final StringBuilder sb) {

    sb.append("scalar ").append(nt.typeName()).append("\n\n");

  }

  private void generate(final ExecutableOutputType nt, final StringBuilder sb) {

    sb.append("type");

    sb.append(" ");
    sb.append(nt.typeName());

    // List<String> supertypes = nt.type()
    // .supertypes()
    // .flatMap(x -> x.names().stream())
    // .collect(Collectors.toList());
    //
    // if (!supertypes.isEmpty()) {
    // sb.append(" implements ");
    // sb.append(supertypes.stream().collect(Collectors.joining(" & ")));
    // }

    sb.append(" {\n\n");

    nt.fields().values().stream()
        .forEach(m -> {

          sb.append(" ").append(StringUtils.replaceAll(this.generate(m), "\n", "\n ")).append("\n\n");

        });

    sb.append("}\n\n");

  }

  private String generate(final ExecutableOutputField m) {
    final StringBuilder sb = new StringBuilder();

    Optional.ofNullable(m.documentation())
        .ifPresent(doc -> sb.append("# ").append(doc).append("\n"));

    sb.append(m.fieldName());

    if (m.parameters().isPresent()) {
      sb.append("(");
      sb.append(m.parameters().get().fields().values().stream().map(p -> this.generate(p)).collect(Collectors.joining(", ")));
      sb.append(")");
    }

    sb.append(": ");
    sb.append(this.typeUse(m.fieldType()));
    return sb.toString();
  }

  /**
   *
   * @param p
   * @return
   */

  private String generate(final ExecutableOutputFieldParam p) {
    final StringBuilder sb = new StringBuilder();
    sb.append(p.fieldName());
    sb.append(": ");
    sb.append(this.typeUse(p.fieldType()));
    return sb.toString();
  }

  /**
   *
   * @param p
   * @return
   */

  private String generate(final ExecutableInputField p) {
    final StringBuilder sb = new StringBuilder();
    sb.append(p.fieldName());
    sb.append(": ");
    sb.append(this.typeUse(p.fieldType()));
    return sb.toString();
  }

  /*
   * write out a type token.
   */

  private String typeUse(final ExecutableTypeUse use) {
    try {
      return StringUtils.repeat("[", use.arity()) + use.logicalType() + StringUtils.repeat("]", use.arity());
    }
    catch (final Exception ex) {
      ex.printStackTrace();
      return "???";
    }
  }

}
