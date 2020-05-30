package org.smoothbuild.parse;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.base.Space.USER;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.nio.file.Path;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ModulePath;

public class LocationHelpersTest {
  @Test
  public void location_of_parser_rule_context() {
    ParserRuleContext parserRuleContext = mock(ParserRuleContext.class);
    Token token = token(13);
    when(parserRuleContext.getStart()).thenReturn(token);
    Location location = locationOf(modulePath(), parserRuleContext);
    assertThat(location.line())
        .isEqualTo(13);
  }

  @Test
  public void location_of_token() {
    Token token = token(13);
    Location location = locationOf(modulePath(), token);
    assertThat(location.line())
        .isEqualTo(13);
  }

  private static Token token(int l) {
    Token token = mock(Token.class);
    when(token.getLine()).thenReturn(l);
    return token;
  }

  private static ModulePath modulePath() {
    return new ModulePath(USER, Path.of("script.smooth"), "{u}/script.smooth");
  }
}
