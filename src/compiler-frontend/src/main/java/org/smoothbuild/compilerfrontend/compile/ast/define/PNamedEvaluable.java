package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class PNamedEvaluable implements PReferenceable, PEvaluable
    permits PNamedFunc, PNamedValue {
  private final String nameText;
  private final PTypeParams typeParams;
  private final Maybe<PExpr> body;
  private final Maybe<PAnnotation> annotation;
  private final Location location;
  private PScope scope;
  private Fqn fqn;

  protected PNamedEvaluable(
      String nameText,
      PTypeParams typeParams,
      Maybe<PExpr> body,
      Maybe<PAnnotation> annotation,
      Location location) {
    this.nameText = nameText;
    this.typeParams = typeParams;
    this.body = body;
    this.annotation = annotation;
    this.location = location;
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
  public String nameText() {
    return nameText;
  }

  @Override
  public PTypeParams typeParams() {
    return typeParams;
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
  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public String q() {
    return Strings.q(nameText);
  }

  @Override
  public Location location() {
    return location;
  }
}
