package org.smoothbuild.lang.function.base;

import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.Type;

public class Parameter extends TypedName {
  private final Expression defaultValue;

  public Parameter(Type type, Name name, Expression defaultValue) {
    super(type, name);
    this.defaultValue = defaultValue;
  }

  public boolean isRequired() {
    return defaultValue == null;
  }

  public Expression defaultValueExpression() {
    return defaultValue;
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name().toString(), minNameLength, ' ');
    return typePart + namePart;
  }

  public String toString() {
    return "Param(" + type().name() + ": " + name() + ")";
  }
}
