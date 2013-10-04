package org.smoothbuild.util;

import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.testory.common.Closure;

public class StringUtilsTest {

  @Test
  public void null_string_is_forbidden() {
    when(countOf(null, 'a'));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void empty_string_has_no_characters() {
    when(countOf("", 'a'));
    thenReturned(0);
  }

  @Test
  public void one_character_long_string_has_its_character_once() {
    when(countOf("a", 'a'));
    thenReturned(1);
  }

  @Test
  public void string_with_exactly_three_equal_characters() {
    when(countOf("aaa", 'a'));
    thenReturned(3);
  }

  @Test
  public void string_with_two_matching_characters() {
    when(countOf("aaaaaxaaaaaaxaaaa", 'x'));
    thenReturned(2);
  }

  @Test
  public void string_that_does_not_have_matching_character() {
    when(countOf("abcdefghijk", 'x'));
    thenReturned(0);
  }

  private Closure countOf(final String string, final char character) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return StringUtils.countOf(string, character);
      }
    };
  }
}
