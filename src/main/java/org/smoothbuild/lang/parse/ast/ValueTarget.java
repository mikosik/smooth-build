package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.type.Type;

public class ValueTarget implements RefTarget {
  private final Value value;

  public ValueTarget(Value value) {
    this.value = value;
  }

  @Override
  public Optional<Type> inferredType() {
    return Optional.of(value.type());
  }

  public Value value() {
    return value;
  }
}
