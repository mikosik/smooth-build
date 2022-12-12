package org.smoothbuild.compile.lang.define;

public sealed interface ExprFuncS
    extends FuncS
    permits AnonFuncS, DefFuncS {
  public ExprS body();
}
