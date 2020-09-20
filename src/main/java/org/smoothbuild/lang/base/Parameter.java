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

  public Parameter(Type type, String name, Optional<Expression> defaultValue, Location location) {
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
