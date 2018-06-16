package io.zrz.graphql.core.parser;

import java.io.InputStream;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.lang.GQLTypeRegistry;

public interface GQLParser {

  default GQLDocument parse(final String doc) {
    return parse(doc, GQLSourceInput.emptySource());
  }

  GQLDocument parse(String doc, GQLSourceInput source);

  GQLTypeRegistry parseSchema(String input, GQLSourceInput emptySource);

  GQLOperationDefinition parseQuery(String string);

  GQLTypeRegistry parseSchema(InputStream schema, GQLSourceInput source);

}
