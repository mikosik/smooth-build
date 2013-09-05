package org.smoothbuild.plugin.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.Test;

public class MutableStringSetTest {
  MutableStringSet mutableStringSet = new MutableStringSet();

  @Test
  public void doesNotContainRandomString() {
    assertThat(mutableStringSet.contains("abc")).isFalse();
  }

  @Test
  public void initiallyIteratorIsEmpty() throws Exception {
    assertThat(mutableStringSet.iterator()).isEmpty();
  }

  @Test
  public void iteratorIsNotEmptyAfterAddingString() throws Exception {
    mutableStringSet.add("abc");
    assertThat(mutableStringSet.iterator()).isNotEmpty();
  }

  @Test
  public void containsAddedElement() throws Exception {
    String string = "abc";
    mutableStringSet.add(string);
    assertThat(mutableStringSet.contains(string)).isTrue();
  }

  @Test
  public void iteratorContainsAddedElements() throws Exception {
    String string = "abc";
    String string2 = "abc";
    mutableStringSet.add(string);
    mutableStringSet.add(string2);

    assertThat(mutableStringSet.iterator()).containsOnly(string, string2);
  }

  @Test
  public void iteratorCanRemoveElements() throws Exception {
    String string = "abc";
    mutableStringSet.add(string);

    Iterator<String> iterator = mutableStringSet.iterator();
    iterator.next();
    iterator.remove();

    assertThat(mutableStringSet.contains(string)).isFalse();
  }

}
