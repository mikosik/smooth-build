package org.smoothbuild.fs.match;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.match.NamePattern.namePattern;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.fs.match.NameMatcher;
import org.smoothbuild.fs.match.NamePattern;
import org.testory.common.Closure;

public class NameMatcherTest {
  NameMatcher nameMatcher;
  NamePattern pattern;

  @Test()
  public void double_star_pattern_is_forbidden() {
    given(pattern = namePattern("**"));
    when($nameMatcher(pattern));
    thenThrown(IllegalArgumentException.class);
  }

  // single star pattern

  @Test
  public void single_star_matches_one_character_string() {
    given(nameMatcher = nameMatcher("*"));
    when(nameMatcher.apply(path("a")));
    thenReturned(true);
  }

  @Test
  public void single_star_matches_long_string() {
    given(nameMatcher = nameMatcher("*"));
    when(nameMatcher.apply(path("abcdefghijklmnop")));
    thenReturned(true);
  }

  // pattern that starts with star

  @Test
  public void pattern_that_have_star_plus_ending_matches_lonely_ending() {
    given(nameMatcher = nameMatcher("*abc"));
    when(nameMatcher.apply(path("abc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_star_plus_ending_matches_lonely_ending_prefixed_by_half_of_ending() {
    given(nameMatcher = nameMatcher("*abc"));
    when(nameMatcher.apply(path("ababc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_star_plus_ending_matches_doubled_lonely_ending() {
    given(nameMatcher = nameMatcher("*abc"));
    when(nameMatcher.apply(path("abcabc")));
    thenReturned(true);
  }

  // pattern that starts with star

  @Test
  public void pattern_that_have_prefixed_star_matches_lonely_prefix() {
    given(nameMatcher = nameMatcher("abc*"));
    when(nameMatcher.apply(path("abc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_prefixed_star_matches_lonely_prefix_suffixed_by_half_of_prefix() {
    given(nameMatcher = nameMatcher("abc*"));
    when(nameMatcher.apply(path("abcab")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_prefixed_star_matches_doubled_lonely_prefix() {
    given(nameMatcher = nameMatcher("abc*"));
    when(nameMatcher.apply(path("abcabc")));
    thenReturned(true);
  }

  // pattern with one star in the middle

  @Test
  public void pattern_that_have_middle_star_does_not_match_lonely_prefix() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.apply(path("abc")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_middle_star_does_not_match_lonely_ending() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.apply(path("def")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_middle_star_matches_prefix_plus_ending() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.apply(path("abcdef")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_middle_star_matches_prefix_and_ending_split_by_its_halfs() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.apply(path("abcabdedef")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_middle_star_matches_doubled_prefix_plus_doubled_suffix() {
    given(nameMatcher = nameMatcher("abc*def"));
    when(nameMatcher.apply(path("abcabcdefdef")));
    thenReturned(true);
  }

  // pattern with trailing stars

  @Test
  public void pattern_that_have_trailing_stars_matches_lonely_middle_part() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.apply(path("abc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_prefixed_middle_part() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.apply(path("xxxabc")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_suffixed_middle_part() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.apply(path("abcyyy")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_surrounded_middle_part() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.apply(path("xxxabcyyy")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_trailing_stars_matches_surrounded_middle_part_prefixed_with_half_of_middle() {
    given(nameMatcher = nameMatcher("*abc*"));
    when(nameMatcher.apply(path("xxxababcyyy")));
    thenReturned(true);
  }

  // pattern with two inner stars

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_stars() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.apply(path("abcdefghi")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_match_when_middle_is_missing() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.apply(path("abcghi")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_first_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.apply(path("abcdefyyyghi")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_nothing_for_second_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.apply(path("abcxxxdefghi")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_matches_string_with_something_for_each_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.apply(path("abcxxxdefyyyghi")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_matches_string_with_not_full_middle_part() {
    given(nameMatcher = nameMatcher("abc*def*ghi"));
    when(nameMatcher.apply(path("abcdeghi")));
    thenReturned(false);
  }

  // pattern with three inner stars

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_stars() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcdefghijkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_three_inner_stars_does_not_match_when_left_middle_is_missing() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcghijkl")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_three_inner_stars_does_not_match_when_right_middle_is_missing() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcdefjkl")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_first_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcdefyyyghizzzjkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_second_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcxxxdefghizzzjkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_nothing_for_third_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcxxxdefyyyghijkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_three_inner_stars_matches_string_with_something_for_each_star() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcxxxdefyyyghizzzjkl")));
    thenReturned(true);
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_match_string_with_not_full_left_middle_part() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcdeghijkl")));
    thenReturned(false);
  }

  @Test
  public void pattern_that_have_two_inner_stars_does_not_match_string_with_not_full_right_middle_part() {
    given(nameMatcher = nameMatcher("abc*def*ghi*jkl"));
    when(nameMatcher.apply(path("abcdefghjkl")));
    thenReturned(false);
  }

  private static Closure $nameMatcher(final NamePattern pattern) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new NameMatcher(pattern);
      }
    };
  }

  private static NameMatcher nameMatcher(final String pattern) {
    return new NameMatcher(namePattern(pattern));
  }
}
