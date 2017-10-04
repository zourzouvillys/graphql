package io.zrz.graphql.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.utils.GQLUtils;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValues;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * An immutable registered GQL output type, with handlers.
 * 
 * @author theo
 *
 */

@ToString
@EqualsAndHashCode
public final class GraphQLInputType
{

  /**
   * arguments that can be provided to fields.
   */

  public static final class FieldBuilder
  {

    private String name;
    private GQLTypeReference type;
    private FieldHandler handler;
    private String description;

    public FieldBuilder(String name)
    {
      name(name);
    }

    public FieldBuilder name(String name)
    {
      this.name = GQLUtils.normalize(name);
      return this;
    }

    /**
     * The type of this field.
     */

    public FieldBuilder type(GQLTypeReference type)
    {
      this.type = type;
      return this;
    }

    public FieldBuilder handler(FieldHandler handler)
    {
      this.handler = handler;
      return this;
    }

    public FieldBuilder description(String description)
    {
      this.description = description;
      return this;
    }

  }

  /**
   * Handles the building of a given type.
   */

  public static final class Builder
  {

    private List<FieldBuilder> fields = new LinkedList<>();
    private String name;

    public Builder name(String name)
    {
      this.name = GQLUtils.normalize(name);
      return this;
    }

    /**
     * Adds a field with the given name.
     */

    public FieldBuilder addField(String name)
    {
      FieldBuilder builder = new FieldBuilder(name);
      this.fields.add(builder);
      return builder;
    }

    /**
     * Converts into an immutable instance, with all references resolved.
     */

    public GraphQLInputType build()
    {
      return new GraphQLInputType(this);
    }

  }

  /**
   * Generates a builder initialized with the result of performing reflection on the given class.
   * 
   * @param klass
   * @return
   */

  public static final Builder builder(GraphQLInputType clone)
  {
    return new Builder();
  }

  public static Builder builder(String typeName)
  {
    return new Builder().name(typeName);
  }

  public static Builder builder()
  {
    return new Builder();
  }

  // ----

  @ToString
  @EqualsAndHashCode
  public static final class Field
  {

    private final String name;
    private final GQLTypeReference returnType;
    // note: handler being null will just always result in null being returned.
    // so, this must fail if the type is GQLNonNull.
    private FieldHandler handler;
    private String description;

    private Field(FieldBuilder fb)
    {

      this.name = fb.name;
      this.handler = fb.handler;
      this.returnType = fb.type;
      this.description = fb.description;

      // validate
      Preconditions.checkState(GQLUtils.isValidTypeName(this.name), "invalid field name", this.name);
      Preconditions.checkState(this.returnType != null, "invalid return type", this.returnType);
      Set<String> allItems = new HashSet<>();

    }

    public String name()
    {
      return this.name;
    }

    public String description()
    {
      return this.description;
    }

    public @NonNull GQLTypeReference returnType()
    {
      return this.returnType;
    }

    public FieldHandler handler()
    {
      return this.handler;
    }

  }

  // the output type name.
  private final String name;
  private final ImmutableList<Field> fields;

  private GraphQLInputType(Builder builder)
  {

    Preconditions.checkState(GQLUtils.isValidTypeName(builder.name), "input type name", builder.name);

    this.name = builder.name;

    List<Field> fields = new ArrayList<>(builder.fields.size());

    for (FieldBuilder fb : builder.fields)
    {
      fields.add(new Field(fb));
    }

    this.fields = ImmutableList.copyOf(fields);

    Set<String> allItems = new HashSet<>();
    Set<String> dups = fields.stream().map(arg -> arg.name.toLowerCase()).filter(n -> !allItems.add(n)).collect(Collectors.toSet());
    Preconditions.checkState(dups.isEmpty(), "duplicate field names", dups);

  }

  public String name()
  {
    return this.name;
  }

  public List<Field> fields()
  {
    return this.fields;
  }

  public Field field(String string)
  {
    return fields().stream().filter(p -> p.name().equals(string)).findAny().orElse(null);
  }

}
