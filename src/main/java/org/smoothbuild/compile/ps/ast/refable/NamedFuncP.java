package org.smoothbuild.compile.ps.ast.refable;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.ps.ast.AnnP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class NamedFuncP extends EvaluableP {
  private final Optional<TypeP> resT;
  private final NList<ItemP> params;

  public NamedFuncP(Optional<TypeP> resT, String name, NList<ItemP> params, Optional<ExprP> body,
      Optional<AnnP> ann, Loc loc) {
    super(name, body, ann, loc);
    this.resT = resT;
    this.params = params;
  }

  public Optional<TypeP> resT() {
    return resT;
  }

  public NList<ItemP> params() {
    return params;
  }

  @Override
  public Optional<TypeP> evalT() {
    return resT();
  }

  public ImmutableList<TypeS> paramTs() {
    return map(params(), ItemP::typeS);
  }
}
