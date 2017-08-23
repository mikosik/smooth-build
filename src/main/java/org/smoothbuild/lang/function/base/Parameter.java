package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

public class Parameter {
  private final Type type;
  private final String name;
  private final Expression defaultValue;
  private final HashCode nameHash;

  public Parameter(Type type, String name, Expression defaultValue) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.defaultValue = defaultValue;
    this.nameHash = Hash.string(name);
  }

  public Type type() {
    return type;
  }

  public String name() {
    return name;
  }

  public boolean isRequired() {
    return defaultValue == null;
  }

  public Expression defaultValueExpression() {
    return defaultValue;
  }

  public HashCode nameHash() {
    return nameHash;
  }

  public final boolean equals(Object object) {
    if (!(object instanceof Parameter)) {
      return false;
    }
    Parameter that = (Parameter) object;
    return this.type.equals(that.type) && this.name.equals(that.name)
        && this.isRequired() == that.isRequired();
  }

  public final int hashCode() {
    return 17 * type.hashCode() + name.hashCode();
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type.name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name, minNameLength, ' ');
    return typePart + namePart;
  }

  public String toString() {
    return "Param(" + type.name() + ": " + name + ")";
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
      result = Math.max(result, parameter.name().length());
    }
    return result;
  }
}
