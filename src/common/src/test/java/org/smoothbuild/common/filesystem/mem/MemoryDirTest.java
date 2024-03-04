package org.smoothbuild.common.filesystem.mem;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smoothbuild.common.filesystem.base.Path;

public class MemoryDirTest {
  private final MemoryElement child = createChild(Path.path("childName1"));
  private final MemoryElement child2 = createChild(Path.path("childName2"));
  private final Path path = Path.path("name");
  private final MemoryDir parent = Mockito.mock(MemoryDir.class);
  private MemoryDir memoryDir;

  @Test
  public void name() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.name()).isEqualTo(path);
  }

  @Test
  public void parent() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.parent()).isEqualTo(parent);
  }

  @Test
  public void memory_dir_is_not_file() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.isFile()).isFalse();
  }

  @Test
  public void memory_dir_is_file() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.isDir()).isTrue();
  }

  @Test
  public void does_not_have_not_added_child() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.hasChild(child.name())).isFalse();
  }

  @Test
  public void has_child_that_has_been_added_to_it() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    assertThat(memoryDir.hasChild(child.name())).isTrue();
  }

  @Test
  public void returns_child_added_to_it_with_given_name() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    assertThat(memoryDir.child(child.name())).isSameInstanceAs(child);
  }

  @Test
  public void cannot_add_the_same_child_twice() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    assertCall(() -> memoryDir.addChild(child)).throwsException(IllegalStateException.class);
  }

  @Test
  public void child_names_is_empty_when_no_child_was_added() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.childNames()).isEmpty();
  }

  @Test
  public void child_names_returns_all_added_children() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    memoryDir.addChild(child2);
    assertThat(memoryDir.childNames()).containsExactly(child.name(), child2.name());
  }

  @Test
  public void has_no_children_after_removing_all_children() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    memoryDir.addChild(child2);
    memoryDir.removeAllChildren();
    assertThat(memoryDir.childNames()).isEmpty();
  }

  @Test
  public void create_input_stream_throws_exception() {
    memoryDir = new MemoryDir(parent, path);
    assertCall(() -> memoryDir.source()).throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void createOutputStreamThrowsException() {
    memoryDir = new MemoryDir(parent, path);
    assertCall(() -> memoryDir.sink()).throwsException(UnsupportedOperationException.class);
  }

  private static MemoryElement createChild(Path name) {
    MemoryElement childMock = Mockito.mock(MemoryElement.class);
    when(childMock.name()).thenReturn(name);
    return childMock;
  }
}
