package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.expr.NativeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class NatFuncS extends FuncS {
  private final NativeS ann;

  public NatFuncS(FuncTS type, ModPath modPath, String name, NList<ItemS> params, NativeS ann,
      Loc loc) {
    super(type, modPath, name, params, loc);
    this.ann = ann;
  }

  public NativeS ann() {
    return ann;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NatFuncS that
        && this.resT().equals(that.resT())
        && this.modPath().equals(that.modPath())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.ann.equals(that.ann)
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resT(), modPath(), name(), params(), ann, loc());
  }

  @Override
  public String toString() {
    return "NatFunc(`" + code() + "`)";
  }

  private String code() {
    return ann + " " + signature();
  }
}
