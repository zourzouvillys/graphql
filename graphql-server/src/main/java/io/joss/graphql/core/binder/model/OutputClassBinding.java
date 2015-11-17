package io.joss.graphql.core.binder.model;

import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.binder.reflect.ReflectionUtils;
import io.joss.graphql.core.binder.reflect.TypedClass;
import io.joss.graphql.core.binder.reflect.TypedMethod;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;

/**
 * A class which represents a specific java output model which is a generic model that different implementations can be bound to.
 * 
 * Rather than use clases directly, we abstract the implementation type away, so that other binding types can be used -for example,
 * generating bindings based on an SQL schema directly.
 * 
 * @author theo
 *
 */

public class OutputClassBinding
{

  private TypedClass<?> klass;

  private OutputClassBinding(TypedClass<?> klass)
  {
    this.klass = klass;
  }

  public GQLObjectTypeDeclaration decl()
  {

    GQLObjectTypeDeclaration.Builder b = GQLObjectTypeDeclaration.builder();

    b.name(name());

    for (InterfaceRef iface : ifaces())
    {
      b.iface(iface.ref());
    }

    // add all the fields we can find that are defined directly, and not overriding an interface.
    buildFields(this.klass);

    GQLObjectTypeDeclaration decl = b.build();

    return decl;

  }

  public List<OutputClassField> fields()
  {
    return buildFields(klass);
  }

  /**
   * @return
   * 
   */

  private static List<OutputClassField> buildFields(TypedClass<?> klass)
  {

    List<OutputClassField> fields = new LinkedList<>();

    for (TypedMethod<?> method : klass.getDeclaredMethods())
    {

      if (!shouldInclude(method, klass))
      {
        continue;
      }

      OutputClassField field = buildField(method);

      if (field != null)
      {
        fields.add(field);
      }

    }

    return fields;

  }

  private static boolean shouldInclude(TypedMethod<?> method, TypedClass<?> klass)
  {

    if (method.hasAnnotation(GQLField.class))
    {
      return true;
    }

    return isAutoInclude(klass, method);

  }

  private static boolean isAutoInclude(TypedClass<?> klass, TypedMethod<?> method)
  {

    GQLType type = klass.getAnnotation(GQLType.class);

    if (type == null)
    {
      return false;
    }

    switch (method.method().getName())
    {
      case "toString":
      case "equals":
      case "hashCode":
      case "wait":
      case "notify":
      case "notifyAll":
        return false;
    }

    return type.autoField();

  }

  /**
   * 
   * @param method
   */

  private static OutputClassField buildField(TypedMethod<?> method)
  {
    return OutputClassField.bind(method);
  }

  /**
   * 
   */

  public List<InterfaceRef> ifaces()
  {

    List<InterfaceRef> ifaces = new ArrayList<>();

    for (AnnotatedType aiface : this.klass.rawClass().getAnnotatedInterfaces())
    {
      TypedClass<?> iface = ReflectionUtils.wrap(aiface);
      GQLDeclarationRef decl = GQLDeclarationRef.builder().name(OutputClassBinding.name(iface)).build();
      ifaces.add(new InterfaceRef(decl, iface));
    }

    return ifaces;

  }

  public static String name(TypedClass<?> iface)
  {

    GQLType tinfo = iface.rawClass().getAnnotation(GQLType.class);

    if (tinfo == null)
    {
      throw new RuntimeException(String.format("'%s' must be annotated with @GQLType", iface.toString()));
    }

    try
    {
      if (tinfo.name().isEmpty())
        return iface.rawClass().getSimpleName();
      return tinfo.name();
    }
    catch (Exception ex)
    {
      throw new RuntimeException(iface.toString(), ex);
    }
  }

  /**
   * The GQL name for this class.
   * 
   * @return
   */

  public String name()
  {
    return name(klass);
  }

  public String description()
  {
    return klass.getAnnotation(GQLType.class).description();
  }

  public static OutputClassBinding bind(Class<?> klass)
  {

    TypedClass<Object> type = ReflectionUtils.forClass(klass);

    GQLType tinfo = type.getAnnotation(GQLType.class);

    if (tinfo == null)
    {
      throw new IllegalArgumentException("can't use type without @GQLType on " + type);
    }

    if (tinfo.type() != GQLObjectTypeDeclaration.class)
    {
      throw new IllegalArgumentException("Only output types can be used, not " + tinfo.type());
    }

    return new OutputClassBinding(type);

  }

  public List<OutputClassField> fields(String string)
  {
    return fields().stream().filter(f -> f.name().equals(string)).collect(Collectors.toList());
  }

  /**
   * checks if the given object is an instance of the psueho-class represented by this binding.
   * 
   * @param object
   * @return
   */

  public boolean isInstance(Object object)
  {
    return klass.rawClass().isInstance(object);
  }

  public String toString()
  {
    return String.format("OutputClassBinding(%s)", klass.getType().toString());
  }

}
