package org.smoothbuild.lang.base;

import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.Type;

public class Parameter extends ParameterInfo {
  private final Expression defaultValue;

  public Parameter(int index, Type type, String name, Expression defaultValue) {
    super(index, type, name, defaultValue == null);
    this.defaultValue = defaultValue;
  }

  public Expression defaultValueExpression() {
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
