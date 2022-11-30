package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.ps.ast.AnnP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.type.TypeP;

/**
 * Evaluable that has fully qualified name.
 */
public sealed abstract class NamedEvaluableP extends NalImpl implements RefableP, EvaluableP
    permits NamedFuncP, NamedValueP {
  private final Optional<ExprP> body;
  private final Optional<AnnP> ann;

  public NamedEvaluableP(String name, Optional<ExprP> body, Optional<AnnP> ann, Loc loc) {
    super(name, loc);
    this.body = body;
    this.ann = ann;
  }

  @Override
  public Optional<ExprP> body() {
    return body;
  }

  public Optional<AnnP> ann() {
    return ann;
  }

  public abstract Optional<TypeP> evalT();

  public abstract SchemaS schemaS();

  @Override
  public String q() {
    return super.q();
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof NamedEvaluableP that
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
