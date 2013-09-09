package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;

public class TestPipe extends PipeContext {

  public TestPipe(ParserRuleContext parent) {
    super(parent, 0);
  }

  public TestCall addFunctionCall(String name) {
    TestCall function = TestCall.call(this, name);
    addChild(function);
    return function;
  }
}
