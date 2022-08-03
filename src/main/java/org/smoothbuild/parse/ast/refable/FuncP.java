package org.smoothbuild.parse.ast.refable;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nlistWithNonUniqueNames;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.parse.ast.AnnP;
import org.smoothbuild.parse.ast.expr.ExprP;
import org.smoothbuild.parse.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class FuncP extends PolyRefableP {
  private final Optional<TypeP> resT;
  private final NList<ItemP> params;

  public FuncP(Optional<TypeP> resT, String name, List<ItemP> params, Optional<ExprP> body,
      Optional<AnnP> ann, Loc loc) {
    this(resT, name, nlistWithNonUniqueNames(ImmutableList.copyOf(params)), body, ann, loc);
  }

  public FuncP(Optional<TypeP> resT, String name, NList<ItemP> params, Optional<ExprP> body,
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
    return resT;
  }

  public ImmutableList<TypeS> paramTs() {
    return map(params(), ItemP::typeS);
  }
}
