package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;

public class FakeCall extends CallContext {
  public static FakeCall call(ParserRuleContext parent, String name) {
    FakeCall call = new FakeCall(parent);
    call.addChild(Helpers.functionName(call, name));
    return call;
  }

  public FakeCall(ParserRuleContext parent) {
    super(parent, 0);
  }
}
