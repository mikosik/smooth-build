package org.smoothbuild.builtin.file.match.testing;

import static org.smoothbuild.builtin.file.match.testing.HelpTester.endsWithThreeLetters;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class HelpTesterTest {
  @Test
  public void empty_string_does_not_end_with_three_letters() {
    when(endsWithThreeLetters(""));
    thenReturned(false);
  }

  @Test
  public void two_letters_string_does_not_end_with_three_letters() {
    when(endsWithThreeLetters("ab"));
    thenReturned(false);
  }

  @Test
  public void three_letters_string_ends_with_three_letters() {
    when(endsWithThreeLetters("abc"));
    thenReturned(true);
  }

  @Test
  public void long_word_ends_with_three_letters() {
    when(endsWithThreeLetters("abcdefghi"));
    thenReturned(true);
  }

  @Test
  public void long_word_with_digit_at_the_end_does_not_end_with_three_letters() {
    when(endsWithThreeLetters("abcdefghi1"));
    thenReturned(false);
  }
}
