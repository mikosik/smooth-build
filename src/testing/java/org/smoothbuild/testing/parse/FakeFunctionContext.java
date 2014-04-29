package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;

public class FakeFunctionContext extends FunctionContext {
  public static FakeFunctionContext functionCtx(String name) {
    return functionCtx(null, name);
  }

  public static FakeFunctionContext functionCtx(ParserRuleContext parent, String name) {
    FakeFunctionContext function = new FakeFunctionContext(parent);
    function.addChild(Helpers.functionNameCtx(function, name));
    return function;
  }

  private FakeFunctionContext(ParserRuleContext parent) {
    super(parent, 0);
  }

  public FakePipeContext addPipeExpressionCtx() {
    FakePipeContext pipe = new FakePipeContext(this);
    addChild(pipe);
    return pipe;
  }
}
