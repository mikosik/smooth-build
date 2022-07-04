package org.smoothbuild.util.bindings;

import static java.util.Objects.requireNonNullElse;

import java.util.Optional;

public record Bound<V>(Optional<V> value) {
  public Bound() {
    this(null);
  }

  public boolean isMissing() {
    return value == null;
  }

  public Optional<V> toOptional() {
    return requireNonNullElse(value, Optional.empty());
  }
}
