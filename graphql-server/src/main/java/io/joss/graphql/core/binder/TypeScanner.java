package io.joss.graphql.core.binder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.binder.annotatons.GQLTypeUse;
import io.joss.graphql.core.binder.model.InputClassBinding;
import io.joss.graphql.core.binder.model.InputClassField;
import io.joss.graphql.core.binder.model.InterfaceRef;
import io.joss.graphql.core.binder.model.OutputClassBinding;
import io.joss.graphql.core.binder.model.OutputClassField;
import io.joss.graphql.core.binder.model.OutputClassFieldArg.BeanParamArg;
import io.joss.graphql.core.binder.model.OutputClassFieldArg.InputArg;
import io.joss.graphql.core.binder.reflect.ReflectionUtils;
import io.joss.graphql.core.binder.reflect.SimpleTypedClass;
import io.joss.graphql.core.binder.reflect.TypedClass;
import io.joss.graphql.core.binder.reflect.TypedGetter;
import io.joss.graphql.core.decl.GQLArgumentDefinition;
import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLInputFieldDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.lang.GQLSchemaBuilder;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.core.types.GQLTypes;
import io.joss.graphql.core.value.GQLValue;

/**
 * Scans the POJOs and builds a schema and java handlers up based on the provided model.
 * 
 * @author theo
 *
 */

public class TypeScanner implements BindingProvider
{

  private GQLSchemaBuilder builder;
  private Map<String, GQLDeclaration> decls = new HashMap<>();

  // references we have seen but not directly registered.
  private Set<Type> refs = new HashSet<>();

  private Map<Class<?>, OutputClassBinding> meta = new HashMap<>();
  private Map<GQLDeclaration, OutputClassBinding> bindings = new HashMap<>();

  public TypeScanner(GQLSchemaBuilder builder)
  {
    this.builder = builder;
  }

  /**
   * Scans a type for references to other types.
   *
   * @param type
   */

  public void scan(Type type)
  {

    String name = this.calculateName(type);

    if (this.decls.containsKey(name))
    {
      return;
    }

    this.refs.add(type);

  }

  /**
   * Explicitly adds this type and scans it's children, even if it's not annotated with @GQLType. Used when it's implicitly used as a type,
   * e.g as a return type on a @GQLField.
   *
   * Ideally, this should be avoided where possible in favor of explicitly registering.
   *
   * @param root
   * @return
   *
   */

  public GQLDeclaration add(Class<?> type)
  {

    String name = calculateName(type);

    TypedClass<?> klass = ReflectionUtils.forClass(type);

    if (klass.getAnnotation(GQLType.class) == null)
    {
      throw new RuntimeException(String.format("Missing @GQLType on %s", type.getName()));
    }

    if (klass.getAnnotation(GQLType.class).type() == GQLObjectTypeDeclaration.class)
    {
      return add(OutputClassBinding.bind(type), name);
    }
    else if (klass.getAnnotation(GQLType.class).type() == GQLInputTypeDeclaration.class)
    {
      return add(InputClassBinding.bind(type), name);
    }
    else
    {
      throw new RuntimeException("Don't know how to generate java binding for " + type);
    }

  }

  /**
   * creates an input type declaration from an input binding model.
   * 
   * @param binding
   * @param name
   * @return
   */

  private GQLInputTypeDeclaration add(InputClassBinding binding, String name)
  {

    if (decls.containsKey(name))
    {
      throw new IllegalStateException();
    }

    GQLInputTypeDeclaration.Builder b = GQLInputTypeDeclaration.builder();

    b.name(name);

    for (InputClassField getter : binding.getters())
    {

      GQLInputFieldDeclaration.Builder fb = GQLInputFieldDeclaration.builder();

      fb.name(getter.name());
      fb.type(ref(getter.type()));

      b.field(fb.build());

    }

    GQLInputTypeDeclaration decl = b.build();

    builder.add(decl);
    this.decls.put(name, decl);

    return decl;

  }

  /**
   * adds the given type with the specified name.
   *
   * If the given type is already used, an exception will be thrown.
   *
   * @param root
   * @param name
   * @return
   */

  private GQLDeclaration add(OutputClassBinding binding, String name)
  {

    if (decls.containsKey(name))
    {
      throw new IllegalStateException();
    }

    GQLObjectTypeDeclaration.Builder b = GQLObjectTypeDeclaration.builder();

    b.name(binding.name());

    b.description(binding.description());

    for (InterfaceRef iface : binding.ifaces())
    {
      b.iface(iface.ref());
      scan(iface.type().rawClass());
    }

    for (OutputClassField field : binding.fields())
    {

      try
      {

        GQLParameterableFieldDeclaration.Builder fb = GQLParameterableFieldDeclaration.builder();

        fb.name(field.name());
        fb.type(ref(field.returnType()));

        fb.deprecationReason(field.isDeprecated() ? "marked for deprecation" : null);
        fb.description(field.description());

        for (InputArg arg : field.inputParams())
        {

          GQLArgumentDefinition.Builder ab = GQLArgumentDefinition.builder();

          ab.name(arg.name());
          ab.type(ref(arg.type()));
          ab.defaultValue(makeDefaultValue(ref(arg.type()), arg.defaultValue()));

          fb.arg(ab.build());

        }

        for (BeanParamArg bean : field.beanParams())
        {

          for (TypedGetter<?> getter : bean.param().type().getters())
          {

            GQLArgumentDefinition.Builder ab = GQLArgumentDefinition.builder();

            ab.name(getter.name());
            ab.type(ref(getter.type()));
            ab.defaultValue(makeDefaultValue(ref(getter.type()), getter.defaultValue()));

            fb.arg(ab.build());

          }

        }

        b.field(fb.build());

      }
      catch (Exception ex)
      {
        throw new ObjectFieldException(binding, field, ex);
      }

    }

    GQLObjectTypeDeclaration decl = b.build();

    builder.add(decl);

    bindings.put(decl, binding);
    decls.put(name, decl);

    return decl;

  }

  /**
   * Calculates the default value.
   *
   * @param param
   * @param type
   * @param defaultValue
   * @return
   */

  private GQLValue makeDefaultValue(GQLTypeReference type, String defaultValue)
  {
    if (defaultValue == null || defaultValue.isEmpty())
    {
      return null;
    }
    return type.apply(new DefaultValueGenerator(type, defaultValue));
  }

  /**
   * creates a reference to the given type.
   *
   * @param type
   * @return
   */

  private GQLTypeReference ref(TypedClass<?> type)
  {

    if (type.hasAnnotation(GQLTypeUse.class))
    {
      String typeId = type.getAnnotation(GQLTypeUse.class).value();
      return makeNonNull(type, GQLTypes.concreteTypeRef(typeId));
    }

    if (type.isCollection())
    {
      return makeNonNull(type, GQLTypes.listOf(ref(type.asCollection().componentType())));
    }

    return makeNonNull(type, ref(((SimpleTypedClass<?>) type).getType()));

  }

  private GQLTypeReference makeNonNull(TypedClass<?> type, GQLTypeReference ref)
  {
    if (type.hasAnnotation(GQLNonNull.class))
    {
      return GQLTypes.nonNull(ref);
    }
    return ref;
  }

  private GQLTypeReference ref(Class<?> klass)
  {

    // handle builtin types.
    if (klass.equals(Integer.TYPE) || klass.equals(Long.TYPE))
    {
      return GQLTypes.nonNull(GQLTypes.intType());
    }
    else if (klass.equals(Integer.class) || klass.equals(Long.class))
    {
      return GQLTypes.intType();
    }
    else if (klass.equals(Boolean.TYPE))
    {
      return GQLTypes.nonNull(GQLTypes.booleanType());
    }
    else if (klass.equals(Boolean.class))
    {
      return GQLTypes.booleanType();
    }
    else if (klass.equals(String.class))
    {
      return GQLTypes.stringType();
    }

    scan(klass);

    return GQLTypes.typeRef(calculateName(klass));

  }

  /**
   * calculates the name for a java class.
   */

  public String calculateName(Type klass)
  {
    if (klass instanceof Class<?>)
    {
      return OutputClassBinding.name(ReflectionUtils.forClass((Class<?>) klass));
    }
    throw new RuntimeException("Unable to generate name for: " + klass);
  }

  /**
   * adds any types which we've referenced but were not explicitly registered.
   */

  public void finish()
  {

    while (!this.refs.isEmpty())
    {

      Type todo = refs.iterator().next();
      this.refs.remove(todo);

      String name = this.calculateName(todo);

      if (this.decls.containsKey(name))
      {
        continue;
      }

      add((Class<?>) todo);

    }

  }

  public <T> OutputClassBinding meta(Class<T> handler)
  {
    return meta.get(handler);
  }

  /**
   * fetches the object for the given type.
   * 
   * @param type
   * @return
   */

  public OutputClassBinding meta(GQLDeclaration type)
  {

    for (OutputClassBinding decl : bindings.values())
    {
      if (decl.name().equals(type.name()))
      {
        return decl;
      }
    }

    throw new IllegalArgumentException();
  }

  /**
   * Performs a binding, loading the given class as the root query.
   * 
   * @param klass
   * @return
   */

  public static TypeBindingResult bind(Class<?> klass)
  {

    GQLSchemaBuilder builder = new GQLSchemaBuilder();

    builder.add(GQLTypes.builtins());

    TypeScanner scanner = new TypeScanner(builder);

    GQLDeclaration root = scanner.add(klass);

    scanner.finish();

    GQLTypeRegistry reg = builder.build();

    return new TypeBindingResult(reg, scanner, root, null);

  }

  public static TypeBindingResult bind(Class<?> query, Class<?> mutate)
  {

    GQLSchemaBuilder builder = new GQLSchemaBuilder();

    builder.add(GQLTypes.builtins());

    TypeScanner scanner = new TypeScanner(builder);

    GQLDeclaration queryRoot = scanner.add(query);
    GQLDeclaration mutateRoot = scanner.add(mutate);

    scanner.finish();

    GQLTypeRegistry reg = builder.build();

    return new TypeBindingResult(reg, scanner, queryRoot, mutateRoot);

  }

}
