package io.zrz.graphql.zulu.executable;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import io.zrz.zulu.types.ZField;
import io.zrz.zulu.values.ZValue;

/**
 * a single input field in the parameters.
 *
 * @author theo
 *
 */

public final class ExecutableInputField implements ZField, ExecutableElement, ExecutableInput {

  private final ExecutableInputType inputType;
  private final @NonNull String fieldName;
  private final ExecutableTypeUse targetType;
  private final boolean isNullable;
  private final ZField zfield;

  public ExecutableInputField(final ExecutableInputType type, final ZField p, final BuildContext buildContext) {
    this.inputType = type;
    this.fieldName = p.fieldName();
    this.isNullable = p.isOptional();
    this.zfield = p;

    try {
      this.targetType = buildContext.use(this, p);
    }
    catch (final Throwable ex) {
      throw new RuntimeException("error building field '" + this.fieldName + "'" + " in '" + type.typeName() + "'", ex);
    }

  }

  @Override
  public TypeToken<?> javaType() {
    Preconditions.checkNotNull(null, "invalid", this.inputType, this.zfield);
    return null;
  }

  /**
   * the name of this parameter
   */

  @Override
  public @NonNull String fieldName() {
    return this.fieldName;
  }

  /**
   * the type for this parameter.
   */

  @Override
  public ExecutableTypeUse fieldType() {
    return Objects.requireNonNull(this.targetType);
  }

  /**
   *
   */

  @Override
  public String toString() {
    return "parameter " + this.zfield;
  }

  @Override
  public boolean isNullable() {
    return this.isNullable;
  }

  @Override
  public Optional<ZValue> defaultValue() {
    return ZField.super.defaultValue();
  }

}
