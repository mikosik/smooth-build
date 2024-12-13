package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Named function.
 */
public abstract sealed class SNamedFunc extends SNamedEvaluable implements SFunc
    permits SAnnotatedFunc, SNamedExprFunc, SConstructor {
  private final NList<SItem> params;

  public SNamedFunc(SFuncSchema sSchema, Id id, NList<SItem> params, Location location) {
    super(sSchema, id, location);
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
