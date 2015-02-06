package org.smoothbuild.builtin.util;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;

public class PredicatesAndTest {
  private Predicate<Object> predicate;
  private Predicate<Object> predicate2;
  private Object value;

  @Test
  public void returns_true_when_both_wrapped_predicates_return_true() throws Exception {
    given(predicate = mock(Predicate.class));
    given(predicate2 = mock(Predicate.class));
    given(value = new Object());
    given(willReturn(true), predicate).test(value);
    given(willReturn(true), predicate2).test(value);
    when(Predicates.and(predicate, predicate2).test(value));
    thenReturned(true);
  }

  @Test
  public void returns_false_when_only_first_wrapped_predicate_returns_true() throws Exception {
    given(predicate = mock(Predicate.class));
    given(predicate2 = mock(Predicate.class));
    given(value = new Object());
    given(willReturn(true), predicate).test(value);
    given(willReturn(false), predicate2).test(value);
    when(Predicates.and(predicate, predicate2).test(value));
    thenReturned(false);
  }

  @Test
  public void returns_false_when_only_second_wrapped_predicate_returns_true() throws Exception {
    given(predicate = mock(Predicate.class));
    given(predicate2 = mock(Predicate.class));
    given(value = new Object());
    given(willReturn(false), predicate).test(value);
    given(willReturn(true), predicate2).test(value);
    when(Predicates.and(predicate, predicate2).test(value));
    thenReturned(false);
  }

  @Test
  public void returns_false_when_no_wrapped_predicate_returns_true() throws Exception {
    given(predicate = mock(Predicate.class));
    given(predicate2 = mock(Predicate.class));
    given(value = new Object());
    given(willReturn(false), predicate).test(value);
    given(willReturn(false), predicate2).test(value);
    when(Predicates.and(predicate, predicate2).test(value));
    thenReturned(false);
  }
}
