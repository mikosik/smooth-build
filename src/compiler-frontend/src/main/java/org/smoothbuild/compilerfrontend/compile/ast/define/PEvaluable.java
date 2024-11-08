package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Located;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public sealed interface PEvaluable extends Located, PScoped permits PFunc, PNamedEvaluable {
  public SType typeS();

  public SchemaS schemaS();

  public Maybe<PExpr> body();

  public String q();
}
