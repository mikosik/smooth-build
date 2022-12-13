package org.smoothbuild.compile.lang.define;

public sealed interface ExprFuncS
    extends FuncS
    permits AnonFuncS, NamedExprFuncS {
  public ExprS body();
}
