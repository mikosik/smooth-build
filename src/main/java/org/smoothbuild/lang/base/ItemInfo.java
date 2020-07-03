package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.lang.object.type.Type;

public class ItemInfo {
  private final int index;
  private final Type type;
  private final String name;
  private final boolean hasDefaultValue;

  public ItemInfo(int index, Type type, String name, boolean hasDefaultValue) {
    this.index = index;
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.hasDefaultValue = hasDefaultValue;
  }

  public int index() {
    return index;
  }

  public Type type() {
    return type;
  }

  public String name() {
    return name;
  }

  /**
   * @return single quoted name of this Parameter.
   */
  public String q() {
    return "'" + name() + "'";
  }

  public boolean hasDefaultValue() {
    return hasDefaultValue;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof ItemInfo that)) {
      return false;
    }
    return type.equals(that.type)
        && name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name(), minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return type.name() + " " + name;
  }

  public static String iterableToString(Iterable<ItemInfo> names) {
    int typeLength = longestType(names);
    int nameLength = longestName(names);
    return stream(names)
        .map(p -> "  " + p.toPaddedString(typeLength, nameLength) + "\n")
        .sorted()
        .collect(joining());
  }

  public static int longestType(Iterable<ItemInfo> names) {
    int result = 0;
    for (ItemInfo name : names) {
      result = Math.max(result, name.type.name().length());
    }
    return result;
  }

  public static int longestName(Iterable<ItemInfo> names) {
    int result = 0;
    for (ItemInfo name : names) {
      result = Math.max(result, name.name.length());
    }
    return result;
  }
}
