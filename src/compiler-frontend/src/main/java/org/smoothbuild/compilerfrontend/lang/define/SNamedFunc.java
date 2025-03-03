package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NamedFunc;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Named function.
 */
public abstract sealed class SNamedFunc implements NamedFunc, SFunc, SNamedEvaluable
    permits SAnnotatedFunc, SNamedExprFunc, SConstructor {
  private final NList<SItem> params;
  private final SFuncType type;
  private final Fqn fqn;
  private final Location location;

  public SNamedFunc(SType resultType, Fqn fqn, NList<SItem> params, Location location) {
    this.type = new SFuncType(params.list().map(SItem::type), resultType);
    this.fqn = fqn;
    this.params = params;
    this.location = location;
  }

  @Override
  public SFuncType type() {
    return type;
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

  protected String funcHeaderToSourceCode(Maybe<List<STypeVar>> typeParams) {
    return type().result().specifier() + " " + name()
        + typeParams.map(STypeVar::typeParamsToSourceCode).getOr("")
        + params().list().map(SItem::toSourceCode).toString("(", ", ", ")");
  }
}
