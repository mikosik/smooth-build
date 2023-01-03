package org.smoothbuild.compile.fs.ps.ast.define;

import java.util.Optional;

import org.smoothbuild.compile.fs.lang.base.location.Located;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;

public sealed interface EvaluableP
    extends Located
    permits FuncP, NamedEvaluableP {
  public TypeS typeS();

  public SchemaS schemaS();

  public Optional<ExprP> body();

  public String q();
}
