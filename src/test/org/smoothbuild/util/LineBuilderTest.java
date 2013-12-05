package org.smoothbuild.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;

import org.junit.Test;

public class LineBuilderTest {
  String string = "string";
  String string2 = "string2";

  LineBuilder lineBuilder = new LineBuilder();

  @Test
  public void build_returns_empty_string_when_nothing_was_added() throws Exception {
    when(lineBuilder = new LineBuilder());
    thenEqual(lineBuilder.build(), "");
  }

  @Test
  public void build_returns_added_string() throws Exception {
    when(lineBuilder).add(string);
    thenEqual(lineBuilder.build(), string);
  }

  @Test
  public void build_returns_all_added_strings() throws Exception {
    given(lineBuilder).add(string);
    when(lineBuilder).add(string2);
    thenEqual(lineBuilder.build(), string + string2);
  }

  @Test
  public void build_returns_added_line_with_new_line_appended() throws Exception {
    when(lineBuilder).addLine(string);
    thenEqual(lineBuilder.build(), string + "\n");
  }

  @Test
  public void build_returns_added_lines_with_new_line_appended() throws Exception {
    given(lineBuilder).addLine(string);
    when(lineBuilder).addLine(string2);
    thenEqual(lineBuilder.build(), string + "\n" + string2 + "\n");
  }
}
