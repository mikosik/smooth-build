package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Type.inferVariableBounds;
import static org.smoothbuild.lang.expr.Expression.toTypes;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Function extends Callable {
  private final Body body;

  public Function(Type resultType, String name, ImmutableList<Item> parameters,
      Body body, Location location) {
    super(resultType, name, parameters, location);
    this.body = requireNonNull(body);
  }

  public Body body() {
    return body;
  }

  @Override
  public Expression createCallExpression(ImmutableList<Expression> arguments, Location location) {
    Type resultType = inferResultType(arguments);
    return new CallExpression(resultType, this, arguments, location);
  }

  private Type inferResultType(ImmutableList<Expression> arguments) {
    var variableToBounds =
        inferVariableBounds(type().parameterTypes(), toTypes(arguments), LOWER);
    return resultType().mapVariables(variableToBounds, LOWER);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Function that
        && this.resultType().equals(that.resultType())
        && this.name().equals(that.name())
        && this.parameters().equals(that.parameters())
        && this.body.equals(that.body)
        && this.location().equals(that.location());
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
