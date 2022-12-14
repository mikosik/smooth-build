package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Sanal;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.util.collect.NList;

/**
 * Named function.
 */
public sealed abstract class NamedFuncS
    extends Sanal
    implements FuncS, NamedEvaluableS
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
