package org.smoothbuild.util;

import static org.smoothbuild.util.Sets.map;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

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

  private static <T> Set<T> set(T... elements) {
    return com.google.common.collect.Sets.newHashSet(elements);
  }
}
