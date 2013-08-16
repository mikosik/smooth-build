package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;

public class TestingFunctionContext extends FunctionContext {
  public static TestingFunctionContext testingFunctionContext(String name) {
    TestingFunctionContext function = new TestingFunctionContext();
    FunctionNameContext functionName = new FunctionNameContext(function, 0);
    functionName.addChild(stringChild(name));
    functionName.start = token();
    functionName.stop = token();
    function.addChild(functionName);
    return function;
  }

  public TestingFunctionContext() {
    super(null, 0);
  }

  private static ParserRuleContext stringChild(final String string) {
    return new ParserRuleContext() {
      @Override
      public String getText() {
        return string;
      }
    };
  }

  private static Token token() {
    CommonToken token = new CommonToken(0);
    token.setStartIndex(13);
    token.setLine(11);
    token.setStopIndex(17);
    return token;
  }
}
