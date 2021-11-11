package org.smoothbuild.lang.base.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NameableImpl;

import com.google.common.collect.ImmutableList;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public class ItemSignature extends NameableImpl {
  private final TypeS type;
  private final Optional<TypeS> defaultValueType;

  public ItemSignature(TypeS type, String name, Optional<TypeS> defaultValueType) {
    this(type, Optional.of(name), defaultValueType);
  }

  public ItemSignature(TypeS type, Optional<String> name, Optional<TypeS> defaultValueType) {
    super(name);
    this.type = requireNonNull(type);
    this.defaultValueType = requireNonNull(defaultValueType);
  }

  public static ItemSignature itemSignature(TypeS type) {
    return new ItemSignature(type, Optional.empty(), Optional.empty());
  }

  public static ImmutableList<ItemSignature> toItemSignatures(List<? extends TypeS> types) {
    return map(types, ItemSignature::itemSignature);
  }

  public TypeS type() {
    return type;
  }

  public Optional<TypeS> defaultValueType() {
    return defaultValueType;
  }

  public String typeAndName() {
    return nameO().map(n -> type().name() + " " + n).orElseGet(() -> type().name());
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(nameSane(), minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public String toString() {
    return type().name() + nameO().map(n -> " " + n).orElse("");
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    return object instanceof ItemSignature that
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.nameO(), that.nameO())
        && Objects.equals(this.defaultValueType, that.defaultValueType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), nameO(), defaultValueType);
  }
}
