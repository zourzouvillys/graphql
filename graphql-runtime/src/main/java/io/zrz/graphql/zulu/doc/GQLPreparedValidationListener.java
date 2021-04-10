package io.zrz.graphql.zulu.doc;

import java.util.Optional;

import io.zrz.graphql.core.parser.GQLSourceRange;
import io.zrz.zulu.types.ZField;

public interface GQLPreparedValidationListener {

  void error(ZField field, Optional<GQLSourceRange> location, String string);

}
