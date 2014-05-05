package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.Constants.DOUBLE_STAR;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.testory.Closure;

import com.google.common.collect.ImmutableList;

public class NamePatternTest {
  private NamePattern pattern;

  @Test
  public void null_pattern_is_forbidden() {
    when(namePatternClosure(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void slash_in_pattern_is_forbidden() {
    when(namePatternClosure("abc/def"));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void empty_pattern_is_forbidden() throws Exception {
    when(namePatternClosure(""));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void double_star_pattern_is_allowed() throws Exception {
    when(namePatternClosure(DOUBLE_STAR));
    thenReturned();
  }

  @Test
  public void double_star_with_suffix_is_not_allowed() throws Exception {
    when(namePatternClosure(DOUBLE_STAR + "a"));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void double_star_with_prefix_is_not_allowed() throws Exception {
    when(namePatternClosure("a" + DOUBLE_STAR));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void three_star_pattern_is_not_allowed() throws Exception {
    when(namePatternClosure("***"));
    thenThrown(IllegalArgumentException.class);
  }

  // hasStars()

  @Test
  public void hasStars_returns_false_for_pattern_that_contains_no_stars() throws Exception {
    given(pattern = namePattern("abcdef"));
    when(pattern.hasStars());
    thenReturned(false);
  }

  @Test
  public void hasStars_returns_true_for_one_star_pattern() throws Exception {
    given(pattern = namePattern("*"));
    when(pattern.hasStars());
    thenReturned(true);
  }

  @Test
  public void hasStars_returns_true_for_pattern_that_contains_one_star() throws Exception {
    given(pattern = namePattern("abc*def"));
    when(pattern.hasStars());
    thenReturned(true);
  }

  @Test
  public void hasStars_returns_true_for_pattern_that_contains_two_stars() throws Exception {
    given(pattern = namePattern("abc*def*ghi"));
    when(pattern.hasStars());
    thenReturned(true);
  }

  // isSingleStar()

  @Test
  public void isSingleStar_returns_false_for_pattern_that_contains_one_star() throws Exception {
    given(pattern = namePattern("*abc"));
    when(pattern.isSingleStar());
    thenReturned(false);
  }

  @Test
  public void isSingleStar_returns_true_for_one_star_pattern() throws Exception {
    given(pattern = namePattern("*"));
    when(pattern.isSingleStar());
    thenReturned(true);
  }

  // isDoubleStar()

  @Test
  public void isDoubleStar_returns_false_for_pattern_that_contains_no_star() throws Exception {
    given(pattern = namePattern("abc"));
    when(pattern.isDoubleStar());
    thenReturned(false);
  }

  @Test
  public void isDoubleStar_returns_false_for_pattern_that_contains_one_star() throws Exception {
    given(pattern = namePattern("*abc"));
    when(pattern.isDoubleStar());
    thenReturned(false);
  }

  @Test
  public void isDoubleStar_returns_false_for_pattern_that_contains_two_disjoint_stars()
      throws Exception {
    given(pattern = namePattern("*abc*def"));
    when(pattern.isDoubleStar());
    thenReturned(false);
  }

  @Test
  public void isDoubleStar_returns_true_for_double_star_pattern() throws Exception {
    given(pattern = namePattern("**"));
    when(pattern.isDoubleStar());
    thenReturned(true);
  }

  // parts()

  @Test
  public void parts_returns_pattern_parts() throws Exception {
    given(pattern = namePattern("abc*def*ghi"));
    when(pattern.parts());
    thenReturned(ImmutableList.of("abc", "*", "def", "*", "ghi"));
  }

  @Test
  public void single_star_pattern_has_one_part() throws Exception {
    given(pattern = namePattern("*"));
    when(pattern.parts());
    thenReturned(ImmutableList.of("*"));
  }

  @Test
  public void double_star_pattern_has_one_part() throws Exception {
    given(pattern = namePattern("**"));
    when(pattern.parts());
    thenReturned(ImmutableList.of("**"));
  }

  private Closure namePatternClosure(final String value) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return namePattern(value);
      }
    };
  }
}
