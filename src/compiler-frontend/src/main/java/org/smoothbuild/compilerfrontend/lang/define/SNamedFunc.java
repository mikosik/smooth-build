package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NamedFunc;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

/**
 * Named function.
 */
public abstract sealed class SNamedFunc implements NamedFunc, SFunc, SNamedEvaluable
    permits SAnnotatedFunc, SNamedExprFunc, SConstructor {
  private final NList<SItem> params;
  private final SSchema schema;
  private final Fqn fqn;
  private final Location location;

  public SNamedFunc(SFuncSchema schema, Fqn fqn, NList<SItem> params, Location location) {
    this.schema = schema;
    this.fqn = fqn;
    this.params = params;
    this.location = location;
  }

  @Override
  public SFuncSchema schema() {
    return (SFuncSchema) schema;
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

  protected String funcHeaderToSourceCode(SVarSet localVars) {
    return schema().type().result().specifier(localVars) + " " + name()
        + schema().quantifiedVars().toSourceCode()
        + params().list().map(sItem -> sItem.toSourceCode(localVars)).toString("(", ", ", ")");
  }
}
