package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.base.location.Located;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public sealed interface EvaluableP extends Located, ScopedP permits FuncP, NamedEvaluableP {
  public SType typeS();

  public SchemaS schemaS();

  public Maybe<ExprP> body();

  public String q();
}
