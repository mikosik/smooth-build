package org.smoothbuild.parse;

import static org.smoothbuild.lang.message.CodeLocation.codeLocation;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.lang.message.CodeLocation;

public class LocationHelpers {
  public static CodeLocation locationOf(ArgContext arg) {
    NameContext name = arg.name();
    if (name == null) {
      return locationOf(arg.expr());
    } else {
      return locationOf(name);
    }
  }

  public static CodeLocation locationOf(ParserRuleContext parserRuleContext) {
    return locationOf(parserRuleContext.getStart());
  }

  public static CodeLocation locationOf(Token token) {
    return codeLocation(token.getLine());
  }
}
