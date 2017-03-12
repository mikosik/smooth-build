package org.smoothbuild.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ListsTest {
  private List<String> list;

  @Test
  public void concat_to_empty() throws Exception {
    given(list = new ArrayList<>());
    when(Lists.concat(list, "element"));
    thenReturned(Arrays.asList("element"));
  }

  @Test
  public void concat() throws Exception {
    given(list = new ArrayList<>(Arrays.asList("first")));
    when(Lists.concat(list, "second"));
    thenReturned(Arrays.asList("first", "second"));
  }

  @Test
  public void concat_doesnt_modify_list() throws Exception {
    given(list = new ArrayList<>(Arrays.asList("first")));
    when(Lists.concat(list, "second"));
    thenEqual(list, new ArrayList<>(Arrays.asList("first")));
  }
}
