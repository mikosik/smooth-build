package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.lang.type.Type;

public class ParameterInfo {
  private final Type type;
  private final String name;
  private final boolean isRequired;

  public ParameterInfo(Type type, String name, boolean isRequired) {
    this.isRequired = isRequired;
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
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
    return type.name() + " " + name.toString();
  }

  public static String iterableToString(Iterable<ParameterInfo> names) {
    int typeLength = longestType(names);
    int nameLength = longestName(names);
    return stream(names)
        .map(p -> "  " + p.toPaddedString(typeLength, nameLength) + "\n")
        .sorted()
        .collect(joining());
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
