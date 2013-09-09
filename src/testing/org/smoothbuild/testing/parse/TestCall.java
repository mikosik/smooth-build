package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;

public class TestCall extends CallContext {
  public static TestCall call(ParserRuleContext parent, String name) {
    TestCall call = new TestCall(parent);
    call.addChild(Helpers.functionName(call, name));
    return call;
  }

  public TestCall(ParserRuleContext parent) {
    super(parent, 0);
  }
}
