package io.zrz.graphql.generator.protobuf;

import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

public class ProtoPrinter {

  private static final String SP = "  ";
  private final PrintStream out;
  private int depth;

  public ProtoPrinter(final PrintStream out) {
    this.out = out;
    this.depth = 0;
  }

  public void print(final FileDescriptorProto res) {
    this.out.println("syntax = \"proto3\";");
    this.out.println("option optimize_for = CODE_SIZE;");
    this.out.println();
    res.getDependencyList()
        .forEach(dep -> {

          this.out.print("import \"");
          this.out.print(dep);
          this.out.println("\";");

        });
    this.out.println();

    res.getEnumTypeList().forEach(this::print);
    res.getMessageTypeList().forEach(this::print);
  }

  public void print(final EnumDescriptorProto res) {

    this.out.print(StringUtils.repeat(SP, this.depth));
    this.out.print("enum ");
    this.out.print(res.getName());
    this.out.println(" {");
    this.depth++;

    res.getValueList()
        .forEach(val -> {
          this.out.print(StringUtils.repeat(SP, this.depth));
          this.out.print(val.getName());
          this.out.print(" = ");
          this.out.print(val.getNumber());
          this.out.println(";");
        });

    this.depth--;
    this.out.print(StringUtils.repeat(SP, this.depth));
    this.out.println("}");

  }

  public void print(final DescriptorProto res) {

    this.out.print(StringUtils.repeat(SP, this.depth));
    this.out.print("message ");
    this.out.print(res.getName());
    this.out.println(" {");
    this.depth++;

    res.getFieldList().forEach(this::print);
    res.getNestedTypeList().forEach(this::print);

    this.depth--;
    this.out.print(StringUtils.repeat(SP, this.depth));
    this.out.println("}");

  }

  public void print(final FieldDescriptorProto res) {

    this.out.print(StringUtils.repeat(SP, this.depth));

    // if (res.isRepeated()) {
    // this.out.print("repeated ");
    // }

    switch (res.getType()) {
      case TYPE_ENUM:
      case TYPE_MESSAGE:
        this.out.print(res.getTypeName());
        break;
      case TYPE_BOOL:
      case TYPE_BYTES:
      case TYPE_DOUBLE:
      case TYPE_FIXED32:
      case TYPE_FIXED64:
      case TYPE_FLOAT:
      case TYPE_INT32:
      case TYPE_INT64:
      case TYPE_SFIXED32:
      case TYPE_SFIXED64:
      case TYPE_SINT32:
      case TYPE_SINT64:
      case TYPE_STRING:
      case TYPE_UINT32:
      case TYPE_UINT64:
        this.out.print(res.getType().name().toLowerCase().substring(5));
        break;
      case TYPE_GROUP:
      default:
        throw new IllegalArgumentException(res.getType().name());

    }

    this.out.print(" ");
    this.out.print(res.getName());
    this.out.print(" = ");
    this.out.print(res.getNumber());

    this.out.println(";");

  }

}
