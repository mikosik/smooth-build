package org.smoothbuild.parse;

import static org.smoothbuild.message.base.CodeLocation.codeLocation;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.message.base.CodeLocation;

public class LocationHelpers {
  public static CodeLocation locationOf(ArgContext arg) {
    ParamNameContext paramName = arg.paramName();
    if (paramName == null) {
      return locationOf(arg.expression());
    } else {
      return locationOf(paramName);
    }
  }

  public static CodeLocation locationOf(ParserRuleContext parserRuleContext) {
    return locationOf(parserRuleContext.getStart());
  }

  public static CodeLocation locationOf(Token token) {
    return codeLocation(token.getLine());
  }
}
