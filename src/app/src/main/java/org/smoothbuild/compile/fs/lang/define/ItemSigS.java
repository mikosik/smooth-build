package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.smoothbuild.common.collect.Named;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * Item signature.
 * This class is immutable.
 */
public class ItemSigS implements Named {
  private final TypeS type;
  private final String name;

  public ItemSigS(TypeS type, String name) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
  }

  public static ItemSigS itemSigS(TypeS type, String name) {
    return new ItemSigS(type, name);
  }

  public TypeS type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name, minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    return object instanceof ItemSigS that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }

  @Override
  public String toString() {
    return type().name() + " " + name;
  }
}
