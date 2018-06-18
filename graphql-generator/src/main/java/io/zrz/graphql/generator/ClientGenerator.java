package io.zrz.graphql.generator;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.StringUtils;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import io.reactivex.Single;
import io.zrz.zulu.schema.model.ModelConnection;
import io.zrz.zulu.schema.model.ModelElement;
import io.zrz.zulu.schema.model.ModelElementVisitor;
import io.zrz.zulu.schema.model.ModelInput;
import io.zrz.zulu.schema.model.ModelObjectType;
import io.zrz.zulu.schema.model.ModelOperation;
import io.zrz.zulu.schema.model.ModelRootType;
import io.zrz.zulu.schema.model.ModelScalarField;

@SuppressWarnings("serial")
public class ClientGenerator {

  private List<ModelOperation> operations = new LinkedList<>();
  private TypeSpec.Builder helloWorld;
  private ClassName baseClassName;

  public ClientGenerator() {

    this.baseClassName = ClassName.get("my.package", "GClient");

    this.helloWorld = TypeSpec
        .interfaceBuilder(baseClassName)
        .addModifiers(Modifier.PUBLIC);

  }

  public ClientGenerator add(ModelOperation op) {
    this.operations.add(op);
    return this;
  }

  public ClientGenerator addAll(Collection<ModelOperation> root) {
    this.operations.addAll(root);
    return this;
  }

  public void write(Appendable out) throws IOException {

    this.operations.forEach(op -> write(out, op));

    JavaFile javaFile = JavaFile.builder(baseClassName.packageName(), this.helloWorld.build())
        .build();

    javaFile.writeTo(out);

  }

  private class RegisteredElement {
    private ClassName typeName;
    private ModelElement element;
    private TypeSpec.Builder builder;
  }

  // private Map<ModelElement, RegisteredElement> types = new HashMap<>();

  private class ElementGeneration implements ModelElementVisitor.FunctionVisitor<RegisteredElement, TypeName> {

    @Override
    public TypeName visitModelRoot(ModelRootType root, RegisteredElement parent) {

      ClassName typeName = baseClassName.nestedClass(StringUtils.capitalize(root.operationName()) + "Result");

      TypeSpec.Builder structType = TypeSpec
          .classBuilder(typeName)
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

      RegisteredElement e = new RegisteredElement();
      e.builder = structType;
      e.typeName = typeName;

      root.selections().forEach(sel -> sel.accept(this, e));

      parent.builder.addType(structType.build());

      return typeName;

    }

    /**
     * create a type with the fields.
     */

    @Override
    public TypeName visitModelObject(ModelObjectType obj, RegisteredElement parent) {

      RegisteredElement e = new RegisteredElement();

      e.typeName = parent.typeName.nestedClass(StringUtils.capitalize(obj.outputName()) + "Value");

      TypeSpec.Builder structType = TypeSpec
          .classBuilder(e.typeName)
          .addJavadoc("results holder")
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

      e.element = obj;
      e.builder = structType;

      obj.fields().forEach((fieldName, childElement) -> {

        TypeName fieldType = childElement.accept(this, e);

        structType.addField(fieldType, fieldName, Modifier.PUBLIC);

      });

      parent.builder.addType(structType.build());

      return e.typeName;

    }

    @Override
    public TypeName visitModelConnection(ModelConnection conn, RegisteredElement parent) {

      ClassName edgeTypeName = parent.typeName.nestedClass(StringUtils.capitalize(conn.outputName()) + "Edge");
      ClassName nodeTypeName = parent.typeName.nestedClass(StringUtils.capitalize(conn.outputName()) + "Node");

      // RegisteredElement e = new RegisteredElement();
      // e.typeName = baseClassName.nestedClass("ConnectionValue");
      // e.element = conn;

      TypeSpec.Builder edgeType = TypeSpec
          .classBuilder(edgeTypeName)
          .addJavadoc("edge value in connection\n")
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

      // conn.edgeSelections().forEach(sel -> sel.);

      TypeSpec.Builder nodeType = TypeSpec
          .classBuilder(nodeTypeName)
          .addJavadoc("node value in connection\n")
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

      // conn.nodeSelections().forEach(System.err::println);

      edgeType.addField(nodeTypeName, "node", Modifier.PUBLIC);

      parent.builder.addType(edgeType.build());
      parent.builder.addType(nodeType.build());

      // e.builder = structType;
      // types.put(conn, e);

      return ParameterizedTypeName.get(ClassName.get("xx.yy", "Connection"), edgeTypeName, nodeTypeName);

    }

    @Override
    public TypeName visitModelScalar(ModelScalarField scalar, RegisteredElement parent) {

      return ClassName.get(String.class);

    }

  }

  void write(Appendable out, ModelOperation op) {

    RegisteredElement e = new RegisteredElement();

    e.typeName = baseClassName;
    e.builder = helloWorld;

    TypeName res = op.element().accept(new ElementGeneration(), e);

    // the client API method to invoke and get the query.
    MethodSpec main = MethodSpec
        .methodBuilder(op.operationName())
        .addJavadoc(javadoc(op))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(makeAsync(res))
        .addParameters(params(op))
        .build();

    //

    helloWorld.addMethod(main);

  }

  private CodeBlock javadoc(ModelOperation op) {
    return CodeBlock.of("GraphQL document query $S\n", op.operationName());
  }

  private TypeName makeAsync(TypeName klass) {
    return ParameterizedTypeName.get(ClassName.get(Single.class), klass);
  }

  static <T> TypeToken<Single<T>> mapOf(TypeToken<T> keyType) {
    return new TypeToken<Single<T>>() {}.where(new TypeParameter<T>() {}, keyType);
  }

  Iterable<ParameterSpec> params(ModelOperation op) {
    return op
        .vars()
        .stream()
        .map(param -> this.makeVar(param))
        .map(pb -> pb.build())
        .collect(Collectors.toList());
  }

  ParameterSpec.Builder makeVar(ModelInput var) {
    return ParameterSpec.builder(String.class, var.name());
  }

}
