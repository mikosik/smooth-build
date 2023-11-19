package org.smoothbuild.compile.frontend.compile.ast.define;

import java.util.Optional;
import org.smoothbuild.compile.frontend.lang.base.location.Located;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

public sealed interface EvaluableP extends Located, ScopedP permits FuncP, NamedEvaluableP {
  public TypeS typeS();

  public SchemaS schemaS();

  public Optional<ExprP> body();

  public String q();
}
