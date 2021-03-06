package io.zrz.zulu.schema.model;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.zrz.zulu.schema.ResolvedObjectOrInterfaceType;
import io.zrz.zulu.schema.ResolvedObjectType;
import io.zrz.zulu.schema.SchemaType;

public class ModelUtils {

  /**
   * checks if this type implements the specified one.
   *
   * if the type it is a union, returns true only if all types of the union implement the typename (so must be an
   * interface).
   *
   * if the typeName represents a union, will be true if the type is part of the union.
   *
   * @param type
   * @return
   */

  public static boolean isAssignableTo(final SchemaType type, final String typeName) {
    switch (type.typeKind()) {
      case INTERFACE:
        return (type.typeName().equals(typeName));
      case OBJECT:
        return requireNonNull(asObjectType(type))
            .interfaces()
            .stream()
            .anyMatch(t -> t.typeName().equals(typeName));
      case UNION:
      case ENUM:
      case INPUT_OBJECT:
      case SCALAR:
      default:
        break;
    }

    return false;

  }

  /**
   * returns object if it is one, else null.
   *
   * @param type
   * @return
   */

  public static @Nullable ResolvedObjectType asObjectType(final SchemaType type) {
    if (type instanceof ResolvedObjectType) {
      return (ResolvedObjectType) type;
    }
    return null;
  }

  public static <T> @NonNull T requireNonNull(final @Nullable T value) {
    if (value == null)
      throw new IllegalArgumentException();
    return value;
  }

  /**
   * returns as object or interface, else returns null.
   *
   * @param type
   * @return
   */

  public static @Nullable ResolvedObjectOrInterfaceType asObjectOrInterface(final SchemaType type) {
    if (type instanceof ResolvedObjectOrInterfaceType) {
      return (ResolvedObjectOrInterfaceType) type;
    }
    return null;
  }

}
