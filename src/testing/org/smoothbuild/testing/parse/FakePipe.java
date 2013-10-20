package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;

public class FakePipe extends PipeContext {

  public FakePipe(ParserRuleContext parent) {
    super(parent, 0);
  }

  public FakeCall addFunctionCall(String name) {
    FakeCall function = FakeCall.call(this, name);
    addChild(function);
    return function;
  }
}
