package org.smoothbuild.builtin.util;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;

public class UtilsTest {
  private ArrayList<Object> iterable;
  private Set<Object> set;
  private Object object1;
  private Object object2;

  @Test
  public void empty_iterable_has_size_zero() {
    given(iterable = new ArrayList<>());
    when(Utils.iterableSize(iterable));
    thenReturned(0);
  }

  @Test
  public void iterable_with_one_element_has_size_one() {
    given(iterable = new ArrayList<>());
    given(iterable.add(new Object()));
    when(Utils.iterableSize(iterable));
    thenReturned(1);
  }

  @Test
  public void iterable_with_two_elements_has_size_two() {
    given(iterable = new ArrayList<>());
    given(iterable.add(new Object()));
    given(iterable.add(new Object()));
    when(Utils.iterableSize(iterable));
    thenReturned(2);
  }

  @Test
  public void iterable_with_zero_elements_is_empty() throws Exception {
    given(iterable = new ArrayList<>());
    when(Utils.isEmptyIterable(iterable));
    thenReturned(true);
  }

  @Test
  public void iterable_with_one_element_is_not_empty() throws Exception {
    given(iterable = new ArrayList<>());
    given(iterable.add(new Object()));
    when(Utils.isEmptyIterable(iterable));
    thenReturned(false);
  }

  @Test
  public void immutable_set_cannot_be_changed() throws Exception {
    given(set = Utils.immutableSet(new Object(), new Object()));
    when(set).add(new Object());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void immutable_set_contains_all_elements() throws Exception {
    given(object1 = new Object());
    given(object2 = new Object());
    when(Utils.immutableSet(object1, object2));
    thenReturned(containsInAnyOrder(object1, object2));
  }

  @Test(expected = NullPointerException.class)
  public void check_not_null_throws_exception_for_null_argument() throws Exception {
    Utils.checkNotNull(null);
  }

  public void check_not_null_returns_non_null_argument() throws Exception {
    given(object1 = new Object());
    when(Utils.checkNotNull(object1));
    thenReturned(object1);
  }

}
