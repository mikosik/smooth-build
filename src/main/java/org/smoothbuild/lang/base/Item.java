package org.smoothbuild.lang.base;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.parse.ast.Named;

/**
 * Item contains attributes common to both struct field and function parameter.
 */
public class Item implements Named {
  private final int index;
  private final Type type;
  private final String name;
  private final boolean hasDefaultValue;
  private final Location location;

  public Item(int index, Type type, String name, boolean hasDefaultValue, Location location) {
    this.index = index;
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.hasDefaultValue = hasDefaultValue;
    this.location = requireNonNull(location);
  }

  public int index() {
    return index;
  }

  public Type type() {
    return type;
  }

  @Override
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
  public Location location() {
    return location;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Item that)) {
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

  public static String iterableToString(Iterable<Item> names) {
    int typeLength = longestType(names);
    int nameLength = longestName(names);
    return stream(names)
        .map(p -> "  " + p.toPaddedString(typeLength, nameLength) + "\n")
        .sorted()
        .collect(joining());
  }

  public static int longestType(Iterable<Item> names) {
    int result = 0;
    for (Item name : names) {
      result = Math.max(result, name.type.name().length());
    }
    return result;
  }

  public static int longestName(Iterable<Item> names) {
    int result = 0;
    for (Item name : names) {
      result = Math.max(result, name.name.length());
    }
    return result;
  }
}
