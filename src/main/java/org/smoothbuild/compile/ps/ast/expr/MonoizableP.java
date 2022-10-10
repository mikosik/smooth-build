package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Operation that can be monomorphized.
 */
public abstract sealed class MonoizableP extends OperP
    permits DefaultArgP, RefP {
  private ImmutableMap<VarS, TypeS> monoizeVarMap;

  public MonoizableP(Loc loc) {
    super(loc);
  }

  public void setMonoizeVarMap(ImmutableMap<VarS, TypeS> monoizeVarMap) {
    this.monoizeVarMap = monoizeVarMap;
  }

  public ImmutableMap<VarS, TypeS> monoizeVarMap() {
    return monoizeVarMap;
  }
}
