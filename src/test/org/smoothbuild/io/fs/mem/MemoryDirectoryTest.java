package org.smoothbuild.io.fs.mem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class MemoryDirectoryTest {
  private static final String NAME1 = "childName1";
  private static final String NAME2 = "childName2";

  MemoryElement child = createChild(NAME1);
  MemoryElement child2 = createChild(NAME2);
  MemoryDirectory parent = mock(MemoryDirectory.class);
  MemoryDirectory dir = new MemoryDirectory(parent, "name");

  @Test
  public void name() {
    assertThat(dir.name()).isEqualTo("name");
  }

  @Test
  public void parent() throws Exception {
    assertThat(dir.parent()).isSameAs(parent);
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
    assertThat(dir.hasChild(NAME1)).isFalse();
  }

  @Test
  public void hasChildAfterAdding() {
    dir.addChild(child);
    assertThat(dir.hasChild(NAME1)).isTrue();
  }

  @Test
  public void addingAndRetrievingChild() throws Exception {
    dir.addChild(child);
    assertThat(dir.child(NAME1)).isSameAs(child);
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
    assertThat(dir.childNames()).isEqualTo(ImmutableList.of(NAME1));
  }

  @Test
  public void removeAllChildren() throws Exception {
    dir.addChild(child);
    dir.addChild(child2);
    dir.removeAllChildren();
    assertThat(dir.childNames()).isEmpty();
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

  private static MemoryElement createChild(String name) {
    MemoryElement childMock = mock(MemoryElement.class);
    when(childMock.name()).thenReturn(name);
    return childMock;
  }
}
