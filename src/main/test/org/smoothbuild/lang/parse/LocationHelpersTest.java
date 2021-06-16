package org.smoothbuild.lang.parse;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.base.define.TestingFileLocation.fileLocation;
import static org.smoothbuild.lang.parse.LocationHelpers.locationOf;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.define.FileLocation;
import org.smoothbuild.lang.base.define.Location;

public class LocationHelpersTest {
  @Test
  public void location_of_parser_rule_context() {
    ParserRuleContext parserRuleContext = mock(ParserRuleContext.class);
    Token token = token(13);
    when(parserRuleContext.getStart()).thenReturn(token);
    Location location = locationOf(mLocation(), parserRuleContext);
    assertThat(location.line())
        .isEqualTo(13);
  }

  @Test
  public void location_of_token() {
    Token token = token(13);
    Location location = locationOf(mLocation(), token);
    assertThat(location.line())
        .isEqualTo(13);
  }

  private static Token token(int l) {
    Token token = mock(Token.class);
    when(token.getLine()).thenReturn(l);
    return token;
  }

  private static FileLocation mLocation() {
    return fileLocation("script.smooth");
  }
}
