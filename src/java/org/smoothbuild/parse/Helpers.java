package org.smoothbuild.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.problem.SourceLocation;

public class Helpers {
  public static SourceLocation locationOf(ParserRuleContext parserRuleContext) {
    Token startToken = parserRuleContext.getStart();
    Token endToken = parserRuleContext.getStop();

    return locationOf(startToken, endToken);
  }

  public static SourceLocation locationOf(Token token) {
    return locationOf(token, token);
  }

  private static SourceLocation locationOf(Token startToken, Token endToken) {
    int line = startToken.getLine();
    int start = startToken.getStartIndex();
    int end = endToken.getStopIndex();

    return new SourceLocation(line, start, end);
  }
}
