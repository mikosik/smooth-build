package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import org.smoothbuild.compilerfrontend.lang.base.HasName;
import org.smoothbuild.compilerfrontend.lang.base.Name;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Item signature.
 * This class is immutable.
 */
public class SItemSig implements HasName {
  private final SType type;
  private final Name name;

  public SItemSig(SType type, Name name) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
  }

  public SType type() {
    return type;
  }

  public Name id() {
    return name();
  }

  @Override
  public Name name() {
    return name;
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(name.full(), minNameLength, ' ');
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
