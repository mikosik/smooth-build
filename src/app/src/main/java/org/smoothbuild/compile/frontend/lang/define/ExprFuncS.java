package org.smoothbuild.compile.frontend.lang.define;

public sealed interface ExprFuncS
    extends FuncS
    permits LambdaS, NamedExprFuncS {
  public ExprS body();
}
