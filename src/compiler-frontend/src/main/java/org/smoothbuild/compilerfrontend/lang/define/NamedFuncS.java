package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.FuncSchemaS;

/**
 * Named function.
 */
public abstract sealed class NamedFuncS extends NamedEvaluableS implements FuncS
    permits AnnotatedFuncS, NamedExprFuncS, ConstructorS {
  private final NList<ItemS> params;

  public NamedFuncS(FuncSchemaS schemaS, String name, NList<ItemS> params, Location location) {
    super(schemaS, name, location);
    this.params = params;
  }

  @Override
  public FuncSchemaS schema() {
    return (FuncSchemaS) super.schema();
  }

  @Override
  public NList<ItemS> params() {
    return params;
  }
}