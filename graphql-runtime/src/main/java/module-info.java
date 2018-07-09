module zrz.graphql.runtime {

  requires slf4j.api;

  requires zrz.graphql.core;
  requires zrz.graphql.annotations;

  exports io.zrz.graphql.zulu;
  exports io.zrz.graphql.zulu.api;
  exports io.zrz.graphql.zulu.executable;
  exports io.zrz.graphql.zulu.schema;
  exports io.zrz.graphql.zulu.engine;
  exports io.zrz.graphql.zulu.plugins;

  requires zrz.graphql.zulu;

  requires com.google.common;

}
