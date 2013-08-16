package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;

public class TestingFunction extends FunctionContext {
  public static TestingFunction function(String name) {
    return function(null, name);
  }

  public static TestingFunction function(ParserRuleContext parent, String name) {
    TestingFunction function = new TestingFunction(parent);
    function.addChild(Helpers.functionName(function, name));
    return function;
  }

  private TestingFunction(ParserRuleContext parent) {
    super(parent, 0);
  }

  public TestingPipeExpression addPipeExpression() {
    TestingPipeExpression pipe = new TestingPipeExpression(this);
    addChild(pipe);
    return pipe;
  }
}
