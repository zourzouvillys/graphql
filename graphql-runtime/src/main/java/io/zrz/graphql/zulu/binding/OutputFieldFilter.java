package io.zrz.graphql.zulu.binding;

public interface OutputFieldFilter {

  default OutputFieldFilter forSupertype(JavaBindingType t) {
    return this;
  }

  default boolean shouldInclude(JavaBindingMethodAnalysis m) {
    return true;
  }

}
