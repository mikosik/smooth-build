package org.smoothbuild.parse;

import static org.smoothbuild.lang.message.Location.location;

import java.nio.file.Path;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.lang.message.Location;

public class LocationHelpers {
  public static Location locationOf(Path file, ArgContext arg) {
    NameContext name = arg.name();
    if (name == null) {
      return locationOf(file, arg.expr());
    } else {
      return locationOf(file, name);
    }
  }

  public static Location locationOf(Path file, ParserRuleContext parserRuleContext) {
    return locationOf(file, parserRuleContext.getStart());
  }

  public static Location locationOf(Path file, Token token) {
    return location(file, token.getLine());
  }
}
