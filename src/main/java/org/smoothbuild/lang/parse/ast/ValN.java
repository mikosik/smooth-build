package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Loc;

public final class ValN extends EvalN {
  public ValN(Optional<TypeN> type, String name, Optional<ExprN> body,
      Optional<AnnN> annotation, Loc loc) {
    super(type, name, body, annotation, loc);
  }
}
