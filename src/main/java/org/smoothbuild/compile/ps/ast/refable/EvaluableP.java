package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.ps.ast.expr.ExprP;

public interface EvaluableP extends WithLoc {
  public TypeS typeS();

  public Optional<ExprP> body();

  public String q();
}
