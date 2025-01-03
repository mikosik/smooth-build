package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NamedFunc;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Named function.
 */
public abstract sealed class SNamedFunc implements NamedFunc, SFunc, SNamedEvaluable
    permits SAnnotatedFunc, SNamedExprFunc, SConstructor {
  private final NList<SItem> params;
  private final SSchema schema;
  private final Id id;
  private final Location location;

  public SNamedFunc(SFuncSchema schema, Id id, NList<SItem> params, Location location) {
    this.schema = schema;
    this.id = id;
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
  public Id id() {
    return id;
  }

  @Override
  public Location location() {
    return location;
  }

  protected String funcHeaderToSourceCode() {
    return schema().type().result().toSourceCode() + " " + id().parts().getLast()
        + schema().quantifiedVars().toSourceCode()
        + params().list().map(SItem::toSourceCode).toString("(", ", ", ")");
  }
}
