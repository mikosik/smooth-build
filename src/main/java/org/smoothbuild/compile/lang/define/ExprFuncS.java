package org.smoothbuild.compile.lang.define;

public sealed interface ExprFuncS
    extends FuncS
    permits AnonymousFuncS, NamedExprFuncS {
  public ExprS body();
}
