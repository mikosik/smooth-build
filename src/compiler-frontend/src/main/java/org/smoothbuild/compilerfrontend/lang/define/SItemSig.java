package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Item signature.
 * This class is immutable.
 */
public class SItemSig implements Identifiable {
  private final SType type;
  private final Id id;

  public SItemSig(SType type, Id id) {
    this.type = requireNonNull(type);
    this.id = requireNonNull(id);
  }

  public static SItemSig itemSigS(SType type, Id id) {
    return new SItemSig(type, id);
  }

  public SType type() {
    return type;
  }

  @Override
  public Id id() {
    return id;
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String typePart = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String namePart = padEnd(id.full(), minNameLength, ' ');
    return typePart + namePart;
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    return object instanceof SItemSig that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, id);
  }

  @Override
  public String toString() {
    return type().name() + " " + id;
  }
}
