package org.smoothbuild.lang.base;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ValueReferenceExpression;

/**
 * This class is immutable.
 */
public class Value extends Declared {
  private final Optional<Expression> body;

  public Value(Type type, String name, Optional<Expression> body, Location location) {
    super(type, name, location);
    this.body = body;
  }

  public Optional<Expression> body() {
    return body;
  }

  public Expression createReferenceExpression(Location location) {
    return new ValueReferenceExpression(name(), type(), location);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Value that) {
      return this.type().equals(that.type())
          && this.name().equals(that.name())
          && this.body().equals(that.body())
          && this.location().equals(that.location());
    }
    return false;
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


