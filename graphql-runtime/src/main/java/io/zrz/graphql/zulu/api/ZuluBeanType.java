package io.zrz.graphql.zulu.api;

import java.util.List;

import io.zrz.zulu.types.ZStructType;

public interface ZuluBeanType extends ZStructType {

  List<? extends ZuluBeanProperty> properties();

}
