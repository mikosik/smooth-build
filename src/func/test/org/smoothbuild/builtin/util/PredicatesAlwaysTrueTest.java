package org.smoothbuild.builtin.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class PredicatesAlwaysTrueTest {
  private Predicate<Object> predicate;

  @Test
  public void returns_true_for_object() {
    given(predicate = Predicates.alwaysTrue());
    when(predicate.test(new Object()));
    thenReturned(true);
  }

  @Test
  public void returns_true_for_null() {
    given(predicate = Predicates.alwaysTrue());
    when(predicate.test(null));
    thenReturned(true);
  }
}
