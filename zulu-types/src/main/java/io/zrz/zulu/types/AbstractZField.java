package io.zrz.zulu.types;

import java.util.Optional;

public abstract class AbstractZField implements ZField {

  @Override
  public String toString() {
    return fieldType() + (

    Optional.ofNullable(constantValue().orElse(defaultValue().orElse(null)))

        .map(val -> " = " + val).orElse("")

    );
  }

}
