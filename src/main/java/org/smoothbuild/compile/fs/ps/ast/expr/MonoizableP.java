package org.smoothbuild.compile.fs.ps.ast.expr;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

/**
 * Expression that can be monomorphized.
 */
public abstract sealed class MonoizableP extends ExprP
    permits AnonymousFuncP, RefP {
  private ImmutableMap<VarS, TypeS> monoizeVarMap;

  public MonoizableP(Location location) {
    super(location);
  }

  public abstract SchemaS schemaS();

  public void setMonoizeVarMap(ImmutableMap<VarS, TypeS> monoizeVarMap) {
    this.monoizeVarMap = monoizeVarMap;
  }

  public ImmutableMap<VarS, TypeS> monoizeVarMap() {
    return monoizeVarMap;
  }
}
