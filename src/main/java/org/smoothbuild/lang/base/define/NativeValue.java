package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.NativeExpression;

/**
 * This class is immutable.
 */
public class NativeValue extends Value {
  private final NativeExpression nativ;

  public NativeValue(Type type, ModulePath modulePath, String name, NativeExpression nativ,
      Location location) {
    super(type, modulePath, name, location);
    this.nativ = nativ;
  }

  public NativeExpression nativ() {
    return nativ;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NativeValue that
        && this.type().equals(that.type())
        && this.name().equals(that.name())
        && this.nativ().equals(that.nativ())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), nativ(), location());
  }

  @Override
  public String toString() {
    return nativ.toString() + " Value(`" + type().name() + " " + name() + "`)";
  }
}


