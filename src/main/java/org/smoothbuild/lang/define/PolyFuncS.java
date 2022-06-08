package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.type.PolyFuncTS;

public class PolyFuncS {
  private final PolyFuncTS type;
  private final FuncS func;

  public PolyFuncS(PolyFuncTS type, FuncS func) {
    this.type = requireNonNull(type);
    this.func = requireNonNull(func);
  }

  public static PolyFuncS polyFuncS(FuncS funcS) {
    var type = funcS.type();
    return new PolyFuncS(new PolyFuncTS(type.vars(), type), funcS);
  }

  public PolyFuncTS type() {
    return type;
  }

  public FuncS func() {
    return func;
  }
}
