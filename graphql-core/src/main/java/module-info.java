module zrz.graphql.core {

  requires org.immutables.value;
  requires org.eclipse.jdt.annotation;

  exports io.zrz.graphql.core;
  exports io.zrz.graphql.core.decl;
  exports io.zrz.graphql.core.doc;
  exports io.zrz.graphql.core.value;
  exports io.zrz.graphql.core.types;
  exports io.zrz.graphql.core.parser;
  exports io.zrz.graphql.core.lang;
  exports io.zrz.graphql.core.runtime;
  exports io.zrz.graphql.core.utils;

}
