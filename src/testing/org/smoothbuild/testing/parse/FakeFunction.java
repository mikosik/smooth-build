package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;

public class FakeFunction extends FunctionContext {
  public static FakeFunction function(String name) {
    return function(null, name);
  }

  public static FakeFunction function(ParserRuleContext parent, String name) {
    FakeFunction function = new FakeFunction(parent);
    function.addChild(Helpers.functionName(function, name));
    return function;
  }

  private FakeFunction(ParserRuleContext parent) {
    super(parent, 0);
  }

  public FakePipe addPipeExpression() {
    FakePipe pipe = new FakePipe(this);
    addChild(pipe);
    return pipe;
  }
}
