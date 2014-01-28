package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.Test;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.message.base.CodeLocation;

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
    given(willReturn(paramNameContext), argContext).paramName();
    given(willReturn(startToken), paramNameContext).getStart();
    given(willReturn(stopToken), paramNameContext).getStop();
    given(willReturn(line), startToken).getLine();
    given(willReturn(start), startToken).getCharPositionInLine();
    given(willReturn(end), stopToken).getCharPositionInLine();
    given(willReturn(text), stopToken).getText();

    // when
    CodeLocation location = LocationHelpers.locationOf(argContext);

    // then
    assertThat(location.line()).isEqualTo(line);
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
    given(willReturn(expressionContext), argContext).expression();
    given(willReturn(startToken), expressionContext).getStart();
    given(willReturn(stopToken), expressionContext).getStop();
    given(willReturn(line), startToken).getLine();
    given(willReturn(start), startToken).getCharPositionInLine();
    given(willReturn(end), stopToken).getCharPositionInLine();
    given(willReturn(text), stopToken).getText();

    // when
    CodeLocation location = LocationHelpers.locationOf(argContext);

    // then
    assertThat(location.line()).isEqualTo(line);
  }

  @Test
  public void locationOfParserRuleContext() {
    // given
    ParserRuleContext parserRuleContext = mock(ParserRuleContext.class);
    int start = 11;
    int line = 13;
    int end = 17;
    String text = "123";
    given(willReturn(startToken), parserRuleContext).getStart();
    given(willReturn(stopToken), parserRuleContext).getStop();
    given(willReturn(line), startToken).getLine();
    given(willReturn(start), startToken).getCharPositionInLine();
    given(willReturn(end), stopToken).getCharPositionInLine();
    given(willReturn(text), stopToken).getText();

    // when
    CodeLocation location = LocationHelpers.locationOf(parserRuleContext);

    // then
    assertThat(location.line()).isEqualTo(line);
  }

  @Test
  public void locationOfToken() throws Exception {
    // given
    int start = 11;
    int line = 13;
    String text = "123";
    given(willReturn(line), startToken).getLine();
    given(willReturn(start), startToken).getCharPositionInLine();
    given(willReturn(text), startToken).getText();

    // when
    CodeLocation location = LocationHelpers.locationOf(startToken);

    // then
    assertThat(location.line()).isEqualTo(line);
  }
}
