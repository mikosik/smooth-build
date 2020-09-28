package org.smoothbuild.lang.base.type;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.parse.ast.Named;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public record ItemSignature(Type type, String name, boolean hasDefaultValue, Location location)
    implements Named {
  public ItemSignature {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.hasDefaultValue = hasDefaultValue;
    this.location = requireNonNull(location);
  }

  /**
   * @return name of this parameter inside backtics.
   */
  public String q() {
    return "`" + name() + "`";
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