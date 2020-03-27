package org.smoothbuild.builtin.file.match;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.builtin.file.match.Constants.DOUBLE_STAR;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ClassCanBeStatic")
public class NamePatternTest {
  @Nested
  class creating_pattern {
    @Test
    public void null_is_forbidden() {
      assertCall(() -> namePattern(null))
          .throwsException(NullPointerException.class);
    }

    @Test
    public void with_slash_is_forbidden() {
      assertCall(() -> namePattern("abc/def"))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void for_empty_string_is_forbidden() {
      assertCall(() -> namePattern(""))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void double_star_pattern_is_allowed() {
      namePattern(DOUBLE_STAR);
    }

    @Test
    public void double_star_with_suffix_is_not_allowed() {
      assertCall(() -> namePattern(DOUBLE_STAR + "a"))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void double_star_with_prefix_is_not_allowed() {
      assertCall(() -> namePattern("a" + DOUBLE_STAR))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void three_star_pattern_is_not_allowed() {
      assertCall(() -> namePattern("***"))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class hasStars {
    @Test
    public void returns_false_for_pattern_that_contains_no_stars() {
      assertThat(namePattern("abcdef").hasStars())
          .isFalse();
    }

    @Test
    public void returns_true_for_one_star_pattern() {
      assertThat(namePattern("*").hasStars())
          .isTrue();
    }

    @Test
    public void returns_true_for_pattern_that_contains_one_star() {
      assertThat(namePattern("abc*def").hasStars())
          .isTrue();
    }

    @Test
    public void returns_true_for_pattern_that_contains_two_stars() {
      assertThat(namePattern("abc*def*ghi").hasStars())
          .isTrue();
    }
  }

  @Nested
  class isSingleStar {
    @Test
    public void returns_false_for_pattern_that_contains_one_star() {
      assertThat(namePattern("*abc").isSingleStar())
          .isFalse();
    }

    @Test
    public void returns_true_for_one_star_pattern() {
      assertThat(namePattern("*").isSingleStar())
          .isTrue();
    }
  }

  @Nested
  class isDoubleStart {
    @Test
    public void returns_false_for_pattern_that_contains_no_star() {
      assertThat(namePattern("abc").isDoubleStar())
          .isFalse();
    }

    @Test
    public void returns_false_for_pattern_that_contains_one_star() {
      assertThat(namePattern("*abc").isDoubleStar())
          .isFalse();
    }

    @Test
    public void returns_false_for_pattern_that_contains_two_disjoint_stars() {
      assertThat(namePattern("*abc*def").isDoubleStar())
          .isFalse();
    }

    @Test
    public void returns_true_for_double_star_pattern() {
      assertThat(namePattern("**").isDoubleStar())
          .isTrue();
    }
  }

  @Nested
  class parts {
    @Test
    public void returns_pattern_parts() {
      assertThat(namePattern("abc*def*ghi").parts())
          .containsExactly("abc", "*", "def", "*", "ghi")
          .inOrder();
    }

    @Test
    public void single_star_pattern_has_one_part() {
      assertThat(namePattern("*").parts())
          .containsExactly("*");
    }

    @Test
    public void double_star_pattern_has_one_part() {
      assertThat(namePattern("**").parts())
          .containsExactly("**");
    }
  }
}
