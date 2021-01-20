package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.base.define.Location.location;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.ModuleLocation;

public class LocationHelpers {
  public static Location locationOf(
      ModuleLocation moduleLocation, ParserRuleContext parserRuleContext) {
    return locationOf(moduleLocation, parserRuleContext.getStart());
  }

  public static Location locationOf(ModuleLocation moduleLocation, TerminalNode node) {
    return locationOf(moduleLocation, node.getSymbol());
  }

  public static Location locationOf(ModuleLocation moduleLocation, Token token) {
    return location(moduleLocation, token.getLine());
  }
}
