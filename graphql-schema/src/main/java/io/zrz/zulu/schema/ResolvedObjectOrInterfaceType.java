package io.zrz.zulu.schema;

import org.eclipse.jdt.annotation.NonNull;

public interface ResolvedObjectOrInterfaceType extends SchemaElement, SchemaType {

  @Override
  ResolvedSchema schema();

  ResolvedObjectField field(@NonNull String fieldName);

}
