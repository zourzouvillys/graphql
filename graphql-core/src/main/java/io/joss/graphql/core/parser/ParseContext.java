package io.joss.graphql.core.parser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.joss.graphql.core.decl.GQLArgumentDefinition;
import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLEnumValue;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.doc.GQLArgument;
import io.joss.graphql.core.doc.GQLDefinition;
import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.doc.GQLDirective.GQLDirectiveBuilder;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLFieldSelection;
import io.joss.graphql.core.doc.GQLFragmentDefinition;
import io.joss.graphql.core.doc.GQLFragmentDefinition.GQLFragmentDefinitionBuilder;
import io.joss.graphql.core.doc.GQLFragmentSpreadSelection;
import io.joss.graphql.core.doc.GQLFragmentSpreadSelection.Builder;
import io.joss.graphql.core.doc.GQLInlineFragmentSelection;
import io.joss.graphql.core.doc.GQLInlineFragmentSelection.GQLInlineFragmentSelectionBuilder;
import io.joss.graphql.core.doc.GQLOpType;
import io.joss.graphql.core.doc.GQLOperationDefinition;
import io.joss.graphql.core.doc.GQLSelection;
import io.joss.graphql.core.doc.GQLVariableDefinition;
import io.joss.graphql.core.doc.GQLVariableDefinition.GQLVariableDefinitionBuilder;
import io.joss.graphql.core.parser.Lexer.TokenType;
import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.core.types.GQLTypes;
import io.joss.graphql.core.value.GQLListValue;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValues;

public class ParseContext
{

  private final Lexer lexer;

  public ParseContext(final String doc)
  {
    this.lexer = new Lexer(doc);
  }

  /**
   * parse a list of type definitions.
   */

  public List<GQLDeclaration> parseSchema()
  {
    final List<GQLDeclaration> types = new LinkedList<>();
    while (this.lexer.isReadable())
    {
      final GQLDeclaration val = this.parseTypeDefinition();
      if (val != null)
      {
        types.add(val);
      }
    }
    return types;
  }

  /**
   * parses a single type definition.
   */

  private GQLDeclaration parseTypeDefinition()
  {

    String comment = null;

    if (this.is(TokenType.COMMENT))
    {
      comment = this.require(TokenType.COMMENT);
    }

    switch (this.lexer.peek().value())
    {

      case "extend":
        this.require("extend");
        this.parseObjectTypeDefinition().withDescription(comment);
        return null;

      case "interface":
        return this.parseInterfaceDefinition();

      case "type":
        return this.parseObjectTypeDefinition().withDescription(comment);

      case "enum":
        return this.parseEnumDefinition().withDescription(comment);

      case "union":
        return this.parseUnionDefinition().withDescription(comment);

      case "input":
        return this.parseInputDefinition().withDescription(comment);

      case "scalar":
        return this.parseScalarDefinition().withDescription(comment);

      default:
        throw ParserExceptions.expect(this, "type definition", null);
    }

  }

  private GQLScalarTypeDeclaration parseScalarDefinition()
  {
    this.require("scalar");
    return GQLTypes.scalar(this.require(TokenType.NAME));
  }

  /**
   *
   * @return
   */

  private GQLInterfaceTypeDeclaration parseInterfaceDefinition()
  {

    final GQLInterfaceTypeDeclaration.Builder b = GQLInterfaceTypeDeclaration.builder();

    this.require("interface");

    b.name(this.require(TokenType.NAME));

    if (this.skip("implements"))
    {
      do
      {
        b.iface(GQLTypes.concreteTypeRef(this.require(TokenType.NAME)));
      } while (this.is(","));
    }

    this.require("{");

    // now read each of the definitions.

    while (!this.is("}"))
    {

      final GQLParameterableFieldDeclaration.Builder fb = GQLParameterableFieldDeclaration.builder();

      fb.name(this.require(TokenType.NAME));

      if (this.is("("))
      {
        fb.args(this.parseArgumentDefinitions());
      }

      this.require(":");

      fb.type(this.parseTypeRef());

      b.field(fb.build());

    }

    this.require("}");

    return b.build();
  }

  /**
   *
   * @return
   */

  private GQLObjectTypeDeclaration parseObjectTypeDefinition()
  {

    final GQLObjectTypeDeclaration.Builder b = GQLObjectTypeDeclaration.builder();

    this.require("type");

    b.name(this.require(TokenType.NAME));

    if (this.skip("implements"))
    {
      do
      {
        b.iface(GQLTypes.concreteTypeRef(this.require(TokenType.NAME)));
      } while (this.is(","));
    }

    this.require("{");

    while (!this.is("}"))
    {

      final GQLParameterableFieldDeclaration.Builder fb = GQLParameterableFieldDeclaration.builder();

      if (this.is(TokenType.COMMENT))
      {
        fb.description(this.require(TokenType.COMMENT));
      }

      fb.name(this.require(TokenType.NAME));

      if (this.is("("))
      {
        fb.args(this.parseArgumentDefinitions());
      }

      this.require(":");

      final GQLTypeReference type = this.parseTypeRef();

      fb.type(type);

      b.field(fb.build());

    }

    this.require("}");

    return b.build();
  }

  /**
   * returns a field definition, e.g the name, type and optional default value. Consumes start and end '(' ')'
   *
   * @return
   */

  private Collection<? extends GQLArgumentDefinition> parseArgumentDefinitions()
  {
    final List<GQLArgumentDefinition> defs = new LinkedList<>();

    this.require("(");

    while (!this.is(")"))
    {

      final GQLArgumentDefinition.Builder ab = GQLArgumentDefinition.builder();

      ab.name(this.require(TokenType.NAME));

      this.require(":");

      final GQLTypeReference tr = this.parseTypeRef();

      ab.type(tr);

      if (this.skip("="))
      {
        ab.defaultValue(this.parseValue());
      }

      defs.add(ab.build());

    }
    this.require(")");
    return defs;
  }

  /**
   *
   * @return
   */

  private GQLEnumDeclaration parseEnumDefinition()
  {
    final GQLEnumDeclaration.Builder b = GQLEnumDeclaration.builder();
    this.require("enum");
    b.name(this.require(TokenType.NAME));
    this.require("{");
    while (!this.is("}"))
    {
      b.value(GQLEnumValue.builder().name(this.require(TokenType.NAME)).build());
    }
    this.require("}");

    return b.build();
  }

  private GQLUnionTypeDeclaration parseUnionDefinition()
  {

    final GQLUnionTypeDeclaration.Builder b = GQLUnionTypeDeclaration.builder();

    this.require("union");
    b.name(this.require(TokenType.NAME));
    this.require("=");

    do
    {
      b.type(GQLTypes.concreteTypeRef(this.require(TokenType.NAME)));
    } while (this.lexer.isReadable() && this.skip("|"));

    return b.build();

  }

  private GQLObjectTypeDeclaration parseInputDefinition()
  {
    this.require("input");
    final String name = this.require(TokenType.NAME);
    this.require("{");

    while (!this.is("}"))
    {

      this.require(TokenType.NAME);
      this.require(":");

      this.parseTypeRef();

      if (this.skip("="))
      {
        this.parseValue();
      }

    }

    this.require("}");

    return GQLTypes.structBuilder(name).build();
  }

  /**
   *
   * @return
   */

  public GQLDocument parseDocument()
  {

    final GQLDocument.Builder b = GQLDocument.builder();

    while (this.lexer.isReadable())
    {
      GQLDefinition def = this.parseDefinition();
      b.definition(def);
    }

    return b.build();

  }

  /**
   * returns an operation or a fragment.
   */

  public GQLDefinition parseDefinition()
  {

    if (this.skip("fragment"))
    {
      return this.parseFragment();
    }

    return this.parseOperation();

  }

  /**
   * parses an operation, throwing if one is not found.
   *
   * @return
   */

  GQLOperationDefinition parseOperation()
  {

    if (this.is("{"))
    {
      return GQLOperationDefinition.builder()
          .selections(this.parseSelectionSet())
          .build();
    }

    final GQLOperationDefinition.Builder b = GQLOperationDefinition.builder();

    if (this.skip("query"))
    {
      b.type(GQLOpType.Query);
    }
    else if (this.skip("mutation"))
    {
      b.type(GQLOpType.Mutation);
    }
    else if (this.skip("subscription"))
    {
      b.type(GQLOpType.Subscription);
    }
    else
    {
      throw new IllegalArgumentException("Unknown operation type: " + this.next());
    }

    if (this.lexer.peek().type() == TokenType.NAME)
    {
      b.name(this.next());
    }

    if (this.is("("))
    {
      b.vars(this.parseVariableDefinitions());
    }

    if (this.is("@"))
    {
      b.directives(this.parseDirectives());
    }

    b.selections(this.parseSelectionSet());

    return b.build();
  }

  private List<GQLVariableDefinition> parseVariableDefinitions()
  {

    final List<GQLVariableDefinition> vars = new LinkedList<>();

    this.require("(");

    while (!this.is(")"))
    {
      final GQLVariableDefinitionBuilder b = GQLVariableDefinition.builder();
      this.require("$");
      b.name(this.require(TokenType.NAME));
      this.require(":");

      //
      b.type(this.parseTypeRef());

      if (this.skip("="))
      {
        b.defaultValue(this.parseValue());
      }

      vars.add(b.build());

    }

    this.require(")");

    return vars;

  }

  /**
   * parses a type reference, e.g MyType, Int, [Int!], MyType!, [[Int!]!]!
   */

  private GQLTypeReference parseTypeRef()
  {

    // GQLTypeReference

    final GQLTypeReference type;

    if (this.lexer.peek().type() == TokenType.NAME)
    {
      type = GQLTypes.typeRef(this.next());
    }
    else if (this.skip("["))
    {
      type = GQLTypes.listOf(this.parseTypeRef());
      this.require("]");
    }
    else
    {
      throw new IllegalArgumentException(this.lexer.peek().toString());
    }

    if (this.skip("!"))
    {
      return GQLTypes.nonNull(type);
    }

    return type;

  }

  /**
   *
   * @return
   */

  private GQLFragmentDefinition parseFragment()
  {

    final GQLFragmentDefinitionBuilder b = GQLFragmentDefinition.builder();

    final String fragmentName = this.next();

    if (fragmentName.equals("on"))
    {
      throw new IllegalArgumentException("Fragment name must not be 'on'");
    }

    b.name(fragmentName);

    this.require("on");

    final String applyTo = this.parseName();

    b.namedType(GQLTypes.concreteTypeRef(applyTo));

    if (this.is("@"))
    {
      b.directives(this.parseDirectives());
    }

    b.selections(this.parseSelectionSet());

    return b.build();

  }

  /**
   * requires the next token to be '{'. Reads until the last (balanced) '}'.
   *
   * @return
   */

  private List<GQLSelection> parseSelectionSet()
  {

    final List<GQLSelection> selections = new LinkedList<>();

    final Token opening = this.require("{");

    while (!this.is("}"))
    {

      if (this.skip("..."))
      {

        if (is("on") || is("@") || is("{"))
        {

          GQLInlineFragmentSelectionBuilder ifs = GQLInlineFragmentSelection.builder();

          if (skip("on"))
          {
            ifs.typeCondition(GQLTypes.typeRef(require(TokenType.NAME)));
          }

          if (is("@"))
          {
            ifs.directives(parseDirectives());
          }

          if (is("{"))
          {
            ifs.selections(parseSelectionSet());
          }

          selections.add(ifs.build());

        }
        else
        {

          Builder fsb = GQLFragmentSpreadSelection.builder();

          fsb.name(require(TokenType.NAME));

          if (is("@"))
          {
            fsb.directives(parseDirectives());
          }

          selections.add(fsb.build());

        }

        continue;

      }

      final GQLFieldSelection.Builder fb = GQLFieldSelection.builder();

      final String name = this.require(TokenType.NAME, "or '}' to match '{' defined at position " + opening.position().start());

      if (this.skip(":"))
      {
        fb.alias(name);
        fb.name(this.next());
      }
      else
      {
        fb.name(name);
      }

      if (this.is("("))
      {
        fb.args(this.parseArguments());
      }

      if (this.is("@"))
      {
        fb.directives(this.parseDirectives());
      }

      if (this.is("{"))
      {
        fb.selections(this.parseSelectionSet());
      }

      selections.add(fb.build());

    }

    this.require("}");

    return selections;

  }

  /**
   * Parses a set of arguments, starting and ending with '(' and ')'.
   *
   * @return
   */

  private List<GQLArgument> parseArguments()
  {

    this.require("(");

    final List<GQLArgument> args = new LinkedList<>();

    while (!this.is(")"))
    {
      final String name = this.require(TokenType.NAME);
      this.require(":", "after argument name");
      final GQLValue value = this.parseValue();
      args.add(GQLArgument.builder().name(name).value(value).build());
    }

    this.require(")");

    return args;

  }

  GQLValue parseValue()
  {
    if (this.skip("$"))
    {
      return GQLValues.variable(this.require(TokenType.NAME));
    }
    else if (this.skip("false"))
    {
      return GQLValues.booleanFalse();
    }
    else if (this.skip("true"))
    {
      return GQLValues.booleanTrue();
    }
    else if (this.lexer.peek().type() == TokenType.INT)
    {
      return GQLValues.intValue(Long.parseLong(this.next()));
    }
    else if (this.lexer.peek().type() == TokenType.FLOAT)
    {
      return GQLValues.floatValue(Double.parseDouble(this.next()));
    }
    else if (this.lexer.peek().type() == TokenType.STRING)
    {
      return GQLValues.stringValue(this.next());
    }
    else if (this.is("["))
    {
      return this.parseArray();
    }
    else if (this.is("{"))
    {
      return this.parseObject();
    }
    else
    {
      if (this.is("null"))
      {
        throw new IllegalArgumentException("invalid enum name");
      }
      return GQLValues.enumValueRef(this.next());
    }
  }

  private GQLListValue parseArray()
  {

    final GQLListValue.Builder b = GQLListValue.builder();

    this.require("[");

    while (!this.is("]"))
    {
      b.value(this.parseValue());
    }

    this.require("]");

    return b.build();

  }

  private GQLValue parseObject()
  {

    final GQLObjectValue.Builder b = GQLObjectValue.builder();

    this.require("{");

    while (!this.is("}"))
    {
      final String name = this.parseName();
      this.require(":");
      b.value(name, this.parseValue());
    }

    this.require("}");

    return b.build();

  }

  private List<GQLDirective> parseDirectives()
  {

    if (!this.is("@"))
    {
      throw new IllegalStateException();
    }

    final List<GQLDirective> items = new LinkedList<>();

    while (this.skip("@"))
    {
      final GQLDirectiveBuilder b = GQLDirective.builder();
      b.name(this.next());
      if (this.is("("))
      {
        b.args(this.parseArguments());
      }
      items.add(b.build());
    }

    return items;

  }

  private String parseName()
  {
    return this.require(TokenType.NAME);
  }

  private Token require(final String string)
  {
    return this.require(string, null);
  }

  private Token require(final String string, final String message)
  {
    if (!this.is(string))
    {
      throw ParserExceptions.expect(this, string, message);

    }

    final Token next = this.lexer.next();

    if (next == null)
    {
      throw ParserExceptions.endOfStream();
    }

    return next;
  }

  private String require(final TokenType type)
  {
    return this.require(type, null);
  }

  private String require(final TokenType type, final String message)
  {
    if (this.lexer.peek() == null)
    {
      throw ParserExceptions.expect(this, type.toString(), message);
    }
    if (this.lexer.peek().type() != type)
    {
      throw ParserExceptions.expect(this, type.toString(), message);
    }
    return this.next();
  }

  private boolean skip(final String string)
  {
    if (this.is(string))
    {
      this.next();
      return true;
    }
    return false;
  }

  private String next()
  {
    final Token next = this.lexer.next();
    if (next == null)
    {
      throw ParserExceptions.endOfStream();
    }
    return next.value();
  }

  private boolean is(final String value)
  {
    if (this.lexer.peek() == null)
    {
      return false;
    }
    return this.lexer.peek().value().equals(value);
  }

  private boolean is(final TokenType type)
  {
    if (this.lexer.peek() == null)
    {
      return false;
    }
    return this.lexer.peek().type() == type;
  }

  public Lexer lexer()
  {
    return this.lexer;
  }

}
