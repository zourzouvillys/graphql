package io.zrz.zulu.schema;

import org.eclipse.jdt.annotation.NonNull;

public interface ResolvedObjectOrInterfaceType extends SchemaElement {

  @Override
  ResolvedSchema schema();

  ResolvedObjectField field(@NonNull String fieldName);

}
