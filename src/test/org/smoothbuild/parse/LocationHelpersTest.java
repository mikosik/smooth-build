package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.message.CodeLocation.codeLocation;
import static org.smoothbuild.parse.LocationHelpers.locationIn;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.Test;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.message.CodeLocation;

public class LocationHelpersTest {
  Token startToken = mock(Token.class);
  Token stopToken = mock(Token.class);

  @Test
  public void locationOfArgContextWithName() {
    // given
    ArgContext argContext = mock(ArgContext.class);
    ParamNameContext paramNameContext = mock(ParamNameContext.class);
    int line = 13;
    int start = 11;
    int end = 17;
    String text = "123";
    when(argContext.paramName()).thenReturn(paramNameContext);
    when(paramNameContext.getStart()).thenReturn(startToken);
    when(paramNameContext.getStop()).thenReturn(stopToken);
    when(startToken.getLine()).thenReturn(line);
    when(startToken.getCharPositionInLine()).thenReturn(start);
    when(stopToken.getCharPositionInLine()).thenReturn(end);
    when(stopToken.getText()).thenReturn(text);

    // when
    CodeLocation location = LocationHelpers.locationOf(argContext);

    // then
    assertThat(location.line()).isEqualTo(line);
    assertThat(location.start()).isEqualTo(start);
    assertThat(location.end()).isEqualTo(end + text.length());
  }

  @Test
  public void locationOfArgContextWithoutParamName() {
    // given
    ArgContext argContext = mock(ArgContext.class);
    ExpressionContext expressionContext = mock(ExpressionContext.class);
    int line = 13;
    int start = 11;
    int end = 17;
    String text = "123";
    when(argContext.expression()).thenReturn(expressionContext);
    when(expressionContext.getStart()).thenReturn(startToken);
    when(expressionContext.getStop()).thenReturn(stopToken);
    when(startToken.getLine()).thenReturn(line);
    when(startToken.getCharPositionInLine()).thenReturn(start);
    when(stopToken.getCharPositionInLine()).thenReturn(end);
    when(stopToken.getText()).thenReturn(text);

    // when
    CodeLocation location = LocationHelpers.locationOf(argContext);

    // then
    assertThat(location.line()).isEqualTo(line);
    assertThat(location.start()).isEqualTo(start);
    assertThat(location.end()).isEqualTo(end + text.length());
  }

  @Test
  public void locationOfParserRuleContext() {
    // given
    ParserRuleContext parserRuleContext = mock(ParserRuleContext.class);
    int start = 11;
    int line = 13;
    int end = 17;
    String text = "123";
    when(parserRuleContext.getStart()).thenReturn(startToken);
    when(parserRuleContext.getStop()).thenReturn(stopToken);
    when(startToken.getLine()).thenReturn(line);
    when(startToken.getCharPositionInLine()).thenReturn(start);
    when(stopToken.getCharPositionInLine()).thenReturn(end);
    when(stopToken.getText()).thenReturn(text);

    // when
    CodeLocation location = LocationHelpers.locationOf(parserRuleContext);

    // then
    assertThat(location.line()).isEqualTo(line);
    assertThat(location.start()).isEqualTo(start);
    assertThat(location.end()).isEqualTo(end + text.length());
  }

  @Test
  public void locationOfToken() throws Exception {
    // given
    int start = 11;
    int line = 13;
    String text = "123";
    when(startToken.getLine()).thenReturn(line);
    when(startToken.getCharPositionInLine()).thenReturn(start);
    when(startToken.getText()).thenReturn(text);

    // when
    CodeLocation location = LocationHelpers.locationOf(startToken);

    // then
    assertThat(location.line()).isEqualTo(line);
    assertThat(location.start()).isEqualTo(start);
    assertThat(location.end()).isEqualTo(start + text.length());
  }

  @Test
  public void locationInToken() throws Exception {
    // given
    when(startToken.getLine()).thenReturn(7);
    when(startToken.getCharPositionInLine()).thenReturn(11);

    // when
    CodeLocation codeLocation = locationIn(startToken, 13);

    // then
    assertThat(codeLocation).isEqualTo(codeLocation(7, 24, 24));
  }
}
