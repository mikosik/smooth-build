package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.base.define.Location.location;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.lang.base.define.FileLocation;
import org.smoothbuild.lang.base.define.Location;

public class LocationHelpers {
  public static Location locationOf(
      FileLocation fileLocation, ParserRuleContext parserRuleContext) {
    return locationOf(fileLocation, parserRuleContext.getStart());
  }

  public static Location locationOf(FileLocation fileLocation, TerminalNode node) {
    return locationOf(fileLocation, node.getSymbol());
  }

  public static Location locationOf(FileLocation fileLocation, Token token) {
    return location(fileLocation, token.getLine());
  }
}
