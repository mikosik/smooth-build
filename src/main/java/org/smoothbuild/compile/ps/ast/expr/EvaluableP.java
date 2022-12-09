package org.smoothbuild.compile.ps.ast.expr;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Located;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypeS;

public sealed interface EvaluableP
    extends Located
    permits FuncP, NamedEvaluableP {
  public TypeS typeS();

  public SchemaS schemaS();

  public Optional<ExprP> body();

  public String q();
}
