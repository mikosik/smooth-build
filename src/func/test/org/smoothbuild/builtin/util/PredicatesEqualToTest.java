package org.smoothbuild.builtin.util;

import static org.smoothbuild.builtin.util.Predicates.equalTo;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class PredicatesEqualToTest {
  private Predicate<String> predicate;
  private String object;

  @Test
  public void object_is_equal_to_itself() {
    given(object = "abc");
    given(predicate = equalTo(object));
    when(predicate.test(object));
    thenReturned(true);
  }

  @Test
  public void predicate_returns_true_for_equal_objects() {
    given(predicate = equalTo("abc"));
    when(predicate.test("abc"));
    thenReturned(true);
  }

  @Test
  public void predicate_returns_false_for_not_equal_objects() {
    given(predicate = equalTo("abc"));
    when(predicate.test("def"));
    thenReturned(false);
  }

  @Test
  public void predicate_returns_true_when_both_objects_are_null() {
    given(predicate = equalTo(null));
    when(predicate.test(null));
    thenReturned(true);
  }

  @Test
  public void predicate_returns_false_when_only_first_object_is_null() {
    given(predicate = equalTo(null));
    when(predicate.test("abc"));
    thenReturned(false);
  }

  @Test
  public void predicate_returns_false_when_only_second_object_is_null() {
    given(predicate = equalTo("abc"));
    when(predicate.test(null));
    thenReturned(false);
  }
}
