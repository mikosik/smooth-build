package org.smoothbuild.lang.base;

import static com.google.common.base.Strings.padEnd;

import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;

/**
 * This class is immutable.
 */
public class Parameter extends Item {
  private final Optional<Expression> defaultValue;

  public Parameter(int index, Type type, String name, Optional<Expression> defaultValue,
      Location location) {
    super(index, type, name, defaultValue.isPresent(), location);
    this.defaultValue = defaultValue;
  }

  public Optional<Expression> defaultValueExpression() {
    return defaultValue;
  }

  @Override
  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name(), minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return "Param(" + type().name() + ": " + name() + ")";
  }
}
