package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.doc.GQLDocumentManager;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder;
import io.zrz.graphql.zulu.plugins.ZuluPlugin;

public class ZuluEngineBuilder {

  private List<ZuluPlugin> plugins = new LinkedList<>();
  private ExecutableSchemaBuilder schema = ExecutableSchema.builder();
  private GQLDocumentManager docmgr = null;

  public ZuluEngineBuilder queryRoot(Type type) {
    return addRoot(GQLOpType.Query, type);
  }

  public ZuluEngineBuilder mutationRoot(Type type) {
    return addRoot(GQLOpType.Mutation, type);
  }

  public ZuluEngineBuilder subscriptionRoot(Type type) {
    return addRoot(GQLOpType.Subscription, type);
  }

  public ZuluEngineBuilder addRoot(GQLOperationType op, Type type) {
    schema.setRootType(op, type);
    return this;
  }

  public ZuluEngineBuilder plugin(ZuluPlugin plugin) {
    plugin.onPluginRegistered(this);
    this.plugins.add(plugin);
    return this;
  }

  public ZuluEngineBuilder schema(Consumer<ExecutableSchemaBuilder> adapter) {
    adapter.accept(schema);
    return this;
  }

  public ZuluEngineBuilder documentManager(GQLDocumentManager docmgr) {
    this.docmgr = docmgr;
    return this;
  }

  public ZuluEngine build() {

    plugins.forEach(plugin -> plugin.onBuilding(this));

    ZuluEngine engine = new ZuluEngine(schema.build(false), this.docmgr);

    plugins.forEach(plugin -> plugin.onEngine(engine));

    return engine;
  }
}
