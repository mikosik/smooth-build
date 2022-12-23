package org.smoothbuild.compile.ps.ast.expr;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.ps.ast.type.TypeP;

/**
 * Evaluable that has fully qualified name.
 */
public sealed abstract class NamedEvaluableP
    extends NalImpl
    implements RefableP, EvaluableP, WithScopeP
    permits NamedFuncP, NamedValueP {
  private final Optional<ExprP> body;
  private final Optional<AnnotationP> annotation;
  private ScopeP scope;

  protected NamedEvaluableP(
      String name,
      Optional<ExprP> body,
      Optional<AnnotationP> annotation,
      Location location) {
    super(name, location);
    this.body = body;
    this.annotation = annotation;
  }

  @Override
  public ScopeP scope() {
    return scope;
  }

  @Override
  public void setScope(ScopeP scope) {
    this.scope = scope;
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
}
