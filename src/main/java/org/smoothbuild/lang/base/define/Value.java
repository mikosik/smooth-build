package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;

/**
 * This class is immutable.
 */
public class Value extends Referencable {
  private final Body body;

  public Value(Type type, String name, Body body, Location location) {
    super(type, name, location);
    this.body = body;
  }

  public Body body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Value that
        && this.type().equals(that.type())
        && this.name().equals(that.name())
        && this.body().equals(that.body())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), body(), location());
  }

  @Override
  public String toString() {
    return "Value(`" + type().name() + " " + name() + " = " + body + "`) @" + location();
  }
}


