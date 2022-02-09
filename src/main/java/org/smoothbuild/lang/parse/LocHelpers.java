package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.base.define.Loc.loc;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.base.define.Loc;

public class LocHelpers {
  public static Loc locOf(FilePath filePath, ParserRuleContext parserRuleContext) {
    return locOf(filePath, parserRuleContext.getStart());
  }

  public static Loc locOf(FilePath filePath, TerminalNode node) {
    return locOf(filePath, node.getSymbol());
  }

  public static Loc locOf(FilePath filePath, Token token) {
    return loc(filePath, token.getLine());
  }
}
