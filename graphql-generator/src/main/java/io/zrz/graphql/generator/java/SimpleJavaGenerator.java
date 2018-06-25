package io.zrz.graphql.generator.java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.google.common.io.MoreFiles;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import io.reactivex.Single;
import io.zrz.zulu.schema.binding.BoundOperation;
import io.zrz.zulu.schema.binding.BoundVariable;

/**
 * a gimple java generator which creates an interfaces with one method per operation. required arguments are parameters,
 * and a separate builder for optional ones.
 *
 * the result mapping models the source exactly, and each type is nested inside the parent. no connection/edge handling
 * or anything else smart.
 *
 * the models that are generated can be deserialized using jackson or other object mappers.
 *
 * @author theo
 *
 */

public class SimpleJavaGenerator {

  private final List<BoundOperation> operations;
  private final Path target;
  private final ClassName baseClassName;
  private final Builder enclosingClass;
  private final Map<BoundOperation, Builder> callBuilders = new HashMap<>();
  private final String javaPackage;

  public SimpleJavaGenerator(final List<BoundOperation> operations, final Path target, final String javaPackage, final String clientName) {
    this.operations = operations;
    this.target = target;
    this.javaPackage = javaPackage;
    this.baseClassName = ClassName.get(javaPackage, clientName);

    this.enclosingClass = TypeSpec
        .interfaceBuilder(this.baseClassName)
        .addModifiers(Modifier.PUBLIC);

  }

  public void write() throws IOException {

    // create the client stub & builders.

    this.operations.forEach(op -> this.write(op));

    final JavaFile javaFile = JavaFile
        .builder(this.baseClassName.packageName(), this.enclosingClass.build())
        .build();

    MoreFiles.createParentDirectories(this.target.resolve(this.javaPackage.replace('.', '/')));

    javaFile.writeTo(this.target);

    this.callBuilders.values().forEach(b -> {

      this.write(b);

    });

  }

  private void write(final Builder b) {

    final JavaFile javaFile = JavaFile
        .builder(this.baseClassName.packageName(), b.build())
        .build();

    try {
      javaFile.writeTo(this.target);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }

  }

  private CodeBlock javadoc(final BoundOperation op) {
    return CodeBlock.of("GraphQL document query $S\n", op.operationName());
  }

  public void write(final BoundOperation op) {

    // ->

    final String builderName = this.makeBuilderName(op);

    //
    final ClassName returnType = ClassName.get("", builderName);

    // the client API method to invoke and get the query.
    final MethodSpec main = MethodSpec
        .methodBuilder(JavaGenUtils.methodName(op.operationName()))
        .addJavadoc(this.javadoc(op))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(returnType)
        .addParameters(this.params(op))
        .build();

    //
    final Builder builder = TypeSpec
        .classBuilder(builderName)
        .addJavadoc(CodeBlock.of("enclosing class for builder and response type of operation $S\n", op.operationName()))
        .superclass(ClassName.get("", "ZuluCallBuilder"))
        .addModifiers(Modifier.PUBLIC);

    builder.addField(FieldSpec.builder(String.class, "OPDEF", Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
        .addJavadoc(CodeBlock.of("the normalized query\n", op.operationName()))
        .initializer(CodeBlock.of("$S", "\n" + op.normalizedQuery()))
        .build());

    builder.addField(FieldSpec.builder(String.class, "OPTYPE", Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
        .addJavadoc(CodeBlock.of("the normalized query\n", op.operationName()))
        .initializer(CodeBlock.of("$S", op.operationType().operationName()))
        .build());

    builder.addField(FieldSpec.builder(String.class, "OPNAME", Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
        .addJavadoc(CodeBlock.of("the normalized query\n", op.operationName()))
        .initializer(CodeBlock.of("$S", op.operationName()))
        .build());

    this.addOptionalParameters(op, builder, returnType);

    /// add for code generation
    this.callBuilders.put(op, builder);

    this.enclosingClass.addMethod(main);

  }

  private void addOptionalParameters(final BoundOperation op, final Builder builder, final ClassName builderName) {

    op.optionalParameters()
        .forEach(param -> {

          final MethodSpec main = MethodSpec
              .methodBuilder(JavaGenUtils.methodName(param.name()))
              .addJavadoc(CodeBlock.of("provide optional parameter $S\n", param.name()))
              .addModifiers(Modifier.PUBLIC)
              .returns(builderName)
              .addParameter(ParameterSpec.builder(Object.class, "value", Modifier.FINAL).build())
              .addCode(CodeBlock.builder().add("return this;\n").build())
              .build();

          builder.addMethod(main);

        });

  }

  private String makeBuilderName(final BoundOperation op) {
    return JavaGenUtils.className(op.operationName()) + "Call";
  }

  static <T> TypeToken<Single<T>> mapOf(final TypeToken<T> keyType) {
    return new TypeToken<Single<T>>() {}.where(new TypeParameter<T>() {}, keyType);
  }

  Iterable<ParameterSpec> params(final BoundOperation op) {
    final List<ParameterSpec> params = op
        .mandatoryParameters()
        .map(param -> this.makeVar(param))
        .map(pb -> pb.build())
        .collect(Collectors.toList());
    return params;
  }

  ParameterSpec.Builder makeVar(final BoundVariable var) {
    return ParameterSpec.builder(String.class, JavaGenUtils.paramName(var.name()));
  }
}
