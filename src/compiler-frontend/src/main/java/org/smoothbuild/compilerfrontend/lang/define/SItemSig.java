package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import org.smoothbuild.compilerfrontend.lang.base.Named;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Item signature.
 * This class is immutable.
 */
public class SItemSig implements Named {
  private final SType type;
  private final String name;

  public SItemSig(SType type, String name) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
  }

  public static SItemSig itemSigS(SType type, String name) {
    return new SItemSig(type, name);
  }

  public SType type() {
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
    return object instanceof SItemSig that
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
