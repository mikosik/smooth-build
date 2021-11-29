package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

public final class ValN extends EvalN {
  public ValN(Optional<TypeN> type, String name, Optional<ExprN> body,
      Optional<AnnN> annotation, Location location) {
    super(type, name, body, annotation, location);
  }
}
