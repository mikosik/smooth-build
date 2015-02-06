package org.smoothbuild.builtin.util;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;

public class PredicatesNotTest {
  private Predicate<Object> predicate;
  private Object value;

  @Test
  public void returns_false_when_wrapped_predicate_returns_true() throws Exception {
    given(predicate = mock(Predicate.class));
    given(value = new Object());
    given(willReturn(true), predicate).test(value);
    when(Predicates.not(predicate).test(value));
    thenReturned(false);
  }

  @Test
  public void returns_true_when_wrapped_predicate_returns_false() throws Exception {
    given(predicate = mock(Predicate.class));
    given(value = new Object());
    given(willReturn(false), predicate).test(value);
    when(Predicates.not(predicate).test(value));
    thenReturned(true);
  }
}
