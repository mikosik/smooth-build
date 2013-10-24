package org.smoothbuild.type.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;

import com.google.common.collect.Lists;

public class ImmutableStringSetTest {

  @Test
  public void iteratorIsEmptyForEmptySet() throws Exception {
    assertThat(create().iterator()).isEmpty();
  }

  @Test
  public void iteratorIsNotEmptyAfterAddingString() throws Exception {
    assertThat(create("abc")).isNotEmpty();
  }

  @Test
  public void iteratorContainsAddedElements() throws Exception {
    String string = "abc";
    String string2 = "abc";
    ImmutableStringSet set = create(string, string2);

    assertThat(convert(set)).containsOnly(string, string2);
  }

  private static List<String> convert(StringSet stringSet) {
    ArrayList<String> result = Lists.newArrayList();
    for (StringValue stringValue : stringSet) {
      result.add(stringValue.value());
    }
    return result;
  }

  @Test
  public void iteratorCannotRemoveElements() throws Exception {
    String string = "abc";

    Iterator<StringValue> iterator = create(string).iterator();
    iterator.next();
    try {
      iterator.remove();
      fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  private static ImmutableStringSet create(String... strings) {
    return new ImmutableStringSet(Arrays.asList(strings));
  }
}
