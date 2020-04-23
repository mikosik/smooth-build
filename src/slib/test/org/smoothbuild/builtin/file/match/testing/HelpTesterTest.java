package org.smoothbuild.builtin.file.match.testing;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.builtin.file.match.testing.HelpTester.endsWithThreeLetters;

import org.junit.jupiter.api.Test;

public class HelpTesterTest {
  @Test
  public void empty_string_does_not_end_with_three_letters() {
    assertThat(endsWithThreeLetters(""))
        .isFalse();
  }

  @Test
  public void two_letters_string_does_not_end_with_three_letters() {
    assertThat(endsWithThreeLetters("ab"))
        .isFalse();
  }

  @Test
  public void three_letters_string_ends_with_three_letters() {
    assertThat(endsWithThreeLetters("abc"))
        .isTrue();
  }

  @Test
  public void long_word_ends_with_three_letters() {
    assertThat(endsWithThreeLetters("abcdefghi"))
        .isTrue();
  }

  @Test
  public void long_word_with_digit_at_the_end_does_not_end_with_three_letters() {
    assertThat(endsWithThreeLetters("abcdefghi1"))
        .isFalse();
  }
}
