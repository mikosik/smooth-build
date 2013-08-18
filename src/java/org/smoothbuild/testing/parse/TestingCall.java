package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;

public class TestingCall extends CallContext {
  public static TestingCall call(ParserRuleContext parent, String name) {
    TestingCall call = new TestingCall(parent);
    call.addChild(Helpers.functionName(call, name));
    return call;
  }

  public TestingCall(ParserRuleContext parent) {
    super(parent, 0);
  }
}
