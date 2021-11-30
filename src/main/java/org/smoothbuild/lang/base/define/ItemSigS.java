package org.smoothbuild.lang.base.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NameableImpl;

import com.google.common.collect.ImmutableList;

/**
 * Item signature.
 *
 * This class is immutable.
 */
public class ItemSigS extends NameableImpl {
  private final TypeS type;
  private final Optional<TypeS> defaultValType;

  public ItemSigS(TypeS type, String name, Optional<TypeS> defaultValType) {
    this(type, Optional.of(name), defaultValType);
  }

  public ItemSigS(TypeS type, Optional<String> name, Optional<TypeS> defaultValType) {
    super(name);
    this.type = requireNonNull(type);
    this.defaultValType = requireNonNull(defaultValType);
  }

  public static ItemSigS itemSigS(TypeS type) {
    return new ItemSigS(type, empty(), empty());
  }

  public static ItemSigS itemSigS(String name, TypeS type) {
    return new ItemSigS(type, name, empty());
  }

  public static ImmutableList<ItemSigS> toItemSigS(List<? extends TypeS> types) {
    return map(types, ItemSigS::itemSigS);
  }

  public TypeS type() {
    return type;
  }

  public Optional<TypeS> defaultValType() {
    return defaultValType;
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
    return object instanceof ItemSigS that
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.nameO(), that.nameO())
        && Objects.equals(this.defaultValType, that.defaultValType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), nameO(), defaultValType);
  }
}
