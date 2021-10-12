package org.smoothbuild.lang.base.type.api;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

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

  public static ItemSignature itemSignature(Type type) {
    return new ItemSignature(type, Optional.empty(), Optional.empty());
  }

  public static ImmutableList<ItemSignature> toItemSignatures(List<Type> types) {
    return map(types, ItemSignature::itemSignature);
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

  public String typeAndName() {
    return name.map(n -> type.name() + " " + n).orElseGet(type::name);
  }
}
