package org.smoothbuild.lang.define;

import java.util.Objects;

import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.type.impl.FuncTS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class DefFuncS extends FuncS {
  private final ExprS body;

  public DefFuncS(FuncTS type, ModPath modPath, String name,
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
        && this.resT().equals(that.resT())
        && this.modPath().equals(that.modPath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.body.equals(that.body)
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resT(), modPath(), name(), params(), body, loc());
  }

  @Override
  public String toString() {
    return "DefFunc(`" + code() + "`)";
  }

  private String code() {
    return resT().name() + " " + name() + "(" + paramsToString() + ")" + " = ?";
  }
}
