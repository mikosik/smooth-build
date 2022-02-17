package org.smoothbuild.lang.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.type.impl.TypeS;
import org.smoothbuild.util.collect.NameableImpl;

import com.google.common.collect.ImmutableList;

/**
 * Item signature.
 *
 * This class is immutable.
 */
public class ItemSigS extends NameableImpl {
  private final TypeS type;
  private final Optional<TypeS> defaultValT;

  public ItemSigS(TypeS type, String name, Optional<TypeS> defaultValT) {
    this(type, Optional.of(name), defaultValT);
  }

  public ItemSigS(TypeS type, Optional<String> name, Optional<TypeS> defaultValT) {
    super(name);
    this.type = requireNonNull(type);
    this.defaultValT = requireNonNull(defaultValT);
  }

  public static ItemSigS itemSigS(TypeS type) {
    return new ItemSigS(type, empty(), empty());
  }

  public static ItemSigS itemSigS(TypeS type, String name) {
    return new ItemSigS(type, name, empty());
  }

  public static ImmutableList<ItemSigS> toItemSigS(List<? extends TypeS> types) {
    return map(types, ItemSigS::itemSigS);
  }

  public TypeS type() {
    return type;
  }

  public Optional<TypeS> defaultValT() {
    return defaultValT;
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
        && Objects.equals(this.defaultValT, that.defaultValT);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), nameO(), defaultValT);
  }
}
