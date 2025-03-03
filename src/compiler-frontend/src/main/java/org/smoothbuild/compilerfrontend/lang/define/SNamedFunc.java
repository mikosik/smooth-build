package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.typeParamsToSourceCode;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NamedFunc;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncTypeScheme;
import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;

/**
 * Named function.
 */
public abstract sealed class SNamedFunc implements NamedFunc, SFunc, SNamedEvaluable
    permits SAnnotatedFunc, SNamedExprFunc, SConstructor {
  private final NList<SItem> params;
  private final STypeScheme typeScheme;
  private final Fqn fqn;
  private final Location location;

  public SNamedFunc(SFuncTypeScheme typeScheme, Fqn fqn, NList<SItem> params, Location location) {
    this.typeScheme = typeScheme;
    this.fqn = fqn;
    this.params = params;
    this.location = location;
  }

  @Override
  public SFuncTypeScheme typeScheme() {
    return (SFuncTypeScheme) typeScheme;
  }

  @Override
  public NList<SItem> params() {
    return params;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public Location location() {
    return location;
  }

  protected String funcHeaderToSourceCode() {
    return this.typeScheme().type().result().specifier() + " " + name()
        + typeParamsToSourceCode(this.typeScheme().typeParams())
        + params().list().map(SItem::toSourceCode).toString("(", ", ", ")");
  }
}
