package org.smoothbuild.compile.fs.lang.define;

public sealed interface ExprFuncS
    extends FuncS
    permits AnonymousFuncS, NamedExprFuncS {
  public ExprS body();
}
