package org.smoothbuild.lang.base.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableList;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public class ItemSignature extends Named<Type> {
  private final Optional<Type> defaultValueType;

  public ItemSignature(Type type, String name, Optional<Type> defaultValueType) {
    this(type, Optional.of(name), defaultValueType);
  }

  public ItemSignature(Type type, Optional<String> name, Optional<Type> defaultValueType) {
    super(name, type);
    this.defaultValueType = requireNonNull(defaultValueType);
  }

  public static ItemSignature itemSignature(Type type) {
    return new ItemSignature(type, Optional.empty(), Optional.empty());
  }

  public static ImmutableList<ItemSignature> toItemSignatures(List<? extends Type> types) {
    return map(types, ItemSignature::itemSignature);
  }

  public Type type() {
    return object();
  }

  public Optional<Type> defaultValueType() {
    return defaultValueType;
  }

  public String typeAndName() {
    return name().map(n -> type().name() + " " + n).orElseGet(() -> type().name());
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(saneName(), minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return type().name() + name().map(n -> " " + n).orElse("");
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    return object instanceof ItemSignature that
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.defaultValueType, that.defaultValueType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), defaultValueType);
  }
}
