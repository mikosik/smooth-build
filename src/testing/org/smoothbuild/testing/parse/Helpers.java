package org.smoothbuild.testing.parse;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;

public class Helpers {

  public static Token token() {
    CommonToken token = new CommonToken(0);
    token.setLine(11);
    token.setCharPositionInLine(13);
    token.setText("token-text");
    return token;
  }

  public static ParserRuleContext stringChild(final String string) {
    return new ParserRuleContext() {
      @Override
      public String getText() {
        return string;
      }
    };
  }

  public static FunctionNameContext functionName(ParserRuleContext parent, String name) {
    FunctionNameContext functionName = new FunctionNameContext(parent, 0);
    functionName.addChild(stringChild(name));
    functionName.start = token();
    functionName.stop = token();
    return functionName;
  }
}
