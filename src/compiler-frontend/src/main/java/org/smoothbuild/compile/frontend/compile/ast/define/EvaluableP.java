package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compile.frontend.lang.base.location.Located;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

public sealed interface EvaluableP extends Located, ScopedP permits FuncP, NamedEvaluableP {
  public TypeS typeS();

  public SchemaS schemaS();

  public Maybe<ExprP> body();

  public String q();
}
