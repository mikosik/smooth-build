package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.expr.AnnS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class NatFuncS extends FuncS {
  private final AnnS annS;

  public NatFuncS(FuncTypeS type, ModulePath modulePath, String name,
      NList<Item> params, AnnS annS, Location location) {
    super(type, modulePath, name, params, location);
    this.annS = annS;
  }

  public AnnS ann() {
    return annS;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NatFuncS that
        && this.resultType().equals(that.resultType())
        && this.modulePath().equals(that.modulePath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.annS.equals(that.annS)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), modulePath(), name(), params(), annS, location());
  }

  @Override
  public String toString() {
    return "NatFunc(`" + code() + "`)";
  }

  private String code() {
    return annS + " " + signature();
  }
}
