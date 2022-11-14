package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tanal;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.util.collect.NList;

/**
 * Named function.
 */
public sealed abstract class NamedFuncS extends Tanal implements FuncS, NamedEvaluableS
    permits AnnFuncS, DefFuncS, SyntCtorS {
  private final NList<ItemS> params;

  public NamedFuncS(FuncTS type, String name, NList<ItemS> params, Loc loc) {
    super(type, name, loc);
    this.params = params;
  }

  @Override
  public FuncTS type() {
    return (FuncTS) super.type();
  }

  @Override
  public NList<ItemS> params() {
    return params;
  }
}
