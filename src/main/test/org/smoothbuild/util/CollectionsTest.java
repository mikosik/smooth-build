package org.smoothbuild.util;

import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CollectionsTest {
  private Collection<String> collection;

  @Test
  public void to_map_with_key_function() {
    given(collection = list("abc", "defg", "hijkl"));
    when(() -> Collections.toMap(collection, String::length));
    thenReturned(Map.of(3, "abc", 4, "defg", 5, "hijkl"));
  }

  @Test
  public void empty_collection_to_map_with_key_function() {
    given(collection = list());
    when(() -> Collections.toMap(collection, String::length));
    thenReturned(Map.of());
  }

  @Test
  public void to_map_with_key_and_value_function() {
    given(collection = list("abc", "defg", "hijkl"));
    when(() -> Collections.toMap(collection, String::toUpperCase, String::length));
    thenReturned(Map.of("ABC", 3, "DEFG", 4, "HIJKL", 5));
  }

  @Test
  public void to_map_with_key_and_value_function_allows_null_values() {
    given(collection = list("abc", "defg", "hijkl"));
    when(() -> Collections.toMap(collection, String::toUpperCase, (x) -> null));
    thenReturned(expectedMapWithNulls());
  }

  private static HashMap<String, Object> expectedMapWithNulls() {
    HashMap<String, Object> expected = new HashMap<>();
    expected.put("ABC", null);
    expected.put("DEFG", null);
    expected.put("HIJKL", null);
    return expected;
  }

  @Test
  public void empty_collection_to_map_with_key_and_value_function() {
    given(collection = list());
    when(() -> Collections.toMap(collection, String::toUpperCase, String::length));
    thenReturned(Map.of());
  }
}
