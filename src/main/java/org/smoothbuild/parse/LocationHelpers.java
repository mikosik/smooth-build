package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.Location.location;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ModulePath;

public class LocationHelpers {
  public static Location locationOf(ModulePath path, ParserRuleContext parserRuleContext) {
    return locationOf(path, parserRuleContext.getStart());
  }

  public static Location locationOf(ModulePath path, Token token) {
    return location(path, token.getLine());
  }
}
