package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.ExprS;

/**
 * This class is immutable.
 */
public final class DefinedValueS extends ValueS {
  private final ExprS body;

  public DefinedValueS(TypeS type, ModulePath modulePath, String name, ExprS body,
      Location location) {
    super(type, modulePath, name, location);
    this.body = body;
  }

  public ExprS body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof DefinedValueS that
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
    return "Value(`" + type().name() + " " + name() + " = " + body + "`)";
  }
}


