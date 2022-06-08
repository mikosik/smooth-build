package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.type.PolyFuncTS;

public class PolyFuncS {
  private final PolyFuncTS schema;
  private final FuncS func;

  public PolyFuncS(PolyFuncTS schema, FuncS func) {
    this.schema = requireNonNull(schema);
    this.func = requireNonNull(func);
  }

  public static PolyFuncS polyFuncS(FuncS funcS) {
    var type = funcS.type();
    return new PolyFuncS(new PolyFuncTS(type.vars(), type), funcS);
  }

  public PolyFuncTS schema() {
    return schema;
  }

  public FuncS func() {
    return func;
  }
}
