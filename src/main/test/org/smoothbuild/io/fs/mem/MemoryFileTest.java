package org.smoothbuild.io.fs.mem;

import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.OutputStreamWriter;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemException;

public class MemoryFileTest {
  private final MemoryDirectory parent = mock(MemoryDirectory.class);
  private final Path name = Path.path("some/path");
  private final Path otherName = Path.path("other/path");
  private final String line = "some string";
  private MemoryFile file;
  private OutputStreamWriter writer;

  @Test
  public void name() {
    given(file = new MemoryFile(parent, name));
    when(file.name());
    thenReturned(name);
  }

  @Test
  public void parent() throws Exception {
    given(file = new MemoryFile(parent, name));
    when(file.parent());
    thenReturned(sameInstance(parent));
  }

  @Test
  public void memory_file_is_file() throws Exception {
    given(file = new MemoryFile(parent, name));
    when(file).isFile();
    thenReturned(true);
  }

  @Test
  public void memory_file_is_not_directory() throws Exception {
    given(file = new MemoryFile(parent, name));
    when(file).isDirectory();
    thenReturned(false);
  }

  @Test
  public void does_not_have_any_children() {
    given(file = new MemoryFile(parent, name));
    when(file).hasChild(otherName);
    thenReturned(false);
  }

  @Test
  public void accessing_children_causes_exception() {
    given(file = new MemoryFile(parent, name));
    when(file).child(otherName);
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void child_names_throws_exception() {
    given(file = new MemoryFile(parent, name));
    when(file).childNames();
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void add_child_throws_exception() {
    given(file = new MemoryFile(parent, name));
    when(file).addChild(mock(MemoryElement.class));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void opening_input_stream_for_non_existent_file_fails() throws Exception {
    given(file = new MemoryFile(parent, name));
    when(file).openInputStream();
    thenThrown(FileSystemException.class);
  }

  @Test
  public void data_written_to_memory_file_can_be_read_back() throws Exception {
    given(file = new MemoryFile(parent, name));
    given(writer = new OutputStreamWriter(file.openOutputStream()));
    given(writer).write(line);
    given(writer).close();
    thenEqual(line, inputStreamToString(file.openInputStream()));
  }
}
