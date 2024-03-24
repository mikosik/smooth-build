package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Named function.
 */
public abstract sealed class SNamedFunc extends SNamedEvaluable implements SFunc
    permits SAnnotatedFunc, SNamedExprFunc, SConstructor {
  private final NList<SItem> params;

  public SNamedFunc(SFuncSchema schemaS, String name, NList<SItem> params, Location location) {
    super(schemaS, name, location);
    this.params = params;
  }

  @Override
  public SFuncSchema schema() {
    return (SFuncSchema) super.schema();
  }

  @Override
  public NList<SItem> params() {
    return params;
  }
}
