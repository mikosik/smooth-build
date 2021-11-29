package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

public final class ValueN extends EvaluableN {
  public ValueN(Optional<TypeN> type, String name, Optional<ExprN> body,
      Optional<AnnotationN> annotation, Location location) {
    super(type, name, body, annotation, location);
  }
}
