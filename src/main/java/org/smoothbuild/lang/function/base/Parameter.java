package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.lang.type.Types.parameterTypes;

import java.util.Set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.util.LineBuilder;

import com.google.common.hash.HashCode;

public class Parameter {
  private final Type type;
  private final String name;
  private final boolean isRequired;
  private final HashCode nameHash;

  public static Parameter optionalParameter(Type type, String name) {
    return parameter(type, name, false);
  }

  public static Parameter requiredParameter(Type type, String name) {
    return parameter(type, name, true);
  }

  public static Parameter parameter(Type type, String name, boolean isRequired) {
    return new Parameter(type, name, isRequired);
  }

  protected Parameter(Type type, String name, boolean isRequired) {
    this.type = checkAllowedType(type);
    this.name = checkNotNull(name);
    this.isRequired = isRequired;
    this.nameHash = Hash.string(name);
  }

  private Type checkAllowedType(Type type) {
    checkNotNull(type);
    checkArgument(parameterTypes().contains(type));
    return type;
  }

  public Type type() {
    return type;
  }

  public String name() {
    return name;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public HashCode nameHash() {
    return nameHash;
  }

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof Parameter)) {
      return false;
    }
    Parameter that = (Parameter) object;
    return this.type.equals(that.type) && this.name.equals(that.name)
        && this.isRequired == that.isRequired;
  }

  @Override
  public final int hashCode() {
    return 17 * type.hashCode() + name.hashCode();
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type.name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name, minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return "Param(" + type.name() + ": " + name + ")";
  }

  public static String parametersToString(Set<Parameter> parameters) {
    int typeLength = longestParameterType(parameters);
    int nameLength = longestParameterName(parameters);

    LineBuilder builder = new LineBuilder();
    for (Parameter parameter : parameters) {
      builder.addLine("  " + parameter.toPaddedString(typeLength, nameLength));
    }
    return builder.build();
  }

  private static int longestParameterType(Set<Parameter> parameters) {
    int result = 0;
    for (Parameter parameter : parameters) {
      result = Math.max(result, parameter.type().name().length());
    }
    return result;
  }

  private static int longestParameterName(Set<Parameter> parameters) {
    int result = 0;
    for (Parameter parameter : parameters) {
      result = Math.max(result, parameter.name().length());
    }
    return result;
  }
}
