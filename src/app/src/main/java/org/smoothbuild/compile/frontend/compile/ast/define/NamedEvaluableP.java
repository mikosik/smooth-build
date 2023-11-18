package org.smoothbuild.compile.frontend.compile.ast.define;

import java.util.Optional;

import org.smoothbuild.compile.frontend.lang.base.NalImpl;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

/**
 * Evaluable that has fully qualified name.
 */
public sealed abstract class NamedEvaluableP
    extends NalImpl
    implements ReferenceableP, EvaluableP
    permits NamedFuncP, NamedValueP {
  private final String shortName;
  private final Optional<ExprP> body;
  private final Optional<AnnotationP> annotation;
  private ScopeP scope;

  protected NamedEvaluableP(
      String fullName,
      String shortName,
      Optional<ExprP> body,
      Optional<AnnotationP> annotation,
      Location location) {
    super(fullName, location);
    this.shortName = shortName;
    this.body = body;
    this.annotation = annotation;
  }

  @Override
  public String shortName() {
    return shortName;
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

  public abstract TypeP evaluationT();

  @Override
  public String q() {
    return super.q();
  }
}
