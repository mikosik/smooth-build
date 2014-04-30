package org.smoothbuild.builtin.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class EndsWithPredicateTest {
  private EndsWithPredicate predicate;
  private final String suffix = "suffix";

  @Test(expected = NullPointerException.class)
  public void nullSuffixIsForbidden() throws Exception {
    new EndsWithPredicate(null);
  }

  @Test
  public void matches_string_containing_only_suffix() {
    given(predicate = new EndsWithPredicate(suffix));
    when(predicate).apply(suffix);
    thenReturned(true);
  }

  @Test
  public void matches_string_containing_suffix() throws Exception {
    given(predicate = new EndsWithPredicate(suffix));
    when(predicate).apply("abc" + suffix);
    thenReturned(true);
  }

  @Test
  public void does_not_match_string_that_ends_with_suffix_plus_space() throws Exception {
    given(predicate = new EndsWithPredicate(suffix));
    when(predicate).apply(suffix + " ");
    thenReturned(false);
  }

  @Test
  public void does_not_match_string_that_does_not_end_with_suffix() throws Exception {
    given(predicate = new EndsWithPredicate(suffix));
    when(predicate).apply("abc");
    thenReturned(false);
  }

  @Test
  public void does_not_match_empty_string() throws Exception {
    given(predicate = new EndsWithPredicate(suffix));
    when(predicate).apply("");
    thenReturned(false);
  }

  @Test
  public void empty_suffix_matches_anything() throws Exception {
    given(predicate = new EndsWithPredicate(""));
    when(predicate).apply("abc");
    thenReturned(true);
  }

  @Test
  public void empty_suffix_matches_empty_string() throws Exception {
    given(predicate = new EndsWithPredicate(""));
    when(predicate).apply("");
    thenReturned(true);
  }
}
