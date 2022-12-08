package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Located;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.ps.ast.expr.ExprP;

public interface EvaluableP extends Located {
  public TypeS typeS();

  public SchemaS schemaS();

  public Optional<ExprP> body();

  public String q();
}
