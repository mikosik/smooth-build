package org.smoothbuild.parse;

import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.message.base.CodeLocation;

public class LocationHelpersTest {
  private int line;
  private Token startToken;
  private CodeLocation location;
  private ParserRuleContext parserRuleContext;
  private ExpressionContext expressionContext;
  private ArgContext argContext;
  private ParamNameContext paramNameContext;

  @Before
  public void before() {
    givenTest(this);
    given(line = 13);
  }

  @Test
  public void locationOfArgContextWithName() {
    given(willReturn(paramNameContext), argContext).paramName();
    given(willReturn(startToken), paramNameContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(argContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void locationOfArgContextWithoutParamName() {
    given(willReturn(expressionContext), argContext).expression();
    given(willReturn(startToken), expressionContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(argContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void locationOfParserRuleContext() {
    given(willReturn(startToken), parserRuleContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(parserRuleContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void locationOfToken() {
    given(willReturn(line), startToken).getLine();
    when(location = locationOf(startToken));
    when(location.line());
    thenReturned(line);
  }
}
