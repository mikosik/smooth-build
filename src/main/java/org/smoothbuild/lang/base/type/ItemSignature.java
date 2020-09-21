package org.smoothbuild.lang.base.type;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.parse.ast.Named;

/**
 * Item contains attributes common to both struct field and function parameter.
 * This class is immutable.
 */
public class ItemSignature implements Named {
  private final Type type;
  private final String name;
  private final boolean hasDefaultValue;
  private final Location location;

  public ItemSignature(Type type, String name, boolean hasDefaultValue, Location location) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.hasDefaultValue = hasDefaultValue;
    this.location = requireNonNull(location);
  }

  public Type type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * @return name of this parameter inside backtics.
   */
  public String q() {
    return "`" + name() + "`";
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
    if (!(object instanceof ItemSignature that)) {
      return false;
    }
    return this.type.equals(that.type)
        && this.name.equals(that.name)
        && this.hasDefaultValue == that.hasDefaultValue
        && this.location.equals(that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, hasDefaultValue, location);
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

  public static String iterableToString(Iterable<ItemSignature> names) {
    int typeLength = longestType(names);
    int nameLength = longestName(names);
    return stream(names)
        .map(p -> "  " + p.toPaddedString(typeLength, nameLength) + "\n")
        .sorted()
        .collect(joining());
  }

  public static int longestType(Iterable<ItemSignature> names) {
    int result = 0;
    for (ItemSignature name : names) {
      result = Math.max(result, name.type.name().length());
    }
    return result;
  }

  public static int longestName(Iterable<ItemSignature> names) {
    int result = 0;
    for (ItemSignature name : names) {
      result = Math.max(result, name.name.length());
    }
    return result;
  }
}
