package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;

import java.util.Objects;

import org.smoothbuild.lang.type.Type;

public class ParameterInfo {
  private final Type type;
  private final Name name;

  public ParameterInfo(Type type, Name name) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
  }

  public Type type() {
    return type;
  }

  public Name name() {
    return name;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof ParameterInfo)) {
      return false;
    }
    ParameterInfo that = (ParameterInfo) object;
    return type.equals(that.type)
        && name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name().toString(), minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return type.toString() + " " + name.toString();
  }

  public static String iterableToString(Iterable<ParameterInfo> names) {
    int typeLength = longestType(names);
    int nameLength = longestName(names);

    StringBuilder builder = new StringBuilder();
    for (ParameterInfo name : names) {
      builder.append("  " + name.toPaddedString(typeLength, nameLength) + "\n");
    }
    return builder.toString();
  }

  public static int longestType(Iterable<ParameterInfo> names) {
    int result = 0;
    for (ParameterInfo name : names) {
      result = Math.max(result, name.type.name().length());
    }
    return result;
  }

  public static int longestName(Iterable<ParameterInfo> names) {
    int result = 0;
    for (ParameterInfo name : names) {
      result = Math.max(result, name.name.toString().length());
    }
    return result;
  }
}
