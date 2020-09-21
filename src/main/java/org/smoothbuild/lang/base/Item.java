package org.smoothbuild.lang.base;

import java.util.Optional;

import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public class Item extends ItemSignature {
  private final Optional<Expression> defaultValue;

  public Item(Type type, String name, Optional<Expression> defaultValue, Location location) {
    super(type, name, defaultValue.isPresent(), location);
    this.defaultValue = defaultValue;
  }

  public Optional<Expression> defaultValueExpression() {
    return defaultValue;
  }

  @Override
  public String toString() {
    return "Parameter(`" + type().name() + " " + name() + "`)";
  }
}
