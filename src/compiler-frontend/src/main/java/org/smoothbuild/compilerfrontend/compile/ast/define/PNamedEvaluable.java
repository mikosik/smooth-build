package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.HasNameText;
import org.smoothbuild.compilerfrontend.lang.name.Id;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class PNamedEvaluable extends HasLocationImpl
    implements PReferenceable, PEvaluable, HasIdAndLocation, HasNameText
    permits PNamedFunc, PNamedValue {
  private final String nameText;
  private final Maybe<PExpr> body;
  private final Maybe<PAnnotation> annotation;
  private PScope scope;
  private Id id;

  protected PNamedEvaluable(
      String nameText, Maybe<PExpr> body, Maybe<PAnnotation> annotation, Location location) {
    super(location);
    this.nameText = nameText;
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

  public abstract PType evaluationType();

  public void setId(Id id) {
    this.id = id;
  }

  @Override
  public Id id() {
    return id;
  }

  @Override
  public String q() {
    return Strings.q(nameText);
  }
}
