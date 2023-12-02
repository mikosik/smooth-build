package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compile.frontend.lang.base.NalImpl;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class NamedEvaluableP extends NalImpl implements ReferenceableP, EvaluableP
    permits NamedFuncP, NamedValueP {
  private final String shortName;
  private final Maybe<ExprP> body;
  private final Maybe<AnnotationP> annotation;
  private ScopeP scope;

  protected NamedEvaluableP(
      String fullName,
      String shortName,
      Maybe<ExprP> body,
      Maybe<AnnotationP> annotation,
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
  public Maybe<ExprP> body() {
    return body;
  }

  public Maybe<AnnotationP> annotation() {
    return annotation;
  }

  public abstract TypeP evaluationT();

  @Override
  public String q() {
    return super.q();
  }
}
