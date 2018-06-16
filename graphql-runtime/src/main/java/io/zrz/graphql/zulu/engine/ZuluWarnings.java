package io.zrz.graphql.zulu.engine;

import org.apache.commons.lang3.StringUtils;

import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.engine.ZuluWarning.DocumentWarning;
import io.zrz.graphql.zulu.engine.ZuluWarning.ExecutionError;
import io.zrz.graphql.zulu.engine.ZuluWarning.OutputFieldWarning;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableType;

public class ZuluWarnings {

  public static String format(ZuluWarning warning, String template) {

    ExecutableOutputField efield = null;

    if (warning instanceof OutputFieldWarning) {
      efield = ((OutputFieldWarning) warning).element();
    }

    String operationName = null;

    if (warning instanceof DocumentWarning) {
      operationName = ((DocumentWarning) warning).operationName();
    }

    ZuluSelection selection = null;

    if (warning instanceof ExecutionError) {
      selection = ((ExecutionError) warning).selection();
    }

    ExecutableType type = warning.context();
    GQLPreparedSelection field = warning.selection();

    return StringUtils.replaceEach(
        template,
        new String[] {
            "${field.name}",
            "${type.name}",
            "${field.type.name}",
            "${operation.name}"
        },
        new String[] {
            field == null ? "" : field.fieldName(),
            type == null ? "" : type.typeName(),
            (efield == null ? "<unknown>" : efield.fieldType().logicalType()),
            operationName == null ? "<default>" : operationName
        });

  }

}
