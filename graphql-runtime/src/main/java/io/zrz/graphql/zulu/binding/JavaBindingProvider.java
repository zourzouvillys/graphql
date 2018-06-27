package io.zrz.graphql.zulu.binding;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.spi.ExtensionGenerator;

/**
 * generates type extensions directly for a single view from java classes/models which can then be populated into a
 * runtime.
 *
 * the result of reflection on the provided classes is a standard interface for reflecting (for schema generation) and
 * executing.
 *
 * the java types must be mapped to names in the schema. this can be done automatically under most circumstances,
 * although may be overridden as needed.
 *
 * @author theo
 *
 */

public class JavaBindingProvider {

  private static Logger log = LoggerFactory.getLogger(JavaBindingProvider.class);

  private static final Map<TypeToken<?>, JavaBindingType> cache = new HashMap<>();
  private final Map<TypeToken<?>, JavaBindingType> uses = new HashMap<>();
  private final Multimap<TypeToken<?>, JavaBindingMethodAnalysis> extensions = LinkedHashMultimap.create();
  private final Map<TypeToken<?>, JavaBindingType> types = new HashMap<>();
  private final Map<TypeToken<?>, JavaBindingType> interfaces = new HashMap<>();
  private final Map<TypeToken<?>, String> unions = new HashMap<>();

  private final Set<ExtensionGenerator> extensionGenerators = new HashSet<>();

  /**
   * registers a class as a zulu type.
   */

  public void registerType(final Type type) {
    this.registerType(TypeToken.of(type));
  }

  /**
   * registers a type as an interface.
   */

  public void registerInterface(final Type type) {
    this.registerInterface(TypeToken.of(type));
  }

  /**
   * registers a type (potentially resolved) as a zulu type. no methods will be added directly by registering a type.
   *
   * a type may be registered multiple times under different names, however a name may only be registered once.
   *
   * @return
   *
   */

  public JavaBindingType registerType(final TypeToken<?> typeToken) {
    log.trace("scanning type [{}]", typeToken);
    return this.types.computeIfAbsent(typeToken, _token -> new JavaBindingType(this, _token));
  }

  /**
   * registers a type to expose as a zulu interface. any concrete type implementing this java class (through extends or
   * implements) will implement this interface.
   *
   * @param typeToken
   * @param typeNames
   */

  public void registerInterface(final TypeToken<?> typeToken, final String... typeNames) {

    log.info("registering interface [{}] as {}", typeToken, Arrays.toString(typeNames));

    final JavaBindingType type = this.interfaces.computeIfAbsent(typeToken, _token -> new JavaBindingType(this, _token));

    // add each of the names to the types which implement it.
    this.types.values().forEach(t -> t.processInterface(type));

  }

  /**
   * registers a type as a union. any type use which declares this as it's return value will use the given type name
   * instead.
   */

  public void registerUnion(final Type type, final String unionName) {
    this.unions.put(TypeToken.of(type), unionName);
  }

  /**
   * called to use (but not export) the given concrete type.
   *
   * @param lookup
   * @return
   */

  public JavaBindingType include(final TypeToken<?> type) {
    return cache.computeIfAbsent(type, _t -> new JavaBindingType(this, type));
  }

  /**
   * registers an extension class.
   */

  public void registerExtension(final Class<?> klass) {
    final JavaBindingType ext = this.include(TypeToken.of(klass));
    ext.analysis()
        .extensionFields()
        .peek(e -> log.trace("adding extension for {}: {}", e.receiverType(), e))
        .forEach(a -> this.extensions.put(a.receiverType(), a));
  }

  /**
   * returns the extension fields for the given token type.
   *
   * this includes not just the exact types, but also any other types that it can match.
   *
   */

  public Stream<JavaOutputField> extensionsFor(final TypeToken<?> type) {
    log.trace("looking up in {} extensions for {}: {}", this.extensions.size(), type, type.getTypes());
    return Stream.concat(
        Stream.concat(Stream.of(TypeToken.of(Object.class)), type.getTypes().stream()).distinct().flatMap(e -> this.extensions.get(e).stream()).distinct(),
        this.extensionGenerators.stream().flatMap(gen -> gen.generateExtensions(type)))
    // .peek(f -> log.trace("extending {} with {}", type, f))
    ;

  }

  /**
   * registers a dynamic extension generator which will be given a change to generate extensions.
   */

  public void extensionGenerator(final ExtensionGenerator ext) {
    this.extensionGenerators.add(ext);
  }

  /**
   * all types exposed by the bindings. these will all need to have mappings in some form to external APIs.
   */

  public Stream<TypeToken<?>> types() {
    return ImmutableSet
        .<TypeToken<?>>builder()
        .addAll(this.types.keySet())
        .addAll(this.interfaces.keySet())
        .addAll(this.unions.keySet())
        .build()
        .stream();
  }

  public Stream<TypeToken<?>> usedTypes() {
    return ImmutableSet
        .<TypeToken<?>>builder()
        .addAll(this.types.keySet())
        .addAll(this.interfaces.keySet())
        .addAll(this.unions.keySet())
        .addAll(this.uses.keySet())
        .build()
        .stream()
        .distinct()
        .sorted((a, b) -> a.toString().compareTo(b.toString()));
  }

}
