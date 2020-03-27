package org.smoothbuild.builtin.file.match;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;

public class NameMatcherTest {
  @Test()
  public void double_star_pattern_is_forbidden() {
    assertCall(() -> new NameMatcher(namePattern("**")))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void single_star_matches_one_character_string() {
    assertThat(nameMatcher("*").test(path("a")))
        .isTrue();
  }

  @Test
  public void single_star_matches_long_string() {
    assertThat(nameMatcher("*").test(path("abcdefghijklmnop")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_star_plus_ending_matches_lonely_ending() {
    assertThat(nameMatcher("*abc").test(path("abc")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_star_plus_ending_matches_lonely_ending_prefixed_by_half_of_ending() {
    assertThat(nameMatcher("*abc").test(path("ababc")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_star_plus_ending_matches_doubled_lonely_ending() {
    assertThat(nameMatcher("*abc").test(path("abcabc")))
        .isTrue();
  }

  // pattern that starts with star

  @Test
  public void pattern_that_have_prefixed_star_matches_lonely_prefix() {
    assertThat(nameMatcher("abc*").test(path("abc")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_prefixed_star_matches_lonely_prefix_suffixed_by_half_of_prefix() {
    assertThat(nameMatcher("abc*").test(path("abcab")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_prefixed_star_matches_doubled_lonely_prefix() {
    assertThat(nameMatcher("abc*").test(path("abcabc")))
        .isTrue();
  }

  // pattern with one star in the middle

  @Test
  public void pattern_that_have_middle_star_does_not_match_lonely_prefix() {
    assertThat(nameMatcher("abc*def").test(path("abc")))
        .isFalse();
  }

  @Test
  public void pattern_that_have_middle_star_does_not_match_lonely_ending() {
    assertThat(nameMatcher("abc*def").test(path("def")))
        .isFalse();
  }

  @Test
  public void pattern_that_have_middle_star_matches_prefix_plus_ending() {
    assertThat(nameMatcher("abc*def").test(path("abcdef")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_middle_star_matches_prefix_and_ending_separated_by_its_halfs() {
    assertThat(nameMatcher("abc*def").test(path("abcabdedef")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_middle_star_matches_doubled_prefix_plus_doubled_suffix() {
    assertThat(nameMatcher("abc*def").test(path("abcabcdefdef")))
        .isTrue();
  }

  // pattern with trailing stars

  @Test
  public void pattern_that_have_trailing_stars_matches_lonely_middle_part() {
    assertThat(nameMatcher("*abc*").test(path("abc")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_prefixed_middle_part() {
    assertThat(nameMatcher("*abc*").test(path("xxxabc")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_suffixed_middle_part() {
    assertThat(nameMatcher("*abc*").test(path("abcyyy")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_surrounded_middle_part() {
    assertThat(nameMatcher("*abc*").test(path("xxxabcyyy")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_surrounded_middle_part_prefixed_with_half_of_middle() {
    assertThat(nameMatcher("*abc*").test(path("xxxababcbcyyy")))
        .isTrue();
  }

  // pattern with two inner stars

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_stars() {
    assertThat(nameMatcher("*abc*def*ghi").test(path("abcdefghi")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_match_when_middle_is_missing() {
    assertThat(nameMatcher("*abc*def*ghi").test(path("abcghi")))
        .isFalse();
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_first_star() {
    assertThat(nameMatcher("*abc*def*ghi").test(path("abcdef___ghi")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_second_star() {
    assertThat(nameMatcher("*abc*def*ghi").test(path("abc___defghi")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_something_for_each_star() {
    assertThat(nameMatcher("*abc*def*ghi").test(path("abc___def___ghi")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_matches_string_with_not_full_middle_part() {
    assertThat(nameMatcher("*abc*def*ghi").test(path("abcdeghi")))
        .isFalse();
  }

  // pattern with three inner stars

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_stars() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abcdefghijkl")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_three_inner_stars_does_not_match_when_left_middle_is_missing() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abcghijkl")))
        .isFalse();
  }

  @Test
  public void pattern_that_have_three_inner_stars_does_not_match_when_right_middle_is_missing() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abcdefjkl")))
        .isFalse();
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_first_star() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abcdef___ghi___jkl")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_second_star() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abc___defghi___jkl")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_third_star() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abc___def___ghijkl")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_something_for_each_star() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abc___def___ghi___jkl")))
        .isTrue();
  }

  @Test
  public void pattern_that_have_three_inner_stars_does_not_match_string_with_not_full_left_middle_part() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abcdeghijkl")))
        .isFalse();
  }

  @Test
  public void pattern_that_have_three_inner_stars_does_not_match_string_with_not_full_right_middle_part() {
    assertThat(nameMatcher("abc*def*ghi*jkl").test(path("abcdefghjkl")))
        .isFalse();
  }

  private static NameMatcher nameMatcher(final String pattern) {
    return new NameMatcher(namePattern(pattern));
  }
}
