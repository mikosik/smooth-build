package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.define.Loc;

public final class ValN extends EvalN {
  public ValN(Optional<TypeN> type, String name, Optional<ObjN> body,
      Optional<AnnN> annotation, Loc loc) {
    super(type, name, body, annotation, loc);
  }
}
