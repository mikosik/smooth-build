package org.smoothbuild.lang.base.define;

import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class RealFunction extends Function {
  private final Expression body;

  public RealFunction(Type resultType, ModulePath modulePath, String name,
      ImmutableList<Item> parameters, Expression body, Location location) {
    super(resultType, modulePath, name, parameters, location);
    this.body = body;
  }

  public Expression body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof RealFunction that
        && this.resultType().equals(that.resultType())
        && this.modulePath().equals(that.modulePath())
        && this.name().equals(that.name())
        && this.parameters().equals(that.parameters())
        && this.body.equals(that.body)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), modulePath(), name(), parameters(), body, location());
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
