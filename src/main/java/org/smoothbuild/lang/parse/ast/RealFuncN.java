package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Loc;

public final class RealFuncN extends FuncN {
  public RealFuncN(Optional<TypeN> type, String name, List<ItemN> params,
      Optional<ExprN> expr, Optional<AnnN> annotation, Loc loc) {
    super(type, name, expr, params, annotation, loc);
  }
}
