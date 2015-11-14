package org.smoothbuild.io.fs.mem;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;

public class MemoryDirTest {
  private final MemoryElement child = createChild(path("childName1"));
  private final MemoryElement child2 = createChild(path("childName2"));
  private final Path path = path("name");
  private final MemoryDir parent = mock(MemoryDir.class);
  private MemoryDir memoryDir;

  @Test
  public void name() {
    given(memoryDir = new MemoryDir(parent, path));
    when(memoryDir).name();
    thenReturned(path);
  }

  @Test
  public void parent() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    when(memoryDir).parent();
    thenReturned(parent);
  }

  @Test
  public void memory_dir_is_not_file() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    when(memoryDir).isFile();
    thenReturned(false);
  }

  @Test
  public void memory_dir_is_file() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    when(memoryDir).isDir();
    thenReturned(true);
  }

  @Test
  public void does_not_have_not_added_child() {
    given(memoryDir = new MemoryDir(parent, path));
    when(memoryDir).hasChild(child.name());
    thenReturned(false);
  }

  @Test
  public void has_child_that_has_been_added_to_it() {
    given(memoryDir = new MemoryDir(parent, path));
    given(memoryDir).addChild(child);
    when(memoryDir).hasChild(child.name());
    thenReturned(true);
  }

  @Test
  public void returns_child_added_to_it_with_given_name() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    given(memoryDir).addChild(child);
    when(memoryDir).child(child.name());
    thenReturned(same(child));
  }

  @Test
  public void cannot_add_the_same_child_twice() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    given(memoryDir).addChild(child);
    when(memoryDir).addChild(child);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void child_names_is_empty_when_no_child_was_added() {
    given(memoryDir = new MemoryDir(parent, path));
    when(memoryDir).childNames();
    thenReturned(empty());
  }

  @Test
  public void child_names_returns_all_added_children() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    given(memoryDir).addChild(child);
    given(memoryDir).addChild(child2);
    when(memoryDir).childNames();
    thenReturned(containsInAnyOrder(child.name(), child2.name()));
  }

  @Test
  public void has_no_children_after_removing_all_children() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    given(memoryDir).addChild(child);
    given(memoryDir).addChild(child2);
    when(memoryDir).removeAllChildren();
    then(memoryDir.childNames(), empty());
  }

  @Test
  public void create_input_stream_throws_exception() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    when(memoryDir).openInputStream();
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void createOutputStreamThrowsException() throws Exception {
    given(memoryDir = new MemoryDir(parent, path));
    when(memoryDir).openOutputStream();
    thenThrown(UnsupportedOperationException.class);
  }

  private static MemoryElement createChild(Path name) {
    MemoryElement childMock = mock(MemoryElement.class);
    given(willReturn(name), childMock).name();
    return childMock;
  }
}
