package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NalImpl;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class PNamedEvaluable extends NalImpl implements PReferenceable, PEvaluable
    permits PNamedFunc, PNamedValue {
  private final String shortName;
  private final Maybe<PExpr> body;
  private final Maybe<PAnnotation> annotation;
  private PScope scope;

  protected PNamedEvaluable(
      String fullName,
      String shortName,
      Maybe<PExpr> body,
      Maybe<PAnnotation> annotation,
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
