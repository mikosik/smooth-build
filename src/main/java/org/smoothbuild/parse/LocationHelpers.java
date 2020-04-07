package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.Location.location;

import java.nio.file.Path;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.ModulePath;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.lang.base.Location;

public class LocationHelpers {
  public static Location locationOf(Path file, ArgContext arg) {
    NameContext name = arg.name();
    if (name == null) {
      return locationOf(file, arg.pipe());
    } else {
      return locationOf(file, name);
    }
  }

  public static Location locationOf(Path path, ParserRuleContext parserRuleContext) {
    return locationOf(path, parserRuleContext.getStart());
  }

  public static Location locationOf(ModulePath path, ParserRuleContext parserRuleContext) {
    return locationOf(path, parserRuleContext.getStart());
  }

  public static Location locationOf(Path path, Token token) {
    return location(new ModulePath(path, path.toString()), token.getLine());
  }

  public static Location locationOf(ModulePath path, Token token) {
    return location(path, token.getLine());
  }
}
