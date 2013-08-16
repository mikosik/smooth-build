package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.FunctionCallContext;

public class TestingFunctionCall extends FunctionCallContext {
  public static TestingFunctionCall functionCall(ParserRuleContext parent,
      String name) {
    TestingFunctionCall call = new TestingFunctionCall(parent);
    call.addChild(Helpers.functionName(call, name));
    return call;
  }

  public TestingFunctionCall(ParserRuleContext parent) {
    super(parent, 0);
  }
}
