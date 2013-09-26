package org.smoothbuild.parse;

import static org.smoothbuild.message.CodeLocation.codeLocation;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.message.CodeLocation;

public class LocationHelpers {
  public static CodeLocation locationOf(ArgContext arg) {
    ParamNameContext paramName = arg.paramName();
    if (paramName == null) {
      return locationOf(arg.expression());
    } else {
      return locationOf(paramName);
    }
  }

  public static CodeLocation locationOf(ParserRuleContext parserRuleContext) {
    Token startToken = parserRuleContext.getStart();
    Token endToken = parserRuleContext.getStop();

    return locationOf(startToken, endToken);
  }

  public static CodeLocation locationOf(Token token) {
    int line = token.getLine();
    int start = token.getCharPositionInLine();
    int end = start + token.getText().length();

    return codeLocation(line, start, end);
  }

  private static CodeLocation locationOf(Token startToken, Token endToken) {
    int line = startToken.getLine();
    int start = startToken.getCharPositionInLine();
    int end = endToken.getCharPositionInLine() + endToken.getText().length();

    return codeLocation(line, start, end);
  }

  public static CodeLocation locationIn(Token token, int offset) {
    int line = token.getLine();
    int column = token.getCharPositionInLine() + offset;
    return codeLocation(line, column, column);
  }
}
