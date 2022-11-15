package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.ps.ast.AnnP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;

public sealed abstract class PolyEvaluableP extends NalImpl implements RefableP
    permits FuncP, NamedValueP {
  private final Optional<ExprP> body;
  private final Optional<AnnP> ann;

  public PolyEvaluableP(String name, Optional<ExprP> body, Optional<AnnP> ann, Loc loc) {
    super(name, loc);
    this.body = body;
    this.ann = ann;
  }

  public Optional<ExprP> body() {
    return body;
  }

  public Optional<AnnP> ann() {
    return ann;
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof PolyEvaluableP that
        && this.name().equals(that.name());
  }

  @Override
  public final int hashCode() {
    return name().hashCode();
  }

  @Override
  public String toString() {
    return "[" + name() + ":" + loc() + "]";
  }
}
