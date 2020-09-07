package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.base.Location.location;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ModuleInfo;

public class LocationHelpers {
  public static Location locationOf(ModuleInfo moduleInfo, ParserRuleContext parserRuleContext) {
    return locationOf(moduleInfo, parserRuleContext.getStart());
  }

  public static Location locationOf(ModuleInfo moduleInfo, Token token) {
    return location(moduleInfo, token.getLine());
  }
}
