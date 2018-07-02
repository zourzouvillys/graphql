package io.zrz.graphql.plugins.jackson;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.zrz.graphql.zulu.api.ZuluTypeBinder;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZPrimitiveScalarType;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.types.ZType;
import io.zrz.zulu.types.ZTypeUse;

/**
 *
 * @author theo
 *
 */

public class ZuluJacksonTypeBinder implements ZuluTypeBinder {

  private final ObjectMapper mapper;

  public ZuluJacksonTypeBinder(final Module... modules) {
    this.mapper = new ObjectMapper()
        .registerModule(new ParameterNamesModule())
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .registerModules(modules);
  }

  public ZuluJacksonTypeBinder(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  private class JZBeanProperty implements ZField {

    private final BeanProperty property;

    public JZBeanProperty(final BeanProperty property) {
      this.property = property;
    }

    @Override
    public @NonNull String fieldName() {
      return Objects.requireNonNull(this.property.getName());
    }

    @Override
    public boolean isOptional() {
      return !this.property.isRequired();
    }

    public String description() {
      return this.property.getMetadata().getDescription();
    }

    public ZType type() {

      final List<JZBeanProperty> properties = new LinkedList<>();

      final FormatVisitor visitor = new FormatVisitor(properties);

      try {

        ZuluJacksonTypeBinder.this.mapper.acceptJsonFormatVisitor(this.property.getType(), visitor);

      }
      catch (final JsonMappingException e) {

        throw new RuntimeException(e);

      }

      if (visitor.type != null) {
        return visitor.type;
      }

      return new JZBeanType(properties);

    }

    @Override
    public String toString() {
      return this.property.toString();
    }

    @Override
    public ZTypeUse fieldType() {
      return new ZTypeUse(this.type());
    }

  }

  class JZBeanType implements ZStructType {

    private final List<JZBeanProperty> properties;

    public JZBeanType(final List<JZBeanProperty> properties) {
      this.properties = properties;
    }

    public List<JZBeanProperty> properties() {
      return this.properties;
    }

    @Override
    public String toString() {
      return this.properties.toString();
    }

    @Override
    public Map<String, ? extends ZField> fields() {
      return this.properties.stream().collect(Collectors.toMap(JZBeanProperty::fieldName, e -> (ZField) e));
    }

  }

  /**
   *
   */

  @Override
  public ZType scan(final Type type) {
    try {
      final List<JZBeanProperty> properties = new LinkedList<>();
      this.mapper.acceptJsonFormatVisitor(this.mapper.constructType(type), new FormatVisitor(properties));
      return new JZBeanType(properties);
    }
    catch (final JsonMappingException e) {
      throw new RuntimeException(e);
    }
  }

  private class ObjectVisitor extends JsonObjectFormatVisitor.Base {

    private final List<JZBeanProperty> properties;

    public ObjectVisitor(final List<JZBeanProperty> properties) {
      this.properties = properties;
    }

    @Override
    public void property(final BeanProperty writer) throws JsonMappingException {
      this.properties.add(new JZBeanProperty(writer));
    }

    @Override
    public void optionalProperty(final BeanProperty writer) throws JsonMappingException {
      this.properties.add(new JZBeanProperty(writer));
    }

    @Override
    public void property(final String name, final JsonFormatVisitable handler, final JavaType propertyTypeHint) throws JsonMappingException {
      throw new IllegalArgumentException("unable to map");
    }

    @Override
    public void optionalProperty(final String name, final JsonFormatVisitable handler, final JavaType propertyTypeHint) throws JsonMappingException {
      throw new IllegalArgumentException("unable to map");
    }

  }

  private class FormatVisitor extends JsonFormatVisitorWrapper.Base {

    private final List<JZBeanProperty> properties;
    private ZPrimitiveScalarType type;

    public FormatVisitor(final List<JZBeanProperty> properties) {
      this.properties = properties;
    }

    /**
     * an object type.
     */

    @Override
    public JsonObjectFormatVisitor expectObjectFormat(final JavaType type) throws JsonMappingException {
      return new ObjectVisitor(this.properties);
    }

    /**
     * an array of another type.
     */

    @Override
    public JsonArrayFormatVisitor expectArrayFormat(final JavaType type) throws JsonMappingException {
      throw new IllegalArgumentException("unable to map");
    }

    /**
     * a string.
     */

    @Override
    public JsonStringFormatVisitor expectStringFormat(final JavaType type) throws JsonMappingException {
      this.type = ZPrimitiveScalarType.STRING;
      // throw new IllegalArgumentException("unable to map type " + type.getRawClass());
      return null;
    }

    /**
     * a numeric
     */

    @Override
    public JsonNumberFormatVisitor expectNumberFormat(final JavaType type) throws JsonMappingException {
      throw new IllegalArgumentException("unable to map");
    }

    /**
     * an interger
     */

    @Override
    public JsonIntegerFormatVisitor expectIntegerFormat(final JavaType type) throws JsonMappingException {
      this.type = ZPrimitiveScalarType.INT;
      return null;
    }

    /**
     * boolean
     */

    @Override
    public JsonBooleanFormatVisitor expectBooleanFormat(final JavaType type) throws JsonMappingException {
      throw new IllegalArgumentException("unable to map");
    }

    /**
     * hmmm
     */

    @Override
    public JsonNullFormatVisitor expectNullFormat(final JavaType type) throws JsonMappingException {
      throw new IllegalArgumentException("unable to map");
    }

    /**
     * accepts anything
     */

    @Override
    public JsonAnyFormatVisitor expectAnyFormat(final JavaType type) throws JsonMappingException {
      throw new IllegalArgumentException("unable to map");
    }

    /**
     * expect a map of a typed key/value.
     */

    @Override
    public JsonMapFormatVisitor expectMapFormat(final JavaType type) throws JsonMappingException {
      throw new IllegalArgumentException("unable to map");
    }

  }

}
