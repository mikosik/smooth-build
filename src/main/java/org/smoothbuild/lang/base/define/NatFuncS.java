package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.expr.AnnS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class NatFuncS extends FuncS {
  private final AnnS annS;

  public NatFuncS(FuncTS type, ModPath modPath, String name,
      NList<ItemS> params, AnnS annS, Loc loc) {
    super(type, modPath, name, params, loc);
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
        && this.resType().equals(that.resType())
        && this.modPath().equals(that.modPath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.annS.equals(that.annS)
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resType(), modPath(), name(), params(), annS, loc());
  }

  @Override
  public String toString() {
    return "NatFunc(`" + code() + "`)";
  }

  private String code() {
    return annS + " " + signature();
  }
}
