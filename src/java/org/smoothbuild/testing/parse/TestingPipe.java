package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;

public class TestingPipe extends PipeContext {

  public TestingPipe(ParserRuleContext parent) {
    super(parent, 0);
  }

  public TestingCall addFunctionCall(String name) {
    TestingCall function = TestingCall.call(this, name);
    addChild(function);
    return function;
  }
}
