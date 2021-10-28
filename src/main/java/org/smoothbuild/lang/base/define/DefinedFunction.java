package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class DefinedFunction extends Function implements DefinedEvaluable {
  private final Expression body;

  public DefinedFunction(FunctionType type, ModulePath modulePath, String name,
      ImmutableList<Item> parameters, Expression body, Location location) {
    super(type, modulePath, name, parameters, location);
    this.body = body;
  }

  @Override
  public Expression body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof DefinedFunction that
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
    return "Function(`" + resultType()
        + "(" + Lists.toCommaSeparatedString(parameters()) + ")" + " = " + body + "`)";
  }
}
