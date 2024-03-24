package org.smoothbuild.compilerfrontend.lang.define;

public sealed interface SExprFunc extends SFunc permits SLambda, SNamedExprFunc {
  public SExpr body();
}
