package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class DefinedFunctionS extends FunctionS implements DefinedEvaluableS {
  private final ExprS body;

  public DefinedFunctionS(FunctionTypeS type, ModulePath modulePath, String name,
      NList<Item> parameters, ExprS body, Location location) {
    super(type, modulePath, name, parameters, location);
    this.body = body;
  }

  @Override
  public ExprS body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof DefinedFunctionS that
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
    return "DefinedFunction(`" + code() + "`)";
  }

  private String code() {
    return resultType().name() + " " + name() + "(" + parametersToString() + ")" + " = ?";
  }
}