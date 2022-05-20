package org.smoothbuild.lang.type;

import com.google.common.collect.ImmutableList;

/**
 * Polymorphic function type.
 */
public final class PolyFuncTS extends PolyTS implements FuncTS {
  public PolyFuncTS(VarSetS freeVars, MonoFuncTS monoFuncTS) {
    super(freeVars, monoFuncTS);
  }

  public static PolyFuncTS polyFuncTS(MonoFuncTS monoFuncTS) {
    return new PolyFuncTS(monoFuncTS.vars(), monoFuncTS);
  }

  @Override
  public MonoFuncTS mono() {
    return (MonoFuncTS) super.mono();
  }

  @Override
  public MonoTS res() {
    return mono().res();
  }

  @Override
  public ImmutableList<MonoTS> params() {
    return mono().params();
  }
}
