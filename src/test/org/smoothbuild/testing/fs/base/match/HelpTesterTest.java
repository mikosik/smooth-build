package org.smoothbuild.testing.fs.base.match;

import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.testory.common.Closure;

public class HelpTesterTest {

  @Test
  public void empty_string_does_not_end_with_three_characters() {
    when(endsWithThreeCharacters(""));
    thenReturned(false);
  }

  @Test
  public void two_letters_string_does_not_end_with_three_characters() {
    when(endsWithThreeCharacters("ab"));
    thenReturned(false);
  }

  @Test
  public void three_letters_string_ends_with_three_characters() {
    when(endsWithThreeCharacters("abc"));
    thenReturned(true);
  }

  @Test
  public void long_word_ends_with_three_characters() {
    when(endsWithThreeCharacters("abcdefghi"));
    thenReturned(true);
  }

  @Test
  public void long_word_with_digit_at_the_end_does_not_end_with_three_characters() {
    when(endsWithThreeCharacters("abcdefghi1"));
    thenReturned(false);
  }

  private Closure endsWithThreeCharacters(final String string) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return HelpTester.endsWithThreeCharacters(string);
      }
    };
  }
}
