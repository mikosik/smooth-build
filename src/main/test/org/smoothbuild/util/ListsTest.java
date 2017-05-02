package org.smoothbuild.util;

import static java.util.Arrays.asList;
import static org.smoothbuild.util.Lists.concat;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

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
}
