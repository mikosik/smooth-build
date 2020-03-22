package org.smoothbuild.parse;

import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.nio.file.Paths;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ExprContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.lang.base.Location;

public class LocationHelpersTest {
  private int line;
  private Token startToken;
  private Location location;
  private ParserRuleContext parserRuleContext;
  private ExprContext expressionContext;
  private PipeContext pipeContext;
  private ArgContext argContext;
  private NameContext nameContext;

  @BeforeEach
  public void before() {
    givenTest(this);
    given(line = 13);
  }

  @Test
  public void location_of_arg_context_with_param_name() {
    given(willReturn(nameContext), argContext).name();
    given(willReturn(startToken), nameContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(Paths.get("script.smooth"), argContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void locatin_of_arg_context_without_param_name() {
    given(willReturn(pipeContext), argContext).pipe();
    given(willReturn(startToken), pipeContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(Paths.get("script.smooth"), argContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void location_of_parser_rule_context() {
    given(willReturn(startToken), parserRuleContext).getStart();
    given(willReturn(line), startToken).getLine();
    given(location = locationOf(Paths.get("script.smooth"), parserRuleContext));
    when(location.line());
    thenReturned(line);
  }

  @Test
  public void location_of_token() {
    given(willReturn(line), startToken).getLine();
    when(location = locationOf(Paths.get("script.smooth"), startToken));
    when(location.line());
    thenReturned(line);
  }
}
