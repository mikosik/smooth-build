package org.smoothbuild.util;

import static org.smoothbuild.util.Text.unlines;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class TextTest {
  @Test
  public void unlining_zero_lines_gives_empty_string() {
    when(unlines());
    thenReturned("");
  }

  @Test
  public void unlining_one_line_gives_unchanged_line() {
    when(unlines("abc"));
    thenReturned("abc");
  }

  @Test
  public void unlining_more_lines() {
    when(unlines(
        "abc",
        "def",
        "ghi"));
    thenReturned("abc\ndef\nghi");
  }

  @Test
  public void unlining_doesnt_change_new_lines() {
    when(unlines("abc\n123"));
    thenReturned("abc\n123");
  }
}
