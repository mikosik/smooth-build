package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.expr.Annotation;

/**
 * This class is immutable.
 */
public class NativeValue extends Value {
  private final Annotation annotation;

  public NativeValue(Type type, ModulePath modulePath, String name, Annotation annotation,
      Location location) {
    super(type, modulePath, name, location);
    this.annotation = annotation;
  }

  public Annotation annotation() {
    return annotation;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NativeValue that
        && this.type().equals(that.type())
        && this.name().equals(that.name())
        && this.annotation().equals(that.annotation())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), annotation(), location());
  }

  @Override
  public String toString() {
    return annotation.toString() + " Value(`" + type().name() + " " + name() + "`)";
  }
}


