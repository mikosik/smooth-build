package org.smoothbuild.util;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.filter;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.base.Predicates;

public class ListsTest {
  private List<String> list;

  @Test
  public void list_with_no_elements() throws Exception {
    when(() -> list());
    thenReturned(empty());
  }

  @Test
  public void list_with_one_element() throws Exception {
    when(() -> list("abc"));
    thenReturned(contains("abc"));
  }

  @Test
  public void list_with_two_elements() throws Exception {
    when(() -> list("abc", "def"));
    thenReturned(contains("abc", "def"));
  }

  @Test
  public void list_with_three_elements() throws Exception {
    when(() -> list("abc", "def", "ghi"));
    thenReturned(contains("abc", "def", "ghi"));
  }

  @Test
  public void concat_to_empty() throws Exception {
    given(list = new ArrayList<>());
    when(concat(list, "element"));
    thenReturned(asList("element"));
  }

  @Test
  public void concat_to_non_empty() throws Exception {
    given(list = new ArrayList<>(asList("first")));
    when(concat(list, "second"));
    thenReturned(asList("first", "second"));
  }

  @Test
  public void concat_doesnt_modify_list() throws Exception {
    given(list = new ArrayList<>(asList("first")));
    when(concat(list, "second"));
    thenEqual(list, new ArrayList<>(asList("first")));
  }

  @Test
  public void filter_empty_returns_empty() throws Exception {
    given(list = asList());
    when(() -> filter(list, Predicates.alwaysTrue()));
    thenReturned(asList());
  }

  @Test
  public void filter_with_always_true_predicate_returns_same_list() throws Exception {
    given(list = asList("first", "second", "third"));
    when(() -> filter(list, Predicates.alwaysTrue()));
    thenReturned(asList("first", "second", "third"));
  }

  @Test
  public void filter_with_always_false_predicate_returns_empty_list() throws Exception {
    given(list = asList("first", "second", "third"));
    when(() -> filter(list, Predicates.alwaysFalse()));
    thenReturned(asList());
  }

  @Test
  public void filter_filters_elements() throws Exception {
    given(list = asList("first", "second", "third"));
    when(() -> filter(list, s -> s.startsWith("s")));
    thenReturned(asList("second"));
  }

  @Test
  public void mapping_empty_returns_empty() throws Exception {
    given(list = asList());
    when(() -> map(list, String::toUpperCase));
    thenReturned(asList());
  }

  @Test
  public void mapping_with_one_element() throws Exception {
    given(list = asList("abc"));
    when(() -> map(list, String::toUpperCase));
    thenReturned(asList("ABC"));
  }

  @Test
  public void mapping_with_two_elements() throws Exception {
    given(list = asList("abc", "def"));
    when(() -> map(list, String::toUpperCase));
    thenReturned(asList("ABC", "DEF"));
  }

  @Test
  public void sane_null_is_converted_to_empty_list() throws Exception {
    when(() -> sane(null));
    thenReturned(asList());
  }

  @Test
  public void sane_empty_list_is_just_returned() throws Exception {
    given(list = new ArrayList<>());
    when(() -> sane(list));
    thenReturned(same(list));
  }

  @Test
  public void sane_non_empty_list_is_just_returned() throws Exception {
    given(list = asList("abc", "def"));
    when(() -> sane(list));
    thenReturned(same(list));
  }
}
