package io.zrz.graphql.core.parser;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
@Value.Style(overshadowImplementation = true, allowedClasspathAnnotations = { Override.class })
public abstract class GQLSourceRange {

  public abstract GQLSourceInput input();

  public abstract GQLSourceLocation start();

  public abstract GQLSourceLocation end();

  public abstract String content();

}
