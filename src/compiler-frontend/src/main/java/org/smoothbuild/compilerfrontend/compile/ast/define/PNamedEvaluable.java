package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class PNamedEvaluable implements PEvaluable, PMonoReferenceable
    permits PNamedFunc, PNamedValue {
  private final String nameText;
  private final Maybe<PExpr> body;
  private final Maybe<PAnnotation> annotation;
  private final Location location;
  private @Nullable PScope scope;
  private @Nullable Fqn fqn;

  protected PNamedEvaluable(
      String nameText, Maybe<PExpr> body, Maybe<PAnnotation> annotation, Location location) {
    this.nameText = nameText;
    this.body = body;
    this.annotation = annotation;
    this.location = location;
  }

  @Override
  public PScope scope() {
    return checkInitializedToNotNull(scope, "scope");
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
  public Maybe<PExpr> body() {
    return body;
  }

  public Maybe<PAnnotation> annotation() {
    return annotation;
  }

  @Override
  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return checkInitializedToNotNull(fqn, "fqn");
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
