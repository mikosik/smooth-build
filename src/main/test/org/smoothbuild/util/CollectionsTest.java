package org.smoothbuild.util;

import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class CollectionsTest {
  private Collection<String> collection;

  @Test
  public void to_map() throws Exception {
    given(collection = list("abc", "defg", "hijkl"));
    when(() -> Collections.toMap(collection, String::length));
    thenReturned(ImmutableMap.of(3, "abc", 4, "defg", 5, "hijkl"));
  }

  @Test
  public void empty_collection_to_map() throws Exception {
    given(collection = list());
    when(() -> Collections.toMap(collection, String::length));
    thenReturned(ImmutableMap.of());
  }
}
