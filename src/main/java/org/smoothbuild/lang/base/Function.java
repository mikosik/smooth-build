package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Function extends Callable {
  private final Optional<Expression> body;

  public Function(Type resultType, String name, ImmutableList<Item> parameters,
      Optional<Expression> body, Location location) {
    super(resultType, name, parameters, location);
    this.body = requireNonNull(body);
  }

  public Optional<Expression> body() {
    return body;
  }

  @Override
  public Expression createCallExpression(ImmutableList<Expression> arguments, Location location) {
    return new CallExpression(this, arguments, location);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Function that) {
      return this.resultType().equals(that.resultType())
          && this.name().equals(that.name())
          && this.parameters().equals(that.parameters())
          && this.body.equals(that.body)
          && this.location().equals(that.location());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), name(), parameters(), body, location());
  }

  @Override
  public String toString() {
    return "Function(`" + resultType() + "(" + parametersToString() + ")" + " = " + body + "`)";
  }

  private String parametersToString() {
    return parameters()
        .stream()
        .map(Object::toString)
        .collect(joining(", "));
  }
}