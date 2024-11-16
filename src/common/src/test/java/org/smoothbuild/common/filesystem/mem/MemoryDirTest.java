package org.smoothbuild.common.filesystem.mem;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
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
  void name() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.name()).isEqualTo(path);
  }

  @Test
  void parent() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.parent()).isEqualTo(parent);
  }

  @Test
  void memory_dir_is_not_file() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.isFile()).isFalse();
  }

  @Test
  void memory_dir_is_file() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.isDir()).isTrue();
  }

  @Test
  void does_not_have_not_added_child() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.hasChild(child.name())).isFalse();
  }

  @Test
  void has_child_that_has_been_added_to_it() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    assertThat(memoryDir.hasChild(child.name())).isTrue();
  }

  @Test
  void returns_child_added_to_it_with_given_name() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    assertThat(memoryDir.child(child.name())).isSameInstanceAs(child);
  }

  @Test
  void cannot_add_the_same_child_twice() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    assertCall(() -> memoryDir.addChild(child)).throwsException(IllegalStateException.class);
  }

  @Test
  void child_names_is_empty_when_no_child_was_added() {
    memoryDir = new MemoryDir(parent, path);
    assertThat(memoryDir.childNames()).isEmpty();
  }

  @Test
  void child_names_returns_all_added_children() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    memoryDir.addChild(child2);
    assertThat(memoryDir.childNames()).containsExactly(child.name(), child2.name());
  }

  @Test
  void has_no_children_after_removing_all_children() {
    memoryDir = new MemoryDir(parent, path);
    memoryDir.addChild(child);
    memoryDir.addChild(child2);
    memoryDir.removeAllChildren();
    assertThat(memoryDir.childNames()).isEmpty();
  }

  @Test
  void source_throws_exception() {
    memoryDir = new MemoryDir(parent, path);
    assertCall(() -> memoryDir.source()).throwsException(IOException.class);
  }

  @Test
  void sink_throws_exception() {
    memoryDir = new MemoryDir(parent, path);
    assertCall(() -> memoryDir.sink()).throwsException(IOException.class);
  }

  private static MemoryElement createChild(Path name) {
    return new MemoryDir(null, name);
  }
}
