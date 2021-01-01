package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public record Item(Type type, String name, Optional<Expression> defaultValue) {
  public Item {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.defaultValue = requireNonNull(defaultValue);
  }

  public ItemSignature signature() {
    return new ItemSignature(type, name, defaultValue.map(Expression::type));
  }

  @Override
  public String toString() {
    return "Item(`" + type().name() + " " + name() + defaultValueToString() + "`)";
  }

  private String defaultValueToString() {
    return defaultValue.map(v -> " = " + v.toString()).orElse("");
  }
}
