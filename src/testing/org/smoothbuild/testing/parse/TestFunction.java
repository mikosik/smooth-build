package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;

public class TestFunction extends FunctionContext {
  public static TestFunction function(String name) {
    return function(null, name);
  }

  public static TestFunction function(ParserRuleContext parent, String name) {
    TestFunction function = new TestFunction(parent);
    function.addChild(Helpers.functionName(function, name));
    return function;
  }

  private TestFunction(ParserRuleContext parent) {
    super(parent, 0);
  }

  public TestPipe addPipeExpression() {
    TestPipe pipe = new TestPipe(this);
    addChild(pipe);
    return pipe;
  }
}
