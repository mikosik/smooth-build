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

  public static String parametersToString(Iterable<Parameter> parameters) {
    int typeLength = longestParameterType(parameters);
    int nameLength = longestParameterName(parameters);

    StringBuilder builder = new StringBuilder();
    for (Parameter parameter : parameters) {
      builder.append("  " + parameter.toPaddedString(typeLength, nameLength) + "\n");
    }
    return builder.toString();
  }

  private static int longestParameterType(Iterable<Parameter> parameters) {
    int result = 0;
    for (Parameter parameter : parameters) {
      result = Math.max(result, parameter.type().name().length());
    }
    return result;
  }

  private static int longestParameterName(Iterable<Parameter> parameters) {
    int result = 0;
    for (Parameter parameter : parameters) {
      result = Math.max(result, parameter.name().toString().length());
    }
    return result;
  }
}
