package io.joss.graphql.generator.java;

import java.io.OutputStream;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.joss.graphql.client.runtime.RuntimeQuery;
import io.joss.graphql.core.binder.runtime.DataContext;
import io.joss.graphql.core.binder.runtime.DataContexts;
import io.joss.graphql.core.binder.runtime.RelayUtils;
import io.joss.graphql.core.binder.runtime.RelayUtils.RelayConnectionContext;
import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLOperationDefinition;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.doc.GQLVariableDefinition;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import io.joss.graphql.core.lang.GQLTypeVisitors;
import io.joss.graphql.generator.java.codedom.Block;
import io.joss.graphql.generator.java.codedom.Expressions;
import io.joss.graphql.generator.java.codedom.FieldDeclaration;
import io.joss.graphql.generator.java.codedom.MethodDeclaration;
import io.joss.graphql.generator.java.codedom.MethodDeclaration.Builder;
import io.joss.graphql.generator.java.codedom.Modifier;
import io.joss.graphql.generator.java.codedom.SingleVariableDeclaration;
import io.joss.graphql.generator.java.codedom.Statements;
import io.joss.graphql.generator.java.codedom.TypeDeclaration;
import lombok.Getter;

/**
 * Generates java code based on a schema and GQL document.
 * 
 * @author theo
 *
 */

public class JavaClientGenerator
{

  private static final String RUNTIME_PACKAGE = RuntimeQuery.class.getPackage().getName();

  @Getter
  private GQLTypeRegistry registry;

  @Getter
  private GQLDocument document;

  private TypeDeclaration.Builder entryClass;
  private TypeDeclaration.Builder syncStub;

  private io.joss.graphql.generator.java.codedom.TypeDeclaration.Builder asyncStub;

  private GQLTypeDeclaration root;

  public JavaClientGenerator(GQLTypeRegistry registry, GQLTypeDeclaration root, GQLDocument document)
  {
    this.registry = registry;
    this.document = document;
    this.root = root;
  }

  /**
   * Performs the model generation based on the query.
   * 
   * @param out
   */

  public void generate(String clientName, OutputStream out)
  {

    new GQLDocumentValidator(this.registry).validate(this.document);

    this.entryClass = TypeDeclaration.builder().name(clientName);

    entryClass.annotation(String.format("@javax.annotation.Generated(value=\"%s\", date=\"%s\")", getClass().getName(), Instant.now().toString()));

    this.syncStub = TypeDeclaration.builder().name("ClientStub").isInterface(true);

    syncStub.modifier(Modifier.PUBLIC);

    for (GQLOperationDefinition op : document.operations())
    {
      switch (op.type())
      {
        case Query:
          this.generateQuery(op);
          break;
        case Mutation:
        case Subscription:
        default:
          throw new RuntimeException(String.format("Operation '%s' is not yet supported in client generation", op.type()));
      }
    }

    entryClass.bodyDeclaration(syncStub.build());

    new JavaWriter(out).write(entryClass.build());

  }

  /**
   * Each query gets a method which returns a handle to represent it, in the main interface (which is entryClass).
   * 
   * @param query
   * @return
   */

  private void generateQuery(GQLOperationDefinition query)
  {

    // work out the shape of this query. DataContext will help us here.
    DataContext root = DataContexts.build(this.getRegistry(), this.root, GQLSelectedOperation.query(this.document, query));

    //
    // TypeDeclaration.Builder tdb = TypeDeclaration.builder();
    // tdb.modifier(Modifier.STATIC);
    // tdb.modifier(Modifier.PUBLIC);
    // tdb.modifier(Modifier.FINAL);
    // String name = CodeUtils.toTypeName(query.name()) + "Result";
    // tdb.name(name);

    //
    String type = generateResultType(entryClass, root, query);

    /// --- synchronus

    Builder mb = MethodDeclaration.builder();

    mb.type(String.format("%s.RuntimeQuery<%s>", RUNTIME_PACKAGE, type));
    mb.name(query.name());

    for (GQLVariableDefinition var : query.vars())
    {
      SingleVariableDeclaration.Builder svb = SingleVariableDeclaration.builder();
      svb.type(String.format("@%s.GQLParamName(\"%s\") String", RUNTIME_PACKAGE, var.name()));
      svb.name(var.name());
      mb.parameter(svb.build());
    }

    // entryClass.bodyDeclaration(queryShape.build());

    // entryClass.bodyDeclaration(tdb.build());

    syncStub.bodyDeclaration(mb.build());

  }

  private String generateResultType(TypeDeclaration.Builder queryShape, DataContext ctx, GQLOperationDefinition query)
  {

    GQLTypeDeclaration type = ctx.type().apply(GQLTypeVisitors.rootType());

    TypeDeclaration.Builder tdb = TypeDeclaration.builder();

    tdb.isInterface(false);
    tdb.modifier(Modifier.PUBLIC);

    MethodDeclaration.Builder cdb = MethodDeclaration.builder();
    cdb.constructor(true);
    cdb.modifier(Modifier.PUBLIC);
    cdb.name(CodeUtils.toTypeName(ctx.parent() == null ? (query.name() + "Result") : type.name()));

    if (RelayUtils.isRelayNode(ctx))
    {
      // tdb.annotation("@GQLRelayNode");
    }

    if (ctx.declaration().description() != null)
    {
      tdb.javadoc(ctx.declaration().description());
    }

    tdb.annotation("@lombok.ToString");
    tdb.annotation("@lombok.EqualsAndHashCode");

    if (ctx.parent() == null)
    {
      // tdb.annotation("@GQLQuery");
    }
    else
    {
      // tdb.annotation(String.format("@%s.GQLPath(\"" + ctx.path() + "\")", RUNTIME_PACKAGE));
    }
    tdb.name(CodeUtils.toTypeName(ctx.parent() == null ? (query.name() + "Result") : type.name()));

    // if the type is a relay connection, we handle it differenty. Edges are inner classes.
    if (ctx.type().apply(new IsRelayConnectionType()))
    {

      RelayConnectionContext relay = RelayUtils.toRelayConnection(ctx);
      // GQLDeclaration edgeType = extractRelayType(ctx.declaration());
      // tdb.superInterface(String.format("%s.RelayCollection<%s, %s, %s>",
      // RUNTIME_PACKAGE,
      // type.name(),
      // relay.edge().decl().name(),
      // relay.edge().edgeType().name()));

    }

    if (ctx.parent() != null && ctx.parent().type().apply(new IsRelayConnectionType()))
    {

      RelayConnectionContext relay = RelayUtils.toRelayConnection(ctx.parent());

      // tdb.superInterface(String.format("%s.RelayEdge<%s, %s, %s>",
      // RUNTIME_PACKAGE,
      // relay.decl().name(),
      // relay.edge().decl().name(),
      // relay.edge().edgeType().name()));

    }

    Block.Builder blockb = Block.builder();

    List<String> conants = new LinkedList<>();

    for (DataContext child : ctx.children())
    {

      if (!child.type().apply(GQLTypeVisitors.isScalar()))
      {
        // it's not scalar, so need to create a type for it.
        generateResultType(ctx.parent() != null ? queryShape : tdb, child, query);
      }

      //
      FieldDeclaration.Builder fdb = FieldDeclaration.builder();
      fdb.modifier(Modifier.PRIVATE);
      fdb.modifier(Modifier.FINAL);
      fdb.name(child.name());
      fdb.type(child.type().apply(new MethodReturnVisitor(this)));

      tdb.bodyDeclaration(fdb.build());

      //

      MethodDeclaration.Builder getter = MethodDeclaration.builder();

      getter.modifier(Modifier.PUBLIC);

      if (child.fdecl().deprecationReason() != null)
      {
        getter.annotation("@java.lang.Deprecated");
      }

      getter.javadoc(child.fdecl().description());

      getter.type(child.type().apply(new MethodReturnVisitor(this)));
      getter.name(child.name());

      Block.Builder bb = Block.builder();

      bb.statement(Statements.returnValue(Expressions.thisField(child.name())));

      getter.body(bb.build());

      tdb.bodyDeclaration(getter.build());

      ///
      conants.add(child.name());

      cdb.parameter(SingleVariableDeclaration.builder()
          .type(child.type().apply(new MethodReturnVisitor(this)))
          .name(child.name())
          .build());

      blockb.statement(Statements.assign(Expressions.thisField(child.name()), Expressions.simpleName(child.name())));

    }

    cdb.annotation(String.format("@java.beans.ConstructorProperties({ %s })", conants.stream().map(p -> "\"" + p + "\"").collect(Collectors.joining(", "))));
    cdb.body(blockb.build());

    tdb.bodyDeclaration(cdb.build());

    TypeDeclaration td = tdb.build();

    queryShape.bodyDeclaration(td);

    return td.getName();
  }

}
