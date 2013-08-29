package org.smoothbuild.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.testing.ScriptBuilder.script;

import org.junit.Test;

public class ScriptBuilderTest {

  @Test
  public void newLinesAreAppendedAtTheEndOfLines() {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("abc");
    builder.addLine("def");
    assertThat(builder.build()).isEqualTo("abc\ndef\n");
  }

  @Test
  public void singleQuotesAreChangedIntoDoubleQuotes() throws Exception {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a 'message' quoted");
    assertThat(builder.build()).isEqualTo("a \"message\" quoted\n");
  }

  @Test
  public void singleQuotesAreChangedIntoDoubleQuotesByOneLineScript() throws Exception {
    assertThat(script("a 'message' quoted")).isEqualTo("a \"message\" quoted");
  }

  @Test
  public void doubleQuotesAreUnchanged() throws Exception {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a \"message\" quoted");
    assertThat(builder.build()).isEqualTo("a \"message\" quoted\n");
  }

  @Test
  public void doubleQuotesAreUnchangedByOneLineScript() throws Exception {
    assertThat(script("a \"message\" quoted")).isEqualTo("a \"message\" quoted");
  }
}
