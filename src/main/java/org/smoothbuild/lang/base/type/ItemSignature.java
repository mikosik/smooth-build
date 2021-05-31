package org.smoothbuild.lang.base.type;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.Optional;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public record ItemSignature(Type type, Optional<String> name, Optional<Type> defaultValueType) {
  public ItemSignature(Type type, String name, Optional<Type> defaultValueType) {
    this(type, Optional.of(name), defaultValueType);
  }

  public ItemSignature(Type type, Optional<String> name, Optional<Type> defaultValueType) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.defaultValueType = requireNonNull(defaultValueType);
  }

  public boolean hasDefaultValue() {
    return defaultValueType.isPresent();
  }

  /**
   * @return name of this parameter inside backticks.
   */
  public String q() {
    return "`" + saneName() + "`";
  }

  public String saneName() {
    return name.orElse("");
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type.name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(saneName(), minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return type.name() + name.map(n -> " " + n).orElse("");
  }

  public static String iterableToString(Iterable<ItemSignature> items) {
    int typeLength = longestType(items);
    int nameLength = longestName(items);
    return stream(items)
        .map(p -> "  " + p.toPaddedString(typeLength, nameLength) + "\n")
        .sorted()
        .collect(joining());
  }

  public static int longestType(Iterable<ItemSignature> items) {
    int result = 0;
    for (ItemSignature item : items) {
      result = Math.max(result, item.type.name().length());
    }
    return result;
  }

  public static int longestName(Iterable<ItemSignature> items) {
    int result = 0;
    for (ItemSignature item : items) {
      result = Math.max(result, item.saneName().length());
    }
    return result;
  }
}
