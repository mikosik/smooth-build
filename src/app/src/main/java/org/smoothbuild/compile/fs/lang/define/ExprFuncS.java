package org.smoothbuild.compile.fs.lang.define;

public sealed interface ExprFuncS
    extends FuncS
    permits LambdaS, NamedExprFuncS {
  public ExprS body();
}
