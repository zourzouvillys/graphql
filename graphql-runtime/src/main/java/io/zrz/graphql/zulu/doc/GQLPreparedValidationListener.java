package io.zrz.graphql.zulu.doc;

import io.zrz.graphql.core.parser.GQLSourceRange;
import io.zrz.zulu.types.ZField;

public interface GQLPreparedValidationListener {

  void error(ZField field, GQLSourceRange location, String string);

}
