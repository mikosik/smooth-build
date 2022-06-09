package org.smoothbuild.lang.define;

import java.util.Objects;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.util.collect.NList;

/**
 * Defined function (function that has body).
 *
 * This class is immutable.
 */
public final class DefFuncS extends FuncS {
  private final MonoObjS body;

  public DefFuncS(FuncTS type, ModPath modPath, String name,
      NList<ItemS> params, MonoObjS body, Loc loc) {
    super(type, modPath, name, params, loc);
    this.body = body;
  }

  public MonoObjS body() {
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
    return signature() + " = ?";
  }
}
