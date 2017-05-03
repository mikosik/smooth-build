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
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.lang.message.CodeLocation;

public class LocationHelpersTest {
  private int line;
  private Token startToken;
  private CodeLocation location;
  private ParserRuleContext parserRuleContext;
  private ExpressionContext expressionContext;
  private ArgContext argContext;
  private NameContext nameContext;

  @Before
  public void before() {
    givenTest(this);
    given(line = 13);
  }

  @Test
  public void location_of_arg_context_with_param_name() {
    given(willReturn(nameContext), argContext).name();
    given(willReturn(startToken), nameContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(argContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void locatin_of_arg_context_without_param_name() {
    given(willReturn(expressionContext), argContext).expression();
    given(willReturn(startToken), expressionContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(argContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void location_of_parser_rule_context() {
    given(willReturn(startToken), parserRuleContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(parserRuleContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void location_of_token() {
    given(willReturn(line), startToken).getLine();
    when(location = locationOf(startToken));
    when(location.line());
    thenReturned(line);
  }
}
