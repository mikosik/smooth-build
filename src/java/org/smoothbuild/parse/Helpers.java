package org.smoothbuild.parse;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.problem.SourceLocation;

public class Helpers {
  public static SourceLocation locationOf(ParserRuleContext parserRuleContext) {
    Token startToken = parserRuleContext.getStart();
    Token endToken = parserRuleContext.getStop();

    int line = startToken.getLine();
    int start = startToken.getStartIndex();
    int end = endToken.getStopIndex();

    return new SourceLocation(line, start, end);
  }
}
