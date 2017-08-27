package org.smoothbuild.parse;

import static org.smoothbuild.lang.message.Location.location;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.lang.message.Location;

public class LocationHelpers {
  public static Location locationOf(ArgContext arg) {
    NameContext name = arg.name();
    if (name == null) {
      return locationOf(arg.expr());
    } else {
      return locationOf(name);
    }
  }

  public static Location locationOf(ParserRuleContext parserRuleContext) {
    return locationOf(parserRuleContext.getStart());
  }

  public static Location locationOf(Token token) {
    return location(token.getLine());
  }
}
