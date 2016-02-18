package io.joss.graphql.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.utils.GQLUtils;
import io.joss.graphql.core.value.GQLStringValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValues;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An immutable registered GQL output type, with handlers.
 * 
 * @author theo
 *
 */

@ToString
@EqualsAndHashCode
public final class GraphQLOutputType
{

  /**
   * arguments that can be provided to fields.
   */

  public static final class ArgBuilder
  {

    private String name;
    private GQLDeclaration type;
    private GQLStringValue defaultValue;

    ArgBuilder(String name)
    {
      name(name);
    }

    public ArgBuilder name(String name)
    {
      this.name = GQLUtils.normalize(name);
      return this;
    }

    public ArgBuilder type(GQLDeclaration type)
    {
      this.type = type;
      return this;
    }

    public ArgBuilder defaultValue(String value)
    {
      if (value == null)
      {
        this.defaultValue = null;
      }
      else
      {
        this.defaultValue = GQLValues.stringValue(value);
      }
      return this;
    }

  }

  public static final class FieldBuilder
  {

    private String name;
    private GQLDeclaration type;
    private List<ArgBuilder> args = new LinkedList<>();
    private FieldHandler handler;

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

    public FieldBuilder type(GQLDeclaration type)
    {
      this.type = type;
      return this;
    }

    public ArgBuilder newArg(String name)
    {
      ArgBuilder builder = new ArgBuilder(name);
      this.args.add(builder);
      return builder;
    }

    public FieldBuilder handler(FieldHandler handler)
    {
      this.handler = handler;
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

    public GraphQLOutputType build()
    {
      return new GraphQLOutputType(this);
    }

  }

  /**
   * Generates a builder initialized with the result of performing reflection on the given class.
   * 
   * @param klass
   * @return
   */

  public static final Builder builder(GraphQLOutputType clone)
  {
    return new Builder();
  }

  /**
   * scan the given klass to generate a builder.
   * 
   * @param klass
   * @return
   */

  public static final Builder builder(Class<?> klass)
  {
    Builder b = new Builder();
    AutoScanner.scan(b, klass);
    return b;
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
  public static final class Arg
  {

    private final String name;
    private final GQLDeclaration type;
    private final GQLValue defaultValue;

    private Arg(ArgBuilder ab)
    {
      this.name = ab.name;
      this.type = ab.type;
      this.defaultValue = ab.defaultValue;

      // validate
      Preconditions.checkState(GQLUtils.isValidTypeName(this.name), "invalid arg name", this.name);

    }

    public String name()
    {
      return this.name;
    }

  }

  @ToString
  @EqualsAndHashCode
  public static final class Field
  {

    private final String name;
    private final ImmutableList<Arg> args;

    // note: handler being null will just always result in null being returned.
    // so, this must fail if the type is GQLNonNull.
    private FieldHandler handler;

    private Field(FieldBuilder fb)
    {

      this.name = fb.name;
      this.args = ImmutableList.copyOf(fb.args.stream().map(Arg::new).collect(Collectors.toList()));
      this.handler = fb.handler;

      // validate
      Preconditions.checkState(GQLUtils.isValidTypeName(this.name), "invalid field name", this.name);
      Set<String> allItems = new HashSet<>();
      Set<String> dups = args.stream().map(arg -> arg.name.toLowerCase()).filter(n -> !allItems.add(n)).collect(Collectors.toSet());
      Preconditions.checkState(dups.isEmpty(), "duplicate args", dups);

    }

    public String name()
    {
      return this.name;
    }

    public FieldHandler handler()
    {
      return this.handler;
    }

    public List<Arg> args()
    {
      return this.args;
    }

  }

  // the output type name.
  private final String name;
  private final ImmutableList<Field> fields;

  private GraphQLOutputType(Builder builder)
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
