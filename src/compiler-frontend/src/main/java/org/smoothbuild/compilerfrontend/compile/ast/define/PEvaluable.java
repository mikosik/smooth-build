package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public sealed interface PEvaluable extends HasLocation, PScoped permits PFunc, PNamedEvaluable {
  public SType sType();

  public SSchema schema();

  public Maybe<PExpr> body();

  public String q();
}
