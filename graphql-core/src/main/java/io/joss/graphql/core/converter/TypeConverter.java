package io.joss.graphql.core.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;

import lombok.Value;

/**
 * Helper class which provides an API for converting between different types - both primitive and collections/arrays.
 *
 * Although there are other libraries out there, we need something that handles {@link AnnotatedType} and generics rather than plain {#link
 * Class<?> instances}.
 *
 * @author theo
 *
 */

public class TypeConverter
{

  public static TypeConverter JRE_COVNERTER = new TypeConverter().registerBuiltins();

  public static TypeConverter defaultConverter()
  {
    return JRE_COVNERTER;
  }

  @Value
  private static class Converstion
  {
    private final Type inputClass;
    private final Type outputClass;
  }

  private Map<Converstion, TypeMapper<Object, Object>> registered = new HashMap<>();
  private Map<Type, TypeMaterializer<? super Object>> materializers = new HashMap<>();
  private Map<Class<?>, Object> defaultValues = new HashMap<>();

  public TypeConverter()
  {
  }

  private TypeConverter registerBuiltins()
  {

    register(String.class, Integer.class, Integer::new);
    register(Integer.class, String.class, val -> val.toString());

    register(String.class, Long.class, Long::new);
    register(Long.class, String.class, val -> val.toString());

    register(String.class, Double.class, Double::new);
    register(Double.class, String.class, val -> val.toString());

    register(String.class, Float.class, Float::new);
    register(Float.class, String.class, val -> val.toString());

    register(String.class, Boolean.class, Boolean::new);
    register(Boolean.class, String.class, val -> val.toString());

    register(String.class, BigInteger.class, BigInteger::new);
    register(BigInteger.class, String.class, val -> val.toString());

    register(String.class, Instant.class, val -> Instant.parse(val));
    register(Instant.class, String.class, val -> val.toString());

    // if a null input is provided, these are used for return value.
    registerDefaultValue(Long.TYPE, 0L);
    registerDefaultValue(Integer.TYPE, 0);
    registerDefaultValue(Short.TYPE, (short) 0);
    registerDefaultValue(Byte.TYPE, (byte) 0);
    registerDefaultValue(Boolean.TYPE, (boolean) false);
    registerDefaultValue(Character.TYPE, (char) 0);
    registerDefaultValue(Float.TYPE, (float) 0);
    registerDefaultValue(Double.TYPE, (double) 0);

    return this;

  }

  /**
   * 
   */

  private <T> void registerDefaultValue(Class<T> type, Object value)
  {
    this.defaultValues.put(type, value);
  }

  /**
   * Registers a type mapper that can convert from one type to another.
   */

  @SuppressWarnings("unchecked")
  public <I extends Object, O extends Object> void register(Class<I> inputClass, Class<O> outputClass, TypeMapper<I, O> mapper)
  {
    this.registered.put(new Converstion(inputClass, outputClass), (TypeMapper<Object, Object>) mapper);
  }

  /**
   * register a type materializer.
   * 
   * @param materializer
   */

  @SuppressWarnings("unchecked")
  public <T> void register(TypeMaterializer<T> materializer)
  {
    AnnotatedType supported = TypeUtils.getParamterOfInterface(materializer.getClass(), TypeMaterializer.class, 0);
    if (supported == null)
    {
      throw new IllegalArgumentException("materializer must implement TypeMaterializer<T>");
    }
    this.materializers.put(supported.getType(), (TypeMaterializer<? super Object>) materializer);
  }

  /**
   * Performs a converstion from the given input value to an output type.
   * 
   * @param input
   * @param outputClass
   * @return
   * @throws Exception
   */

  public <I extends Object, O extends Object> O convert(I input, Type outputClass, Annotation[] annotations)
  {
    return _convert(input, outputClass, annotations);
  }

  @SuppressWarnings("unchecked")
  public <I extends Object, O extends Object> O _convert(I input, Type outputClass, Annotation[] annotations)
  {

    // null input always results in null output.
    if (input == null)
    {
      return defaultValue(outputClass, annotations);
    }

    TypeMapper<? super Object, ? super Object> converter = registered.get(new Converstion(input.getClass(), outputClass));

    if (converter != null)
    {
      return (O) converter.convert(input);
    }

    // check out the superclasses.

    for (TypeToken<?> t : TypeToken.of(input.getClass()).getTypes())
    {
      
      converter = registered.get(new Converstion(t.getRawType(), outputClass));

      if (converter != null)
      {
        return (O) converter.convert(input);
      }

    }


    // ---

    // type the type materializers.

    for (Map.Entry<Type, TypeMaterializer<? super Object>> materializer : this.materializers.entrySet())
    {

      if (!materializer.getKey().equals(input.getClass()))
      {
        continue;
      }

      // AnnotatedType supports = TypeUtils.getInterfaceType(materializer.getClass(), TypeMaterializer.class);

      Object val = materializer.getValue().convert(this, input, outputClass, annotations);

      if (val != null)
      {
        return (O) val;
      }

    }

    // ---

    throw new RuntimeException(String.format("No converter from '%s' to '%s'", input.getClass(), outputClass));

  }

  /**
   * provides the default value to use for the specified output type.
   * 
   * @param outputClass
   * @param annotations
   * @return
   */

  @SuppressWarnings("unchecked")
  private <O> O defaultValue(Type outputType, Annotation[] annotations)
  {
    return (O) this.defaultValues.get(outputType);
  }

  public <I, O> O convert(I input, Type outputClass)
  {
    return convert(input, outputClass, new Annotation[0]);
  }

  public <I, O> O convert(I input, AnnotatedType outputClass)
  {
    return convert(input, outputClass.getType(), outputClass.getAnnotations());
  }

  public <I, O> O convert(I input, Class<O> outputClass)
  {
    return convert(input, outputClass, new Annotation[0]);
  }

}
