package org.smoothbuild.lang.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.util.collect.NameableImpl;

/**
 * Item signature.
 *
 * This class is immutable.
 */
public class ItemSigS extends NameableImpl {
  private final MonoTS type;

  public ItemSigS(MonoTS type, String name) {
    this(type, Optional.of(name));
  }

  public ItemSigS(MonoTS type, Optional<String> name) {
    super(name);
    this.type = requireNonNull(type);
  }

  public static ItemSigS itemSigS(MonoTS type) {
    return new ItemSigS(type, empty());
  }

  public static ItemSigS itemSigS(MonoTS type, String name) {
    return new ItemSigS(type, name);
  }

  public MonoTS type() {
    return type;
  }

  public String typeAndName() {
    return nameO()
        .map(n -> type().name() + " " + n)
        .orElseGet(() -> type().name());
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
        && Objects.equals(this.nameO(), that.nameO());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), nameO());
  }
}
