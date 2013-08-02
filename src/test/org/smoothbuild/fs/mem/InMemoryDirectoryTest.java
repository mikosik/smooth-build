package org.smoothbuild.fs.mem;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class InMemoryDirectoryTest {
  private static final String CHILD_NAME = "childName";

  InMemoryElement child = createChild();
  InMemoryDirectory dir = new InMemoryDirectory("name");

  @Test
  public void name() {
    assertThat(dir.name()).isEqualTo("name");
  }

  @Test
  public void isFile() throws Exception {
    assertThat(dir.isFile()).isFalse();
  }

  @Test
  public void isDirectory() throws Exception {
    assertThat(dir.isDirectory()).isTrue();
  }

  @Test
  public void doesNotHaveChildThatIsNotAdded() {
    assertThat(dir.hasChild(CHILD_NAME)).isFalse();
  }

  @Test
  public void hasChildAfterAdding() {
    dir.addChild(child);
    assertThat(dir.hasChild(CHILD_NAME)).isTrue();
  }

  @Test
  public void addingAndRetrievingChild() throws Exception {
    dir.addChild(child);
    assertThat(dir.child(CHILD_NAME)).isSameAs(child);
  }

  @Test
  public void cannotAddTheSameChildTwice() throws Exception {
    dir.addChild(child);
    try {
      dir.addChild(child);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void childNamesAreInitiallyEmpty() {
    assertThat(dir.childNames()).isEmpty();
  }

  @Test
  public void childNamesReturnsAddedChildren() throws Exception {
    dir.addChild(child);
    assertThat(dir.childNames()).isEqualTo(ImmutableList.of(CHILD_NAME));
  }

  @Test
  public void createInputStreamThrowsException() throws Exception {
    try {
      dir.createInputStream();
      fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void createOutputStreamThrowsException() throws Exception {
    try {
      dir.createOutputStream();
      fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  private static InMemoryElement createChild() {
    InMemoryElement childMock = mock(InMemoryElement.class);
    when(childMock.name()).thenReturn(CHILD_NAME);
    return childMock;
  }

}
