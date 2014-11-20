package org.smoothbuild.testing;

import static org.smoothbuild.testing.parse.ScriptBuilder.script;
import static org.testory.Testory.*;

import org.junit.Test;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class ScriptBuilderTest {
  private ScriptBuilder builder;

  @Test
  public void new_line_is_appended_to_every_line() {
    given(builder = new ScriptBuilder());
    given(builder).addLine("abc");
    given(builder).addLine("def");
    when(builder.build());
    thenReturned("abc\ndef\n");
  }

  @Test
  public void single_quotes_are_changed_to_double_quotes() throws Exception {
    given(builder = new ScriptBuilder());
    given(builder).addLine("a 'message' quoted");
    when(builder).build();
    thenReturned("a \"message\" quoted\n");
  }

  @Test
  public void single_quotes_are_changed_to_double_quotes_by_single_line_script() throws Exception {
    when(script("a 'message' quoted"));
    thenReturned("a \"message\" quoted");
  }

  @Test
  public void double_quotes_are_unchanged() throws Exception {
    given(builder = new ScriptBuilder());
    given(builder).addLine("a \"message\" quoted");
    when(builder).build();
    thenReturned("a \"message\" quoted\n");
  }

  @Test
  public void double_quotes_are_unchanged_by_single_line_script() throws Exception {
    when(script("a \"message\" quoted"));
    thenReturned("a \"message\" quoted");
  }
}
