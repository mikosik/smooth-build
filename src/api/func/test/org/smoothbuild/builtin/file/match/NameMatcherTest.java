package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;

public class NameMatcherTest {
  private NameMatcher nameMatcher;
  private NamePattern pattern;

  @Test()
  public void double_star_pattern_is_forbidden() {
    given(pattern = namePattern("**"));
    when(() -> new NameMatcher(pattern));
    thenThrown(IllegalArgumentException.class);
  }

  // single star pattern

  @Test
  public void single_star_matches_one_character_string() {
    given(nameMatcher = nameMatcher("*"));
    when(nameMatcher.test(path("a")));
    thenReturned(true);
  }

  @Test
  public void single_star_matches_long_string() {
    given(nameMatcher = nameMatcher("*"));
    when(nameMatcher.test(path("abcdefghijklmnop")));
    thenReturned(true);
  }

  // pattern that starts with star

  @Test
  public void pattern_that_have_star_plus_ending_matches_lonely_ending() {
    given(nameMatcher = nameMatcher("*abc"));
    when(nameMatcher.test(path("abc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_star_plus_ending_matches_lonely_ending_prefixed_by_half_of_ending() {
    given(nameMatcher = nameMatcher("*abc"));
    when(nameMatcher.test(path("ababc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_star_plus_ending_matches_doubled_lonely_ending() {
    given(nameMatcher = nameMatcher("*abc"));
    when(nameMatcher.test(path("abcabc")));
    thenReturned(true);
  }

  // pattern that starts with star

  @Test
  public void pattern_that_have_prefixed_star_matches_lonely_prefix() {
    given(nameMatcher = nameMatcher("abc*"));
    when(nameMatcher.test(path("abc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_prefixed_star_matches_lonely_prefix_suffixed_by_half_of_prefix() {
    given(nameMatcher = nameMatcher("abc*"));
    when(nameMatcher.test(path("abcab")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_prefixed_star_matches_doubled_lonely_prefix() {
    given(nameMatcher = nameMatcher("abc*"));
    when(nameMatcher.test(path("abcabc")));
    thenReturned(true);
  }

  // pattern with one star in the middle

  @Test
  public void pattern_that_have_middle_star_does_not_match_lonely_prefix() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.test(path("abc")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_middle_star_does_not_match_lonely_ending() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.test(path("def")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_middle_star_matches_prefix_plus_ending() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.test(path("abcdef")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_middle_star_matches_prefix_and_ending_split_by_its_halfs() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.test(path("abcabdedef")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_middle_star_matches_doubled_prefix_plus_doubled_suffix() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.test(path("abcabcdefdef")));
    thenReturned(true);
  }

  // pattern with trailing stars

  @Test
  public void pattern_that_have_trailing_stars_matches_lonely_middle_part() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.test(path("abc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_prefixed_middle_part() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.test(path("xxxabc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_suffixed_middle_part() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.test(path("abcyyy")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_surrounded_middle_part() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.test(path("xxxabcyyy")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_surrounded_middle_part_prefixed_with_half_of_middle() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.test(path("xxxababcyyy")));
    thenReturned(true);
  }

  // pattern with two inner stars

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_stars() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.test(path("abcdefghi")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_match_when_middle_is_missing() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.test(path("abcghi")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_first_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.test(path("abcdefyyyghi")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_second_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.test(path("abcxxxdefghi")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_something_for_each_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.test(path("abcxxxdefyyyghi")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_matches_string_with_not_full_middle_part() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.test(path("abcdeghi")));
    thenReturned(false);
  }

  // pattern with three inner stars

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_stars() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcdefghijkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_three_inner_stars_does_not_match_when_left_middle_is_missing() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcghijkl")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_three_inner_stars_does_not_match_when_right_middle_is_missing() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcdefjkl")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_first_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcdefyyyghizzzjkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_second_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcxxxdefghizzzjkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_third_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcxxxdefyyyghijkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_something_for_each_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcxxxdefyyyghizzzjkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_match_string_with_not_full_left_middle_part() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcdeghijkl")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_match_string_with_not_full_right_middle_part() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.test(path("abcdefghjkl")));
    thenReturned(false);
  }

  private static NameMatcher nameMatcher(final String pattern) {
    return new NameMatcher(namePattern(pattern));
  }
}
