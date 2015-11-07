package org.smoothbuild.builtin.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class PredicatesEndsWithPredicateTest {
  private Predicate<String> predicate;
  private final String suffix = "suffix";

  @Test
  public void matches_string_containing_only_suffix() {
    given(predicate = Predicates.endsWith(suffix));
    when(predicate.test(suffix));
    thenReturned(true);
  }

  @Test
  public void matches_string_containing_suffix() throws Exception {
    given(predicate = Predicates.endsWith(suffix));
    when(predicate.test("abc" + suffix));
    thenReturned(true);
  }

  @Test
  public void does_not_match_string_that_ends_with_suffix_plus_space() throws Exception {
    given(predicate = Predicates.endsWith(suffix));
    when(predicate.test(suffix + " "));
    thenReturned(false);
  }

  @Test
  public void does_not_match_string_that_does_not_end_with_suffix() throws Exception {
    given(predicate = Predicates.endsWith(suffix));
    when(predicate.test("abc"));
    thenReturned(false);
  }

  @Test
  public void does_not_match_empty_string() throws Exception {
    given(predicate = Predicates.endsWith(suffix));
    when(predicate.test(""));
    thenReturned(false);
  }

  @Test
  public void empty_suffix_matches_anything() throws Exception {
    given(predicate = Predicates.endsWith(""));
    when(predicate.test("abc"));
    thenReturned(true);
  }

  @Test
  public void empty_suffix_matches_empty_string() throws Exception {
    given(predicate = Predicates.endsWith(""));
    when(predicate.test(""));
    thenReturned(true);
  }
}
