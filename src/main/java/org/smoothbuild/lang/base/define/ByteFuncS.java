package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.expr.BytecodeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class ByteFuncS extends AnnFuncS {
  public ByteFuncS(BytecodeS ann, FuncTS type, ModPath modPath, String name, NList<ItemS> params,
      Loc loc) {
    super(type, modPath, name, params, ann, loc);
  }

  @Override
  public BytecodeS ann() {
    return ((BytecodeS) super.ann());
  }

  @Override
  public String toString() {
    return "ByteFunc(`" + code() + "`)";
  }
}
