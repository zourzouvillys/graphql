package io.zrz.graphql.zulu.api;

import org.eclipse.jdt.annotation.NonNull;

import io.zrz.zulu.types.ZType;

public interface ZuluBeanProperty {

  @NonNull
  String name();

  boolean isRequired();

  String description();

  ZType type();

}
