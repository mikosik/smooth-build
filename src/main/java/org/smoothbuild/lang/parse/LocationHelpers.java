package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.base.define.Location.location;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.lang.base.define.FilePath;
import org.smoothbuild.lang.base.define.Location;

public class LocationHelpers {
  public static Location locationOf(FilePath filePath, ParserRuleContext parserRuleContext) {
    return locationOf(filePath, parserRuleContext.getStart());
  }

  public static Location locationOf(FilePath filePath, TerminalNode node) {
    return locationOf(filePath, node.getSymbol());
  }

  public static Location locationOf(FilePath filePath, Token token) {
    return location(filePath, token.getLine());
  }
}
