package org.smoothbuild.lang.function.base;

import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.util.Dag;

public class Parameter extends ParameterInfo {
  private final Dag<Expression> defaultValue;

  public Parameter(Type type, Name name, Dag<Expression> defaultValue) {
    super(type, name);
    this.defaultValue = defaultValue;
  }

  public boolean isRequired() {
    return defaultValue == null;
  }

  public Dag<Expression> defaultValueExpression() {
    return defaultValue;
  }

  @Override
  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name().toString(), minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return "Param(" + type().name() + ": " + name() + ")";
  }
}
