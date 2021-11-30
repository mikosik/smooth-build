package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class DefFuncS extends FuncS {
  private final ExprS body;

  public DefFuncS(FuncTypeS type, ModPath modPath, String name,
      NList<ItemS> params, ExprS body, Loc loc) {
    super(type, modPath, name, params, loc);
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
    return object instanceof DefFuncS that
        && this.resType().equals(that.resType())
        && this.modPath().equals(that.modPath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.body.equals(that.body)
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resType(), modPath(), name(), params(), body, loc());
  }

  @Override
  public String toString() {
    return "DefFunc(`" + code() + "`)";
  }

  private String code() {
    return resType().name() + " " + name() + "(" + paramsToString() + ")" + " = ?";
  }
}
