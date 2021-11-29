package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

public final class RealFuncN extends FunctionN {
  public RealFuncN(Optional<TypeN> type, String name, List<ItemN> params,
      Optional<ExprN> expr, Optional<AnnotationN> annotation, Location location) {
    super(type, name, expr, params, annotation, location);
  }
}
