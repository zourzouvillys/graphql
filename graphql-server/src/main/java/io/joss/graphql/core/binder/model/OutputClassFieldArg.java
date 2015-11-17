package io.joss.graphql.core.binder.model;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLDefaultValue;
import io.joss.graphql.core.binder.reflect.TypedClass;
import io.joss.graphql.core.binder.reflect.TypedParameter;

public class OutputClassFieldArg
{

  public static class ContextArg extends OutputClassFieldArg
  {

    private TypedParameter<?> param;

    public ContextArg(TypedParameter<?> param)
    {
      this.param = param;
    }

    public TypedParameter<?> param()
    {
      return this.param;
    }

  }

  public static class BeanParamArg extends OutputClassFieldArg
  {


    private TypedParameter<?> param;

    public BeanParamArg(TypedParameter<?> param)
    {
      this.param = param;
    }

    public TypedParameter<?> param()
    {
      return this.param;
    }

    
  }

  public static class InputArg extends OutputClassFieldArg
  {

    private TypedParameter<?> param;

    public InputArg(TypedParameter<?> param)
    {
      this.param = param;
    }

    public String name()
    {
      GQLArg ant = param.getAnnotation(GQLArg.class);
      if (ant != null)
      {
        return ant.value();
      }
      return param.getName();
    }

    public String defaultValue()
    {
      GQLDefaultValue ant = param.getAnnotation(GQLDefaultValue.class);
      if (ant != null)
      {
        return ant.value();
      }
      return null;
    }

    public TypedClass<?> type()
    {
      return param.type();
    }

  }

  public static OutputClassFieldArg context(TypedParameter<?> param)
  {
    return new ContextArg(param);
  }

  public static OutputClassFieldArg beanParam(TypedParameter<?> param)
  {
    return new BeanParamArg(param);
  }

  public static InputArg inputArg(TypedParameter<?> param)
  {
    return new InputArg(param);
  }

}
