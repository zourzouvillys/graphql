package io.zrz.graphql.core.value;

import io.zrz.graphql.core.converter.DynamicListCreationMaterializer;
import io.zrz.graphql.core.converter.DynamicObjectInstanceCreationMaterializer;
import io.zrz.graphql.core.converter.TypeConverter;

public class GQLValueTypeConverter extends TypeConverter {

  public static final GQLValueTypeConverter INSTANCE = new GQLValueTypeConverter();

  public GQLValueTypeConverter() {

    // register all of our conveters.

    this.register(GQLValue.class, Integer.TYPE, value -> value.apply(GQLValueConverters.intConverter()));
    this.register(GQLValue.class, Long.TYPE, value -> value.apply(GQLValueConverters.longConverter()));
    this.register(GQLValue.class, Boolean.TYPE, value -> value.apply(GQLValueConverters.booleanConverter()));

    this.register(GQLValue.class, Integer.class, value -> value.apply(GQLValueConverters.intConverter()));
    this.register(GQLValue.class, Long.class, value -> value.apply(GQLValueConverters.longConverter()));
    this.register(GQLValue.class, Boolean.class, value -> value.apply(GQLValueConverters.booleanConverter()));

    this.register(GQLValue.class, String.class, value -> value.apply(GQLValueConverters.stringConverter()));

    this.register(new DynamicObjectInstanceCreationMaterializer());
    this.register(new DynamicListCreationMaterializer());

  }

  public static GQLValueTypeConverter getInstance() {
    return INSTANCE;
  }

}
