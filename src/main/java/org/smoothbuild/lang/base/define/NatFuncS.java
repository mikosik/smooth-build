package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.expr.NativeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class NatFuncS extends AnnFuncS {
  public NatFuncS(NativeS ann, FuncTS type, ModPath modPath, String name, NList<ItemS> params,
      Loc loc) {
    super(type, modPath, name, params, ann, loc);
  }

  @Override
  public NativeS ann() {
    return ((NativeS) super.ann());
  }

  @Override
  public String toString() {
    return "NatFunc(`" + code() + "`)";
  }
}
