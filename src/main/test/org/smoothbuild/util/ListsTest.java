package org.smoothbuild.util;

import static java.util.Arrays.asList;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ListsTest {
  private List<String> list;

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
