package org.smoothbuild.compile.ps.ast.expr;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.ps.ast.type.TypeP;

/**
 * Evaluable that has fully qualified name.
 */
public sealed abstract class NamedEvaluableP extends NalImpl implements RefableP, EvaluableP
    permits NamedFuncP, NamedValueP {
  private final Optional<ExprP> body;
  private final Optional<AnnotationP> annotation;

  protected NamedEvaluableP(String name, Optional<ExprP> body, Optional<AnnotationP> annotation, Loc loc) {
    super(name, loc);
    this.body = body;
    this.annotation = annotation;
  }

  @Override
  public Optional<ExprP> body() {
    return body;
  }

  public Optional<AnnotationP> annotation() {
    return annotation;
  }

  public abstract Optional<TypeP> evalT();

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
