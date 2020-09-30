package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.parse.ast.Named;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public record Item(Type type, String name, Optional<Expression> defaultValue, Location location)
    implements Named {
  public Item {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.defaultValue = requireNonNull(defaultValue);
    this.location = requireNonNull(location);
  }

  public ItemSignature signature() {
    return new ItemSignature(type, name, defaultValue.map(Expression::type), location);
  }

  @Override
  public String toString() {
    return "Item(`" + type().name() + " " + name() + defaultValueToString() + "`)";
  }

  private String defaultValueToString() {
    return defaultValue.map(v -> " = " + v.toString()).orElse("");
  }
}
