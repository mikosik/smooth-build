package org.smoothbuild.parse;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.nio.file.Paths;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.lang.base.Location;

public class LocationHelpersTest {
  @Test
  public void location_of_arg_context_with_param_name() {
    Token token = token(13);
    NameContext nameContext = mock(NameContext.class);
    when(nameContext.getStart()).thenReturn(token);
    ArgContext argContext = mock(ArgContext.class);
    when(argContext.name()).thenReturn(nameContext);
    Location location = locationOf(Paths.get("script.smooth"), argContext);
    assertThat(location.line())
        .isEqualTo(13);
  }

  @Test
  public void location_of_arg_context_without_param_name() {
    PipeContext pipeContext = mock(PipeContext.class);
    Token token = token(13);
    when(pipeContext.getStart()).thenReturn(token);
    ArgContext argContext = mock(ArgContext.class);
    when(argContext.pipe()).thenReturn(pipeContext);
    Location location = locationOf(Paths.get("script.smooth"), argContext);
    assertThat(location.line())
        .isEqualTo(13);
  }

  @Test
  public void location_of_parser_rule_context() {
    ParserRuleContext parserRuleContext = mock(ParserRuleContext.class);
    Token token = token(13);
    when(parserRuleContext.getStart()).thenReturn(token);
    Location location = locationOf(Paths.get("script.smooth"), parserRuleContext);
    assertThat(location.line())
        .isEqualTo(13);
  }

  @Test
  public void location_of_token() {
    Token token = token(13);
    Location location = locationOf(Paths.get("script.smooth"), token);
    assertThat(location.line())
        .isEqualTo(13);
  }

  private static Token token(int l) {
    Token token = mock(Token.class);
    when(token.getLine()).thenReturn(l);
    return token;
  }
}
