package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.Test;
import org.smoothbuild.problem.SourceLocation;

public class HelpersTest {
  ParserRuleContext parserRuleContext = mock(ParserRuleContext.class);
  Token startToken = mock(Token.class);
  Token stopToken = mock(Token.class);

  @Test
  public void locationOfParserRuleContext() {
    int start = 11;
    int line = 13;
    int end = 17;
    when(parserRuleContext.getStart()).thenReturn(startToken);
    when(parserRuleContext.getStop()).thenReturn(stopToken);
    when(startToken.getStartIndex()).thenReturn(start);
    when(startToken.getLine()).thenReturn(line);
    when(stopToken.getStopIndex()).thenReturn(end);

    SourceLocation location = Helpers.locationOf(parserRuleContext);

    assertThat(location.line()).isEqualTo(line);
    assertThat(location.startPosition()).isEqualTo(start);
    assertThat(location.endPosition()).isEqualTo(end);
  }

  @Test
  public void locationOfToken() throws Exception {
    int start = 11;
    int line = 13;
    int end = 17;
    when(startToken.getStartIndex()).thenReturn(start);
    when(startToken.getLine()).thenReturn(line);
    when(startToken.getStopIndex()).thenReturn(end);

    SourceLocation location = Helpers.locationOf(startToken);

    assertThat(location.line()).isEqualTo(line);
    assertThat(location.startPosition()).isEqualTo(start);
    assertThat(location.endPosition()).isEqualTo(end);
  }
}
