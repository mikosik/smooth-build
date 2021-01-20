package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Constructor extends Callable {
  public Constructor(Type resultType, String name, ImmutableList<Item> parameters,
      Location location) {
    super(resultType, name, parameters, location);
  }

  @Override
  public Expression createCallExpression(ImmutableList<Expression> arguments, Location location) {
    return new CallExpression(resultType(), this, arguments, location);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Constructor that) {
      return this.resultType().equals(that.resultType())
          && this.name().equals(that.name())
          && this.parameters().equals(that.parameters())
          && this.location().equals(that.location());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), name(), parameters(), location());
  }
}
