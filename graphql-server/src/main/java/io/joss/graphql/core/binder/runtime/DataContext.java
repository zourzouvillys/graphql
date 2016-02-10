package io.joss.graphql.core.binder.runtime;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.joss.graphql.core.decl.GQLArgumentDefinition;
import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.doc.GQLArgument;
import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLFieldSelection;
import io.joss.graphql.core.doc.GQLFragmentDefinition;
import io.joss.graphql.core.doc.GQLFragmentSpreadSelection;
import io.joss.graphql.core.doc.GQLInlineFragmentSelection;
import io.joss.graphql.core.doc.GQLSelection;
import io.joss.graphql.core.doc.GQLSelectionVisitor;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.lang.GQLTypeVisitors;
import io.joss.graphql.core.parser.GQLException;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.core.types.GQLTypes;
import io.joss.graphql.core.utils.GQLUtils;
import io.joss.graphql.core.value.GQLValueOutputTypes;

/**
 * Repreents a bound context at query time, as the reuslt of the schema along with an actual input.
 * 
 * @author theo
 *
 */

public class DataContext implements GQLSelectionVisitor<Void>
{

  private static final int DEFAULT_MAX_DEPTH = 8;

  /**
   * The parent data context.
   */

  private final DataContext parent;

  /**
   * The type this context represents.
   */

  private final GQLTypeReference type;

  /**
   * The field selection provided by the query.
   */

  private final GQLFieldSelection field;

  /**
   * the children that we create as we're building the plan.
   */

  private List<DataContext> children = new LinkedList<>();

  private GQLTypeRegistry registry;

  private GQLParameterableFieldDeclaration fdecl;

  private GQLDocument doc;

  /**
   * Constructs a root data context. This is the root of the query hierarchy, and doesn't contain a parent.
   * 
   * @param root
   */

  public DataContext(GQLTypeRegistry registry, GQLDeclaration root, GQLDocument doc)
  {

    if (root == null)
    {
      throw new IllegalArgumentException("root");
    }

    this.type = GQLTypes.ref(root);
    this.fdecl = null;
    this.parent = null;
    this.field = null;
    this.registry = registry;
    this.doc = doc;

  }

  /**
   * A data context is a single node within the path. It has a parent and a field.
   *
   * @param parent
   * @param field
   */

  public DataContext(final DataContext parent, final GQLFieldSelection field, GQLParameterableFieldDeclaration fdecl)
  {
    this.fdecl = fdecl;
    this.type = fdecl.type();
    this.parent = parent;
    this.field = field;
    this.registry = parent.typeRegistry();
    this.doc = parent.doc;
  }

  public GQLTypeRegistry typeRegistry()
  {
    return registry;
  }

  public DataContext parent()
  {
    return this.parent;
  }

  /**
   * returns the base declaration type for this class.
   * 
   * If it is a list, then it will return the element type.
   * 
   * @return
   */

  public GQLDeclaration declaration()
  {
    return type.apply(new GQLTypeVisitor<GQLDeclaration>() {

      @Override
      public GQLDeclaration visitNonNull(GQLNonNullType type)
      {
        return type.type().apply(this);
      }

      @Override
      public GQLDeclaration visitList(GQLListType type)
      {
        return type.type().apply(this);
      }

      @Override
      public GQLDeclaration visitDeclarationRef(GQLDeclarationRef type)
      {
        return typeRegistry().resolve(type);
      }
    });
  }

  public GQLTypeReference type()
  {
    return type;
  }

  public GQLFieldSelection selection()
  {
    if (field == null)
    {
      // there is no field selection on the root
      throw new RuntimeException("root node has no selection");
    }
    return this.field;
  }

  public List<DataContext> children()
  {
    return this.children;
  }

  /**
   * the hierarchy, including this.
   */

  public List<DataContext> heirachy()
  {
    if (this.parent == null)
    {
      List<DataContext> ctx = new ArrayList<>();
      ctx.add(this);
      return ctx;
    }
    final List<DataContext> path = this.parent.heirachy();
    path.add(this);
    return path;
  }

  /**
   * the full path to this node. the elements are the aliases (if set), not the type.
   */

  public String path()
  {
    // we skip the first element, as it has no name.
    return this.heirachy().stream().skip(1).map(e -> e.name()).collect(Collectors.joining("."));
  }

  /**
   * the return name to use for this node. returns the alias if set, else the name.
   */

  public String alias()
  {
    if (this.field == null)
    {
      return null;
    }
    return this.field.alias() == null ? this.field.name() : this.field.alias();
  }

  public String name()
  {
    if (this.field == null)
    {
      return null;
    }
    return this.field.name();
  }

  /**
   * Adds a new child node, and recurses into it.
   */

  @Override
  public Void visitFieldSelection(final GQLFieldSelection field)
  {

    try
    {
      if (isLeaf())
      {
        throw new GQLException(String.format("'%s' is a leaf. you can't select children of it.", this.path()));
      }

      if (this.heirachy().size() > DEFAULT_MAX_DEPTH)
      {
        throw new IllegalArgumentException("depth exceeded");
      }

      // find this field.
      GQLParameterableFieldDeclaration selected = type.apply(new FieldExtractor(this.typeRegistry(), field));

      if (selected == null)
      {
        throw new GQLException(
            String.format("Invalid field: '%s' on %s (%s)",
                field.name(),
                this.parent() == null ? "root node" : this.path(),
                this.type()));
      }

      // resolve the underlying type.

      // find the handler for this field.
      final DataContext ctx = new DataContext(this, field, selected);

      if (!ctx.isLeaf())
      {

        if (field.selections().isEmpty())
        {
          throw new GQLException("You must select children of '" + ctx.path() + "'");
        }

      }

      // validate the we have all of the required arguments, either as constant values or variables.
      for (GQLArgumentDefinition arg : selected.args())
      {

        if (!arg.type().apply(GQLTypeVisitors.isNotNull()))
        {
          // we don't need the value, so no need to validate.
          continue;
        }

        GQLArgument provided = field.args(arg.name());

        if (provided == null)
        {
          throw new GQLException(String.format("Missing required parameter '%s' on '%s'", arg.name(), ctx.path()));
        }

      }

      // now apply the children.
      field.selections().forEach(val -> val.apply(ctx));

      children.add(ctx);

    }
    catch (Exception ex)
    {
      throw ex;
    }

    return null;

  }

  /**
   * 
   * @param on
   * @param directives
   * @param selections
   */

  private void applyFragment(GQLDeclarationRef on, List<GQLDirective> directives, List<GQLSelection> selections)
  {

    selections.forEach(sel -> sel.apply(this));

    // throw new RuntimeException("do not support fragments yet");
  }

  /**
   * If the return GQL type is scalar.
   * 
   * It will return true for NonNull and List types too, if the inner type is scalar.
   * 
   * @return
   */

  public boolean isLeaf()
  {
    return this.type.apply(GQLTypeVisitors.isScalar());
  }

  /**
   * If the expected GQL return type from this selection is a list.
   */

  public boolean isCollection()
  {
    return type.apply(GQLTypeVisitors.isList());
  }

  /**
   * Resolve the fragments.
   */

  @Override
  public Void visitFragmentSelection(final GQLFragmentSpreadSelection selection)
  {
    GQLFragmentDefinition frag = doc.fragment(selection.name());
    applyFragment(frag.namedType(), frag.directives(), frag.selections());
    return null;
  }

  /**
   * an inline fragment.
   */

  @Override
  public Void visitInlineFragment(final GQLInlineFragmentSelection frag)
  {
    applyFragment(frag.typeCondition(), frag.directives(), frag.selections());
    return null;
  }

  @Override
  public String toString()
  {
    return String.format("DataContext(%s: path=%s, args=%s)", this.alias(), this.path(), (field != null) ? GQLUtils.toString(this.field.args()) : null);
  }

  public GQLParameterableFieldDeclaration fdecl()
  {
    return this.fdecl;
  }

  public DataContext child(String name)
  {
    return children().stream().filter(ctx -> ctx.name().equals(name)).findAny().orElse(null);
  }

  public GQLValueOutputTypes returnShape()
  {

    return this.type.apply(new GQLTypeVisitor<GQLValueOutputTypes>() {

      @Override
      public GQLValueOutputTypes visitNonNull(GQLNonNullType type)
      {
        return type.type().apply(this);
      }

      @Override
      public GQLValueOutputTypes visitList(GQLListType type)
      {
        return GQLValueOutputTypes.LIST;
      }

      @Override
      public GQLValueOutputTypes visitDeclarationRef(GQLDeclarationRef type)
      {
        return type.apply(new GQLDeclarationVisitor<GQLValueOutputTypes>() {

          @Override
          public GQLValueOutputTypes visitUnion(GQLUnionTypeDeclaration type)
          {
            // TODO: work it out.
            throw new RuntimeException("Not implemented");
          }

          @Override
          public GQLValueOutputTypes visitScalar(GQLScalarTypeDeclaration type)
          {
            return GQLValueOutputTypes.STRING;
          }

          @Override
          public GQLValueOutputTypes visitObject(GQLObjectTypeDeclaration type)
          {
            return GQLValueOutputTypes.OBJECT;
          }

          @Override
          public GQLValueOutputTypes visitInterface(GQLInterfaceTypeDeclaration type)
          {
            return GQLValueOutputTypes.OBJECT;
          }

          @Override
          public GQLValueOutputTypes visitEnum(GQLEnumDeclaration type)
          {
            return GQLValueOutputTypes.ENUM;
          }

          @Override
          public GQLValueOutputTypes visitInput(GQLInputTypeDeclaration type)
          {
            throw new GQLException("Can't return an input type as an output");
          }
        });
      }

    });

  }

}
