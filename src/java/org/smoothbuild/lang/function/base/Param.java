package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.lang.type.Type.allowedForParam;

import java.util.Set;

import org.smoothbuild.lang.type.Type;

public class Param {
  private final Type type;
  private final String name;
  private final boolean isRequired;

  public static Param param(Type type, String name) {
    return param(type, name, false);
  }

  public static Param param(Type type, String name, boolean isRequired) {
    return new Param(type, name, isRequired);
  }

  protected Param(Type type, String name, boolean isRequired) {
    this.type = checkAllowedType(type);
    this.name = checkNotNull(name);
    this.isRequired = isRequired;
  }

  private Type checkAllowedType(Type type) {
    checkNotNull(type);
    checkArgument(allowedForParam().contains(type));
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

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof Param)) {
      return false;
    }
    Param that = (Param) object;
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

  public static String paramsToString(Set<Param> params) {
    int typeLength = longestParamType(params);
    int nameLength = longestParamName(params);

    StringBuilder builder = new StringBuilder();
    for (Param param : params) {
      builder.append("  " + param.toPaddedString(typeLength, nameLength) + "\n");
    }
    return builder.toString();
  }

  private static int longestParamType(Set<Param> params) {
    int result = 0;
    for (Param param : params) {
      result = Math.max(result, param.type().name().length());
    }
    return result;
  }

  private static int longestParamName(Set<Param> params) {
    int result = 0;
    for (Param param : params) {
      result = Math.max(result, param.name().length());
    }
    return result;
  }
}
