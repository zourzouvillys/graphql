package io.zrz.zulu.schema.writer;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class GQLSchemaWriter {

  private enum Context {
    NONE,
    TYPE,
    DIRECTIVE_NAME,
    DIRECTIVE_PARAM_NAME,
    DIRECTIVE_PARAM_VALUE,
    FIELD_NAME,
    PARAM_NAME,
    PARAM_TYPE,
    FIELD_TYPE,
    ENUM_TYPE,
    ENUM_CONSTANT,
  }

  private enum Scope {
    NONE,
    TYPE,
    FIELD
  }

  private Context context = Context.NONE;
  private final PrintStream w;
  private int depth = 0;
  private final Scope scope = Scope.NONE;
  private int fieldCount;

  public GQLSchemaWriter(final PrintStream w) {
    this.w = w;
  }

  public void writeEndDirective() {
  }

  public void writeEndField() {

    switch (this.context) {
      case FIELD_TYPE:
        this.w.println();
        break;
      case DIRECTIVE_NAME:
        this.w.println();
        break;
      case DIRECTIVE_PARAM_VALUE:
        this.w.println(")");
        this.w.println();
        break;
      default:
        throw new IllegalStateException(this.context.name());
    }

    this.context = Context.TYPE;

  }

  public void writeFieldName(final String fieldName) {

    switch (this.context) {
      case TYPE:
        if (this.fieldCount == 0) {
          this.w.println(" {");
        }
        this.w.print(StringUtils.repeat("  ", this.depth));
        break;
      case FIELD_TYPE:
        this.w.println();
        this.w.print(StringUtils.repeat("  ", this.depth));
        break;
      case DIRECTIVE_NAME:
        this.w.print("(");
        break;
      case DIRECTIVE_PARAM_VALUE:
        this.w.println(")");
        this.w.print(StringUtils.repeat("  ", this.depth));
        break;
      case FIELD_NAME:
        this.w.print("(");
        break;
      case PARAM_TYPE:
      case NONE:
      case PARAM_NAME:
      default:
        throw new IllegalStateException(this.context.name());
    }

    this.fieldCount++;

    this.w.print(fieldName);
    this.w.print(": ");

    switch (this.context) {
      case FIELD_NAME:
        this.context = Context.PARAM_NAME;
        break;
      case DIRECTIVE_NAME:
        this.context = Context.DIRECTIVE_PARAM_NAME;
        break;
      default:
        this.context = Context.FIELD_NAME;
        break;
    }

  }

  public void writeArgName(final String fieldName) {

    switch (this.context) {
      case TYPE:
        this.w.print(StringUtils.repeat("  ", this.depth));
        break;
      case FIELD_TYPE:
        this.w.println();
        this.w.print(StringUtils.repeat("  ", this.depth));
        break;
      case DIRECTIVE_NAME:
        this.w.print("(");
        break;
      case DIRECTIVE_PARAM_VALUE:
        this.w.println(")");
        this.w.print(StringUtils.repeat("  ", this.depth));
        break;
      case FIELD_NAME:
        this.w.print("(");
        break;
      case PARAM_TYPE:
      case NONE:
      case PARAM_NAME:
      default:
        throw new IllegalStateException(this.context.name());
    }
    this.w.print(fieldName);
    this.w.print(": ");

    switch (this.context) {
      case FIELD_NAME:
        this.context = Context.PARAM_NAME;
        break;
      case DIRECTIVE_NAME:
        this.context = Context.DIRECTIVE_PARAM_NAME;
        break;
      default:
        this.context = Context.FIELD_NAME;
        break;
    }

  }

  public void writeNullableType(final String typeName) {
    this.writeType(typeName, false, 0);
  }

  public void writeRequiredType(final String typeName) {
    this.writeType(typeName, true, 0);
  }

  public void writeType(final String typeName, final boolean required, final int dims) {
    switch (this.context) {
      case DIRECTIVE_PARAM_NAME:
        this.w.print(" ");
        break;
      case PARAM_NAME:
        break;
      case PARAM_TYPE:
        this.w.print("): ");
        break;
      case FIELD_NAME:
        break;
      default:
        throw new IllegalStateException(this.context.name());
    }
    this.w.print(StringUtils.repeat("[", dims));
    this.w.print(typeName);
    this.w.print(StringUtils.repeat("]", dims));
    if (required) {
      this.w.print("!");
    }
    switch (this.context) {
      case DIRECTIVE_NAME:
        this.context = Context.FIELD_TYPE;
        break;
      case PARAM_TYPE:
        this.context = Context.FIELD_TYPE;
        break;
      case PARAM_NAME:
        this.context = Context.PARAM_TYPE;
        break;
      case FIELD_NAME:
        this.context = Context.FIELD_TYPE;
        break;
      default:
        throw new IllegalStateException(this.context.name());
    }
  }

  public void startExtendType(final GQLTypeDeclKind kind, final String typeName) {
    this.w.print("extend");
    this.startType(kind, typeName);
  }

  public void startType(final GQLTypeDeclKind kind, final String typeName, final String... interfaces) {
    this.startType(kind, typeName, Arrays.asList(interfaces));
  }

  public void startType(final GQLTypeDeclKind kind, final String typeName, final Collection<String> interfaces) {

    this.fieldCount = 0;
    this.writeKind(kind);

    this.w.print(" ");
    this.w.print(typeName);

    if (!interfaces.isEmpty()) {
      this.w.print(" extends ");
      this.w.print(Joiner.on(" & ").join(interfaces));
    }

    this.depth++;
    this.context = Context.TYPE;

  }

  public void writeEndType() {

    switch (this.context) {
      case TYPE:
      case ENUM_TYPE:
        break;
      case FIELD_TYPE:
        this.w.println();
        break;
      case DIRECTIVE_PARAM_VALUE:
        this.w.println(")");
        break;
      case ENUM_CONSTANT:
        this.w.println();
        break;
      case DIRECTIVE_PARAM_NAME:
      case PARAM_TYPE:
      case NONE:
      case FIELD_NAME:
      case PARAM_NAME:
      default:
        throw new IllegalStateException(this.context.name());
    }

    this.depth--;
    this.w.println("}");
    this.w.println();
    this.context = Context.NONE;
  }

  public void writeKind(final GQLTypeDeclKind kind) {
    switch (kind) {
      case ENUM:
        this.w.print("enum");
        break;
      case INPUT_OBJECT:
        this.w.print("input");
        break;
      case INTERFACE:
        this.w.print("interface");
        break;
      case OBJECT:
        this.w.print("type");
        break;
      case SCALAR:
        this.w.print("scalar");
        break;
      case UNION:
        this.w.print("union");
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public void writeRequiredField(final String fieldName, final String typeName) {
    this.writeFieldName(fieldName);
    this.writeType(typeName, true, 0);
  }

  public void writeNullableField(final String fieldName, final String typeName) {
    this.writeFieldName(fieldName);
    this.writeType(typeName, false, 0);
  }

  public void writeParamStart() {
    this.w.print("(");
  }

  public void writeParamEnd() {
    this.w.print(")");
  }

  public void writeStartDirective(final String name) {

    switch (this.context) {
      case DIRECTIVE_PARAM_VALUE:
        this.w.print(") ");
        break;
      case FIELD_TYPE:
        this.w.print(" ");
        break;
      case TYPE:
      case PARAM_TYPE:
      case NONE:
      case FIELD_NAME:
      case PARAM_NAME:
      default:
        throw new IllegalStateException(this.context.name());
    }

    this.w.print("@");
    this.w.print(name);

    this.context = Context.DIRECTIVE_NAME;

  }

  public void writeStringValue(final String string) {

    switch (this.context) {
      case DIRECTIVE_PARAM_NAME:
        this.context = Context.DIRECTIVE_PARAM_VALUE;
        break;
      case TYPE:
      case FIELD_TYPE:
      case DIRECTIVE_PARAM_VALUE:
      case PARAM_TYPE:
      case NONE:
      case FIELD_NAME:
      case PARAM_NAME:
      default:
        throw new IllegalStateException(this.context.name());
    }

    this.w.print('"');
    this.w.print(string.replace("\"", "\\\""));
    this.w.print('"');

  }

  public void writeEnumConstant(final String enumConstant) {
    if (this.fieldCount == 0) {
      this.w.println(" {");
    }
    this.fieldCount++;
    if (this.context == Context.ENUM_CONSTANT) {
      this.w.println(",");
    }
    this.w.print("  ");
    this.w.print(enumConstant);
    this.context = Context.ENUM_CONSTANT;
  }

}
