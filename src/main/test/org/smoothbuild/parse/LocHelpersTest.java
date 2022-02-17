package org.smoothbuild.parse;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.parse.LocHelpers.locOf;
import static org.smoothbuild.testing.TestingContext.filePath;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.define.Loc;

public class LocHelpersTest {
  @Test
  public void loc_of_parser_rule_context() {
    ParserRuleContext parserRuleContext = mock(ParserRuleContext.class);
    Token token = token(13);
    when(parserRuleContext.getStart()).thenReturn(token);
    Loc loc = locOf(mLoc(), parserRuleContext);
    assertThat(loc.line())
        .isEqualTo(13);
  }

  @Test
  public void loc_of_token() {
    Token token = token(13);
    Loc loc = locOf(mLoc(), token);
    assertThat(loc.line())
        .isEqualTo(13);
  }

  private static Token token(int l) {
    Token token = mock(Token.class);
    when(token.getLine()).thenReturn(l);
    return token;
  }

  private static FilePath mLoc() {
    return filePath("script.smooth");
  }
}
