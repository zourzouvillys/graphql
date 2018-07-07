package io.zrz.graphql.core.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.zrz.graphql.core.decl.GQLArgumentDefinition;
import io.zrz.graphql.core.decl.GQLDeclaration;
import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLEnumValue;
import io.zrz.graphql.core.decl.GQLInputFieldDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLSchemaDeclaration;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLArgumentDefinition;
import io.zrz.graphql.core.decl.ImmutableGQLEnumDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLEnumValue;
import io.zrz.graphql.core.decl.ImmutableGQLInputFieldDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLParameterableFieldDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLSchemaDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLUnionTypeDeclaration;
import io.zrz.graphql.core.doc.GQLArgument;
import io.zrz.graphql.core.doc.GQLDefinition;
import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLFragmentDefinition;
import io.zrz.graphql.core.doc.GQLFragmentSpreadSelection;
import io.zrz.graphql.core.doc.GQLInlineFragmentSelection;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.doc.GQLSelection;
import io.zrz.graphql.core.doc.GQLVariableDefinition;
import io.zrz.graphql.core.doc.ImmutableGQLDirective;
import io.zrz.graphql.core.doc.ImmutableGQLDocument;
import io.zrz.graphql.core.doc.ImmutableGQLFieldSelection;
import io.zrz.graphql.core.doc.ImmutableGQLFragmentDefinition;
import io.zrz.graphql.core.doc.ImmutableGQLFragmentSpreadSelection;
import io.zrz.graphql.core.doc.ImmutableGQLInlineFragmentSelection;
import io.zrz.graphql.core.doc.ImmutableGQLOperationDefinition;
import io.zrz.graphql.core.doc.ImmutableGQLVariableDefinition;
import io.zrz.graphql.core.parser.Lexer.TokenType;
import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.types.GQLTypes;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValues;
import io.zrz.graphql.core.value.ImmutableGQLListValue;
import io.zrz.graphql.core.value.ImmutableGQLObjectValue;

public class ParseContext {

  private final Lexer lexer;

  public ParseContext(final String doc, final GQLSourceInput source) {
    this.lexer = new Lexer(doc, source);
  }

  /**
   * parse a list of type definitions.
   */

  public List<GQLDeclaration> parseSchema() {
    final List<GQLDeclaration> types = new LinkedList<>();
    while (this.lexer.isReadable()) {
      final GQLDeclaration val = this.parseTypeDefinition();
      if (val != null) {
        types.add(val);
      }
    }
    return types;
  }

  /**
   * parses a single type definition.
   */

  private GQLDeclaration parseTypeDefinition() {

    String comment = null;
    List<GQLDirective> directives = null;

    if (this.is(TokenType.COMMENT)) {
      comment = this.require(TokenType.COMMENT);
    }

    if (this.is("@")) {
      directives = this.parseDirectives();
    }
    else {
      directives = new LinkedList<>();
    }

    switch (this.lexer.peek().value()) {

      case "extend":
        this.require("extend");
        if (this.is("input")) {
          return this.parseInputDefinition().withIsExtension(true).withDescription(comment).withLocation(this.lexer.position());
        }
        else if (this.is("type")) {
          return this.parseObjectTypeDefinition().withIsExtension(true).withDescription(comment).withLocation(this.lexer.position());
        }
        else if (this.is("union")) {
          return this.parseUnionDefinition().withIsExtension(true).withDescription(comment).withLocation(this.lexer.position());
        }
        else if (this.is("enum")) {
          return this.parseEnumDefinition().withIsExtension(true).withDescription(comment).withLocation(this.lexer.position());
        }
        else if (this.is("interface")) {
          return this.parseInterfaceDefinition().withIsExtension(true).withDescription(comment).withLocation(this.lexer.position());
        }
        throw ParserExceptions.expect(this, "unsupported extend", null);

      case "schema":
        return this.parseSchemaDefinition().withDirectives(directives).withLocation(this.lexer.position());

      case "interface":
        return this.parseInterfaceDefinition().withDirectives(directives).withLocation(this.lexer.position());

      case "type":
        return this.parseObjectTypeDefinition().withDescription(comment).withDirectives(directives).withLocation(this.lexer.position());

      case "enum":
        return this
            .parseEnumDefinition()
            .withDescription(comment)
            .withDirectives(directives)
            .withLocation(this.lexer.position());

      case "union":
        return this.parseUnionDefinition().withDescription(comment).withDirectives(directives).withLocation(this.lexer.position());

      case "input":
        return this.parseInputDefinition().withDescription(comment).withDirectives(directives).withLocation(this.lexer.position());

      case "scalar":
        return this.parseScalarDefinition().withDescription(comment).withDirectives(directives).withLocation(this.lexer.position());

      case "directive":
        return this.parseDirectiveDefinition().withDescription(comment).withDirectives(directives).withLocation(this.lexer.position());

      default:
        throw ParserExceptions.expect(this, "type definition", null);
    }

  }

  private GQLScalarTypeDeclaration parseDirectiveDefinition() {
    this.require("directive");
    return GQLTypes.scalar(this.require(TokenType.NAME));
  }

  private GQLScalarTypeDeclaration parseScalarDefinition() {
    this.require("scalar");
    return GQLTypes.scalar(this.require(TokenType.NAME));
  }

  /**
   *
   * @return
   */

  private GQLInterfaceTypeDeclaration parseInterfaceDefinition() {

    final GQLInterfaceTypeDeclaration.Builder b = GQLInterfaceTypeDeclaration.builder();

    this.require("interface");

    b.name(this.require(TokenType.NAME));

    if (this.skip("implements")) {
      do {
        b.addIfaces(GQLTypes.concreteTypeRef(this.require(TokenType.NAME)));
      }
      while (this.is(","));
    }

    this.require("{");

    // now read each of the definitions.

    while (!this.is("}")) {

      final ImmutableGQLParameterableFieldDeclaration.Builder fb = GQLParameterableFieldDeclaration.builder();

      fb.name(this.require(TokenType.NAME));

      if (this.is("(")) {
        fb.args(this.parseArgumentDefinitions());
      }

      this.require(":");

      fb.type(this.parseTypeRef());

      if (this.is("@")) {
        final List<GQLDirective> directives = this.parseDirectives();
        fb.directives(directives);
      }

      b.addFields(fb.build());

    }

    this.require("}");

    return b.build();
  }

  /**
   *
   * @return
   */

  private GQLObjectTypeDeclaration parseObjectTypeDefinition() {

    final GQLObjectTypeDeclaration.Builder b = GQLObjectTypeDeclaration.builder();

    this.require("type");

    b.name(this.require(TokenType.NAME));

    if (this.skip("implements")) {
      do {
        final String name = this.require(TokenType.NAME);
        b.addIfaces(GQLTypes.concreteTypeRef(name));
        // note: parser skips over comma.
      }
      while (this.skip("&"));
    }

    this.require("{");

    while (!this.is("}")) {

      final ImmutableGQLParameterableFieldDeclaration.Builder fb = GQLParameterableFieldDeclaration.builder();

      if (this.is(TokenType.COMMENT)) {
        fb.description(this.require(TokenType.COMMENT));
      }

      fb.name(this.require(TokenType.NAME));

      if (this.is("(")) {
        fb.args(this.parseArgumentDefinitions());
      }

      this.require(":");

      final GQLTypeReference type = this.parseTypeRef();

      fb.type(type);

      if (this.is("@")) {
        fb.directives(this.parseDirectives());
      }

      b.addFields(fb.build());

    }

    this.require("}");

    return b.build();
  }

  /**
   * returns a field definition, e.g the name, type and optional default value. Consumes start and end '(' ')'
   *
   * @return
   */

  private Collection<? extends GQLArgumentDefinition> parseArgumentDefinitions() {
    final List<GQLArgumentDefinition> defs = new LinkedList<>();

    this.require("(");

    while (!this.is(")")) {

      final ImmutableGQLArgumentDefinition.Builder ab = GQLArgumentDefinition.builder();

      if (this.is("@")) {
        ab.directives(this.parseDirectives());
      }

      ab.name(this.require(TokenType.NAME));

      this.require(":");

      final GQLTypeReference tr = this.parseTypeRef();

      ab.type(tr);

      if (this.skip("=")) {
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

  private GQLEnumDeclaration parseEnumDefinition() {
    final ImmutableGQLEnumDeclaration.Builder b = GQLEnumDeclaration.builder();
    this.require("enum");
    b.name(this.require(TokenType.NAME));
    this.require("{");
    while (!this.is("}")) {

      final ImmutableGQLEnumValue.Builder eb = GQLEnumValue.builder();

      if (this.is("@")) {
        eb.directives(this.parseDirectives());
      }

      b.addValues(eb.name(this.require(TokenType.NAME)).build());
    }
    this.require("}");

    return b.build();
  }

  private GQLUnionTypeDeclaration parseUnionDefinition() {

    final ImmutableGQLUnionTypeDeclaration.Builder b = GQLUnionTypeDeclaration.builder();

    this.require("union");
    b.name(this.require(TokenType.NAME));
    this.require("=");

    do {
      b.addTypes(GQLTypes.concreteTypeRef(this.require(TokenType.NAME)));
    }
    while (this.lexer.isReadable() && this.skip("|"));

    return b.build();

  }

  private GQLSchemaDeclaration parseSchemaDefinition() {

    final ImmutableGQLSchemaDeclaration.Builder builder = GQLTypes.schemaBuilder();

    this.require("schema");

    // TODO: directives

    this.require("{");

    while (!this.is("}")) {
      final String key = this.require(TokenType.NAME);
      this.require(":");
      builder.putEntries(key, GQLTypes.typeRef(this.next()));
    }

    this.require("}");

    return builder.build();
  }

  private GQLInputTypeDeclaration parseInputDefinition() {

    this.require("input");
    final String name = this.require(TokenType.NAME);
    this.require("{");

    final ImmutableGQLInputTypeDeclaration.Builder b = GQLTypes.inputBuilder(name);

    while (!this.is("}")) {

      final ImmutableGQLInputFieldDeclaration.Builder ib = GQLInputFieldDeclaration.builder();

      if (this.is("@")) {
        ib.directives(this.parseDirectives());
      }

      ib.name(this.require(TokenType.NAME));
      this.require(":");

      ib.type(this.parseTypeRef());

      if (this.skip("=")) {
        ib.defaultValue(this.parseValue());
      }

      b.addFields(ib.build());

    }

    this.require("}");

    return b.build();
  }

  /**
   *
   * @param start
   * @return
   */

  public GQLDocument parseDocument() {

    final ImmutableGQLDocument.Builder b = GQLDocument.builder();

    GQLSourceLocation start = this.lexer.position();

    while (this.lexer.isReadable()) {
      final GQLDefinition def = this.parseDefinition(start);
      b.addDefinitions(def);
      start = this.lexer.position();
    }

    return b.build();

  }

  /**
   * returns an operation or a fragment.
   */

  public GQLDefinition parseDefinition(final GQLSourceLocation start) {

    if (this.skip("fragment")) {
      return this.parseFragment();
    }

    return this.parseOperation(start);

  }

  /**
   * parses an operation, throwing if one is not found.
   *
   * @return
   */

  GQLOperationDefinition parseOperation(final GQLSourceLocation start) {

    if (this.is("{")) {
      return GQLOperationDefinition.builder()
          .selections(this.parseSelectionSet())
          .range(this.lexer.range(start, this.lexer.position()))
          .build();
    }

    final ImmutableGQLOperationDefinition.Builder b = GQLOperationDefinition.builder();

    if (this.skip("query")) {
      b.type(GQLOpType.Query);
    }
    else if (this.skip("mutation")) {
      b.type(GQLOpType.Mutation);
    }
    else if (this.skip("subscription")) {
      b.type(GQLOpType.Subscription);
    }
    else {
      throw ParserExceptions.create(this, String.format("Unknown operation type: %s", this.next()));
    }

    if (this.lexer.peek().type() == TokenType.NAME) {
      b.name(this.next());
    }

    if (this.is("(")) {
      b.vars(this.parseVariableDefinitions());
    }

    if (this.is("@")) {
      b.directives(this.parseDirectives());
    }

    b.selections(this.parseSelectionSet());

    b.range(this.lexer.range(start, this.lexer.position()));

    return b.build();

  }

  private List<GQLVariableDefinition> parseVariableDefinitions() {

    final List<GQLVariableDefinition> vars = new LinkedList<>();

    this.require("(");

    while (!this.is(")")) {
      final ImmutableGQLVariableDefinition.Builder b = GQLVariableDefinition.builder();
      this.require("$");
      b.name(this.require(TokenType.NAME));
      this.require(":");

      //
      b.type(this.parseTypeRef());

      if (this.skip("=")) {
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

  GQLTypeReference parseTypeRef() {

    // GQLTypeReference

    final GQLTypeReference type;

    final List<GQLDirective> directives = this.is("@") ? this.parseDirectives() : Collections.emptyList();

    if (this.lexer.peek().type() == TokenType.NAME) {
      type = GQLTypes.typeRef(this.next(), directives);
    }
    else if (this.skip("[")) {
      type = GQLTypes.listOf(this.parseTypeRef(), directives);
      this.require("]");
    }
    else {
      throw ParserExceptions.create(this, "unexpected input");
    }

    if (this.skip("!")) {
      return GQLTypes.nonNull(type, directives);
    }

    return type;

  }

  /**
   *
   * @return
   */

  private GQLFragmentDefinition parseFragment() {

    final ImmutableGQLFragmentDefinition.Builder b = GQLFragmentDefinition.builder();

    final String fragmentName = this.next();

    if (fragmentName.equals("on")) {
      throw ParserExceptions.create(this, "Fragment name must not be 'on'");
    }

    b.name(fragmentName);

    this.require("on");

    final String applyTo = this.parseName();

    b.namedType(GQLTypes.concreteTypeRef(applyTo));

    if (this.is("@")) {
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

  List<GQLSelection> parseSelectionSet() {

    final List<GQLSelection> selections = new LinkedList<>();

    final Token opening = this.require("{");

    if (this.is("}")) {
      throw ParserExceptions.expect(this, "Name", "found '}'");
    }

    while (!this.is("}")) {

      if (this.skip("...")) {

        if (this.is("on") || this.is("@") || this.is("{")) {

          final ImmutableGQLInlineFragmentSelection.Builder ifs = GQLInlineFragmentSelection.builder();

          ifs.location(this.lexer.position());

          if (this.skip("on")) {
            ifs.typeCondition(GQLTypes.typeRef(this.require(TokenType.NAME)));
          }

          if (this.is("@")) {
            ifs.directives(this.parseDirectives());
          }

          if (this.is("{")) {
            ifs.selections(this.parseSelectionSet());
          }

          selections.add(ifs.build());

        }
        else {

          final ImmutableGQLFragmentSpreadSelection.Builder fsb = GQLFragmentSpreadSelection.builder();

          fsb.location(this.lexer.position());

          fsb.name(this.require(TokenType.NAME));

          if (this.is("@")) {
            fsb.directives(this.parseDirectives());
          }

          selections.add(fsb.build());

        }

        continue;

      }

      final ImmutableGQLFieldSelection.Builder fb = GQLFieldSelection.builder();

      final String name = this.require(TokenType.NAME,
          "or '}' to match '{' defined at position " + opening.position().start());

      fb.location(this.lexer.position());

      if (this.skip(":")) {
        fb.alias(name);
        fb.name(this.require(TokenType.NAME, "field name expected after alias"));
      }
      else {
        fb.name(name);
      }

      if (this.is("(")) {
        fb.args(this.parseArguments());
      }

      if (this.is("@")) {
        fb.directives(this.parseDirectives());
      }

      if (this.is("{")) {
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

  private List<GQLArgument> parseArguments() {

    this.require("(");

    final List<GQLArgument> args = new LinkedList<>();

    while (!this.is(")")) {
      final GQLSourceLocation start = this.lexer.position();
      final String name = this.require(TokenType.NAME);
      this.require(":", "after argument name");
      final GQLValue value = this.parseValue();
      ;
      args.add(GQLArgument.builder()
          .name(name)
          .value(value)
          .location(this.lexer.range(start, this.lexer.position()))
          .build());
    }

    this.require(")");

    return args;

  }

  GQLValue parseValue() {
    if (this.skip("$")) {
      return GQLValues.variable(this.require(TokenType.NAME));
    }
    else if (this.skip("false")) {
      return GQLValues.booleanFalse();
    }
    else if (this.skip("true")) {
      return GQLValues.booleanTrue();
    }
    else if (this.lexer.peek().type() == TokenType.INT) {
      return GQLValues.intValue(Long.parseLong(this.next()));
    }
    else if (this.lexer.peek().type() == TokenType.FLOAT) {
      return GQLValues.floatValue(Double.parseDouble(this.next()));
    }
    else if (this.lexer.peek().type() == TokenType.STRING) {
      return GQLValues.stringValue(this.next());
    }
    else if (this.is("[")) {
      return this.parseArray();
    }
    else if (this.is("{")) {
      return this.parseObject();
    }
    else {
      if (this.is("null")) {
        throw ParserExceptions.create(this, "Invalid ENUM name");
      }
      return GQLValues.enumValueRef(this.next());
    }
  }

  private GQLListValue parseArray() {

    final ImmutableGQLListValue.Builder b = GQLListValue.builder();

    this.require("[");

    while (!this.is("]")) {
      b.addValues(this.parseValue());
    }

    this.require("]");

    return b.build();

  }

  private GQLValue parseObject() {

    final ImmutableGQLObjectValue.Builder b = GQLObjectValue.builder();

    this.require("{");

    while (!this.is("}")) {
      final String name = this.parseName();
      this.require(":");
      b.putValues(name, this.parseValue());
    }

    this.require("}");

    return b.build();

  }

  private List<GQLDirective> parseDirectives() {

    if (!this.is("@")) {
      throw new IllegalStateException();
    }

    final List<GQLDirective> items = new LinkedList<>();

    while (this.skip("@")) {
      final ImmutableGQLDirective.Builder b = GQLDirective.builder();
      b.name(this.next());
      if (this.is("(")) {
        b.args(this.parseArguments());
      }
      items.add(b.build());
    }

    return items;

  }

  private String parseName() {
    return this.require(TokenType.NAME);
  }

  private Token require(final String string) {
    return this.require(string, null);
  }

  private Token require(final String string, final String message) {
    if (!this.is(string)) {
      throw ParserExceptions.expect(this, string, message);

    }

    final Token next = this.lexer.next();

    if (next == null) {
      throw ParserExceptions.endOfStream();
    }

    return next;
  }

  private String require(final TokenType type) {
    return this.require(type, null);
  }

  private String require(final TokenType type, final String message) {
    if (this.lexer.peek() == null) {
      throw ParserExceptions.expect(this, type.toString(), message);
    }
    if (this.lexer.peek().type() != type) {
      throw ParserExceptions.expect(this, type.toString(), message);
    }
    return this.next();
  }

  private boolean skip(final String string) {
    if (this.is(string)) {
      this.next();
      return true;
    }
    return false;
  }

  private String next() {
    final Token next = this.lexer.next();
    if (next == null) {
      throw ParserExceptions.endOfStream();
    }
    return next.value();
  }

  private boolean is(final String value) {
    if (this.lexer.peek() == null) {
      return false;
    }
    return this.lexer.peek().value().equals(value);
  }

  private boolean is(final TokenType type) {
    if (this.lexer.peek() == null) {
      return false;
    }
    return this.lexer.peek().type() == type;
  }

  public Lexer lexer() {
    return this.lexer;
  }

}
