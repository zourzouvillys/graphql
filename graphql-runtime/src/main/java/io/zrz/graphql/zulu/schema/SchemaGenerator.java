package io.zrz.graphql.zulu.schema;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import io.zrz.graphql.zulu.executable.ExecutableInputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.graphql.zulu.executable.ExecutableScalarType;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

public class SchemaGenerator {

  private ExecutableSchema j;

  public SchemaGenerator(ExecutableSchema j) {
    this.j = j;
  }

  public String generate() {

    StringBuilder sb = new StringBuilder();

    j.types()
        .forEach(nt -> {

          switch (nt.logicalKind()) {
            case OUTPUT:
              generate((ExecutableOutputType) nt, sb);
              break;
            case SCALAR:
              generate((ExecutableScalarType) nt, sb);
            case ENUM:
            case INPUT:
            case INTERFACE:
            case UNION:
            default:
              break;
          }

        });

    if (!j.operationTypes().isEmpty()) {
      sb.append("schema {\n");

      j.operationTypes()
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

  private void generate(ExecutableScalarType nt, StringBuilder sb) {

    sb.append("scalar ").append(nt.typeName()).append("\n\n");

  }

  private void generate(ExecutableOutputType nt, StringBuilder sb) {

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

          sb.append(" ").append(StringUtils.replaceAll(generate(m), "\n", "\n ")).append("\n\n");

        });

    sb.append("}\n\n");

  }

  private String generate(ExecutableOutputField m) {
    StringBuilder sb = new StringBuilder();

    Optional.ofNullable(m.documentation())
        .map(x -> x.stream())
        .orElse(Stream.empty())
        .forEach(doc -> {

          sb.append("# ").append(doc).append("\n");

        });

    sb.append(m.fieldName());

    if (m.parameters().isPresent()) {
      sb.append("(");
      sb.append(m.parameters().get().fields().values().stream().map(p -> generate(p)).collect(Collectors.joining(", ")));
      sb.append(")");
    }

    sb.append(": ");
    sb.append(typeUse(m.fieldType()));
    return sb.toString();
  }

  /**
   * 
   * @param p
   * @return
   */

  private String generate(ExecutableInputField p) {
    StringBuilder sb = new StringBuilder();
    sb.append(p.fieldName());
    sb.append(": ");
    sb.append(typeUse(p.fieldType()));
    return sb.toString();
  }

  /*
   * write out a type token.
   */

  private String typeUse(ExecutableTypeUse use) {
    try {
      return use.logicalType();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return "???";
    }
  }

}
