package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;

public class FakePipeContext extends PipeContext {

  public FakePipeContext(ParserRuleContext parent) {
    super(parent, 0);
  }

  public FakeCallContext addCallCtx(String name) {
    FakeCallContext function = FakeCallContext.callCtx(this, name);
    addChild(function);
    return function;
  }
}
