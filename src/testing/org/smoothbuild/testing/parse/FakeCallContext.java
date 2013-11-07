package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;

public class FakeCallContext extends CallContext {
  public static FakeCallContext callCtx(ParserRuleContext parent, String name) {
    FakeCallContext callContext = new FakeCallContext(parent);
    callContext.addChild(Helpers.functionNameCtx(callContext, name));
    return callContext;
  }

  public FakeCallContext(ParserRuleContext parent) {
    super(parent, 0);
  }
}
