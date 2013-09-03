package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.Test;

public class StringSetImplTest {
  StringSetImpl stringSetImpl = new StringSetImpl();

  @Test
  public void doesNotContainRandomString() {
    assertThat(stringSetImpl.contains("abc")).isFalse();
  }

  @Test
  public void initiallyIteratorIsEmpty() throws Exception {
    assertThat(stringSetImpl.iterator()).isEmpty();
  }

  @Test
  public void iteratorIsNotEmptyAfterAddingString() throws Exception {
    stringSetImpl.add("abc");
    assertThat(stringSetImpl.iterator()).isNotEmpty();
  }

  @Test
  public void containsAddedElement() throws Exception {
    String string = "abc";
    stringSetImpl.add(string);
    assertThat(stringSetImpl.contains(string)).isTrue();
  }

  @Test
  public void iteratorContainsAddedElements() throws Exception {
    String string = "abc";
    String string2 = "abc";
    stringSetImpl.add(string);
    stringSetImpl.add(string2);

    assertThat(stringSetImpl.iterator()).containsOnly(string, string2);
  }

  @Test
  public void iteratorCanRemoveElements() throws Exception {
    String string = "abc";
    stringSetImpl.add(string);

    Iterator<String> iterator = stringSetImpl.iterator();
    iterator.next();
    iterator.remove();

    assertThat(stringSetImpl.contains(string)).isFalse();
  }

}
