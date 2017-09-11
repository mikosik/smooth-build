package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;

import java.util.Objects;

import org.smoothbuild.lang.type.Type;

public class TypedName {
  private final Type type;
  private final Name name;

  public TypedName(Type type, Name name) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
  }

  public Type type() {
    return type;
  }

  public Name name() {
    return name;
  }

  public boolean equals(Object object) {
    if (!(object instanceof TypedName)) {
      return false;
    }
    TypedName that = (TypedName) object;
    return type.equals(that.type)
        && name.equals(that.name);
  }

  public int hashCode() {
    return Objects.hash(type, name);
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name().toString(), minNameLength, ' ');
    return typePart + namePart;
  }

  public String toString() {
    return type.toString() + " " + name.toString();
  }

  public static String iterableToString(Iterable<TypedName> names) {
    int typeLength = longestType(names);
    int nameLength = longestName(names);

    StringBuilder builder = new StringBuilder();
    for (TypedName name : names) {
      builder.append("  " + name.toPaddedString(typeLength, nameLength) + "\n");
    }
    return builder.toString();
  }

  public static int longestType(Iterable<TypedName> names) {
    int result = 0;
    for (TypedName name : names) {
      result = Math.max(result, name.type.name().length());
    }
    return result;
  }

  public static int longestName(Iterable<TypedName> names) {
    int result = 0;
    for (TypedName name : names) {
      result = Math.max(result, name.name.toString().length());
    }
    return result;
  }
}
