package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.PipeExpressionContext;

public class TestingPipeExpression extends PipeExpressionContext {

  public TestingPipeExpression(ParserRuleContext parent) {
    super(parent, 0);
  }

  public TestingFunctionCall addFunctionCall(String name) {
    TestingFunctionCall function = TestingFunctionCall.functionCall(this, name);
    addChild(function);
    return function;
  }
}
