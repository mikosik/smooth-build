package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Located;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public sealed interface PEvaluable extends Located, PScoped permits PFunc, PNamedEvaluable {
  public SType sType();

  public SSchema sSchema();

  public Maybe<PExpr> body();

  public String q();
}
