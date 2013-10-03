package org.smoothbuild.type.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.smoothbuild.type.impl.ImmutableStringSet;

public class ImmutableStringSetTest {

  @Test
  public void doesNotContainRandomString() {
    assertThat(create().contains("abc")).isFalse();
  }

  @Test
  public void iteratorIsEmptyForEmptySet() throws Exception {
    assertThat(create().iterator()).isEmpty();
  }

  @Test
  public void iteratorIsNotEmptyAfterAddingString() throws Exception {
    assertThat(create("abc")).isNotEmpty();
  }

  @Test
  public void containsElementPassedToConstructor() throws Exception {
    String string = "abc";
    assertThat(create(string).contains(string)).isTrue();
  }

  @Test
  public void iteratorContainsAddedElements() throws Exception {
    String string = "abc";
    String string2 = "abc";
    ImmutableStringSet set = create(string, string2);

    assertThat(set.iterator()).containsOnly(string, string2);
  }

  @Test
  public void iteratorCannotRemoveElements() throws Exception {
    String string = "abc";

    Iterator<String> iterator = create(string).iterator();
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
