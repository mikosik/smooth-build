package org.smoothbuild.util;

import static org.smoothbuild.util.Sets.filter;
import static org.smoothbuild.util.Sets.map;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Predicates;

public class SetsTest {
  private Set<String> set;

  @Test
  public void mapping_empty_returns_empty() throws Exception {
    given(set = new HashSet<>());
    when(() -> map(set, String::toUpperCase));
    thenReturned(new HashSet<>());
  }

  @Test
  public void mapping_with_one_element() throws Exception {
    given(set = set("abc"));
    when(() -> map(set, String::toUpperCase));
    thenReturned(set("ABC"));
  }

  @Test
  public void mapping_with_two_elements() throws Exception {
    given(set = set("abc", "def"));
    when(() -> map(set, String::toUpperCase));
    thenReturned(set("ABC", "DEF"));
  }

  @Test
  public void filter_empty_returns_empty() throws Exception {
    given(set = set());
    when(() -> filter(set, Predicates.alwaysTrue()));
    thenReturned(set());
  }

  @Test
  public void filter_with_always_true_predicate_returns_same_list() throws Exception {
    given(set = set("first", "second", "third"));
    when(() -> filter(set, Predicates.alwaysTrue()));
    thenReturned(set("first", "second", "third"));
  }

  @Test
  public void filter_with_always_false_predicate_returns_empty_list() throws Exception {
    given(set = set("first", "second", "third"));
    when(() -> filter(set, Predicates.alwaysFalse()));
    thenReturned(set());
  }

  @Test
  public void filter_filters_elements() throws Exception {
    given(set = set("first", "second", "third"));
    when(() -> filter(set, s -> s.startsWith("s")));
    thenReturned(set("second"));
  }

  private static <T> Set<T> set(T... elements) {
    return com.google.common.collect.Sets.newHashSet(elements);
  }
}
