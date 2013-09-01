package org.smoothbuild.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.problem.CodeLocation;

public class Helpers {
  public static CodeLocation locationOf(ParserRuleContext parserRuleContext) {
    Token startToken = parserRuleContext.getStart();
    Token endToken = parserRuleContext.getStop();

    return locationOf(startToken, endToken);
  }

  public static CodeLocation locationOf(Token token) {
    return locationOf(token, token);
  }

  private static CodeLocation locationOf(Token startToken, Token endToken) {
    int line = startToken.getLine();
    int start = startToken.getStartIndex();
    int end = endToken.getStopIndex();

    return new CodeLocation(line, start, end);
  }

  public static CodeLocation locationIn(Token token, int offset) {
    int line = token.getLine();
    int column = token.getStartIndex() + offset;
    return new CodeLocation(line, column, column);
  }
}
