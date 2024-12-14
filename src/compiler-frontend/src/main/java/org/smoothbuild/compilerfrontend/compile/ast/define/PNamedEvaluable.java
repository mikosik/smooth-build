package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Ianal;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class PNamedEvaluable extends Ianal implements PReferenceable, PEvaluable
    permits PNamedFunc, PNamedValue {
  private final Maybe<PExpr> body;
  private final Maybe<PAnnotation> annotation;
  private PScope scope;

  protected PNamedEvaluable(
      String nameText, Maybe<PExpr> body, Maybe<PAnnotation> annotation, Location location) {
    super(nameText, location);
    this.body = body;
    this.annotation = annotation;
  }

  @Override
  public PScope scope() {
    return scope;
  }

  @Override
  public void setScope(PScope scope) {
    this.scope = scope;
  }

  @Override
  public Maybe<PExpr> body() {
    return body;
  }

  public Maybe<PAnnotation> annotation() {
    return annotation;
  }

  public abstract PType evaluationType();

  @Override
  public String q() {
    return super.q();
  }
}
