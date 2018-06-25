package io.zrz.graphql.generator.protobuf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumValueDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

import io.zrz.zulu.schema.ResolvedEnumType;
import io.zrz.zulu.schema.ResolvedInputType;
import io.zrz.zulu.schema.ResolvedInterfaceType;
import io.zrz.zulu.schema.ResolvedObjectType;
import io.zrz.zulu.schema.ResolvedScalarType;
import io.zrz.zulu.schema.ResolvedUnionType;
import io.zrz.zulu.schema.SchemaType;
import io.zrz.zulu.schema.binding.BoundInlineFragment;
import io.zrz.zulu.schema.binding.BoundLeafSelection;
import io.zrz.zulu.schema.binding.BoundNamedFragment;
import io.zrz.zulu.schema.binding.BoundObjectSelection;
import io.zrz.zulu.schema.binding.BoundOperation;
import io.zrz.zulu.schema.binding.BoundSelection;
import io.zrz.zulu.schema.binding.BoundSelection.VoidVisitor;

public class ProtobufGenerator {

  private final List<BoundOperation> operations;
  private final FileDescriptorProto.Builder shared = FileDescriptorProto.newBuilder();
  private final Path target;

  public ProtobufGenerator(final List<BoundOperation> operations, final Path target, final String javaPackage, final String clientName) {
    this.operations = operations;
    this.target = target;
  }

  public void write() {

    this.operations
        .stream()
        .forEach(op -> {

          final FileDescriptorProto.Builder fd = FileDescriptorProto.newBuilder();

          fd.addDependency("shared.proto");

          final DescriptorProto.Builder mt = this.createMessageType(op);

          fd.addMessageType(mt);

          final FileDescriptorProto res = fd.build();

          try (OutputStream os = new FileOutputStream(this.target.resolve(op.operationName() + ".proto").toFile());
              PrintStream ps = new PrintStream(os)) {
            new ProtoPrinter(ps).print(res);
          }
          catch (final IOException e) {
            throw new RuntimeException(e);
          }

        });

    final FileDescriptorProto res = this.shared.build();

    try (OutputStream os = new FileOutputStream(this.target.resolve("shared.proto").toFile());
        PrintStream ps = new PrintStream(os)) {
      new ProtoPrinter(ps).print(res);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }

    // System.err.println(fd.build());

  }

  private DescriptorProto.Builder createMessageType(final BoundOperation op) {
    final DescriptorProtos.DescriptorProto.Builder db = DescriptorProto.newBuilder();
    db.setName(StringUtils.capitalize(op.operationName()) + "Response");
    this.addFields(db, db, op.selections());
    return db;
  }

  Set<ResolvedEnumType> enums = new HashSet<>();
  private final AtomicInteger counter = new AtomicInteger(0);

  private void ensureEnum(final ResolvedEnumType type) {

    if (!this.enums.add(type)) {
      return;
    }

    final EnumDescriptorProto.Builder eb = EnumDescriptorProto.newBuilder();

    eb.setName(type.typeName());

    eb.addValue(EnumValueDescriptorProto.newBuilder()
        .setName(type.typeName() + "_UNKNOWN")
        .setNumber(0)
        .build());

    final AtomicInteger num = new AtomicInteger(0);

    type.values()
        .forEach(val -> {

          eb.addValue(EnumValueDescriptorProto.newBuilder()
              .setName(type.typeName() + "_" + val.name())
              .setNumber(num.incrementAndGet())
              .build());

        });

    this.shared.addEnumType(eb.build());

  }

  private void addFields(final DescriptorProto.Builder root, final DescriptorProto.Builder db, final List<BoundSelection> selections) {

    final AtomicInteger fid = new AtomicInteger();

    for (final BoundSelection sel : selections) {

      sel.apply(new VoidVisitor() {

        @Override
        public void apply(final BoundLeafSelection sel) {

          final FieldDescriptorProto.Builder fd = FieldDescriptorProto.newBuilder()
              .setJsonName(sel.outputName())
              .setName(sel.outputName());

          fd.setNumber(fid.incrementAndGet());

          sel.fieldType().targetType().apply(new SchemaType.VoidVisitor() {

            @Override
            public void visit(final ResolvedScalarType type) {

              switch (type.typeName()) {
                case "Int":
                  fd.setType(Type.TYPE_SINT64);
                  break;
                case "Boolean":
                  fd.setType(Type.TYPE_BOOL);
                  break;
                case "Double":
                  fd.setType(Type.TYPE_DOUBLE);
                  break;
                case "String":
                default:
                  fd.setType(Type.TYPE_STRING);
                  break;
              }

            }

            @Override
            public void visit(final ResolvedEnumType type) {

              ProtobufGenerator.this.ensureEnum(type);

              fd
                  .setName(sel.outputName())
                  .setTypeName(type.typeName())
                  .setType(Type.TYPE_ENUM);

              fd.setNumber(fid.incrementAndGet());

            }

            @Override
            public void visit(final ResolvedInputType type) {
              throw new IllegalArgumentException();
            }

            @Override
            public void visit(final ResolvedInterfaceType type) {
              throw new IllegalArgumentException();
            }

            @Override
            public void visit(final ResolvedObjectType type) {
              throw new IllegalArgumentException();
            }

            @Override
            public void visit(final ResolvedUnionType type) {
              throw new IllegalArgumentException();
            }

          });

          //
          db.addField(fd.build());

        }

        @Override
        public void apply(final BoundObjectSelection sel) {

          // create a new type for the selection object

          final DescriptorProtos.DescriptorProto.Builder sub = DescriptorProto.newBuilder();

          final String nestedTypeName = StringUtils.capitalize(sel.outputName()) + "Type" + ProtobufGenerator.this.counter.incrementAndGet();

          sub.setName(nestedTypeName);
          //

          ProtobufGenerator.this.addFields(root, sub, sel.selections());

          root.addNestedType(sub);

          final FieldDescriptorProto.Builder fd = FieldDescriptorProto.newBuilder()
              // .setJsonName(sel.outputName())
              .setName(sel.outputName())
              .setNumber(fid.incrementAndGet())
              .setType(Type.TYPE_MESSAGE)
              .setTypeName(nestedTypeName);

          db.addField(fd.build());

        }

        @Override
        public void apply(final BoundInlineFragment sel) {
          // TODO Auto-generated method stub
        }

        @Override
        public void apply(final BoundNamedFragment sel) {
          // TODO Auto-generated method stub
        }

      });

    }
  }

}
