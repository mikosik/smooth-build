package org.smoothbuild.io.fs.mem;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.base.PathS;

import okio.BufferedSink;
import okio.ByteString;

public class MemoryFileTest {
  private final MemoryDir parent = mock(MemoryDir.class);
  private final PathS name = PathS.path("some/path");
  private final PathS otherName = PathS.path("other/path");
  private final ByteString bytes = ByteString.encodeUtf8("aaa");
  private MemoryFile file;
  private BufferedSink sink;

  @Test
  public void name() {
    file = new MemoryFile(parent, name);
    assertThat(file.name())
        .isEqualTo(name);
  }

  @Test
  public void parent() {
    file = new MemoryFile(parent, name);
    assertThat(file.parent())
        .isSameInstanceAs(parent);
  }

  @Test
  public void memory_file_is_file() {
    file = new MemoryFile(parent, name);
    assertThat(file.isFile())
        .isTrue();
  }

  @Test
  public void memory_file_is_not_dir() {
    file = new MemoryFile(parent, name);
    assertThat(file.isDir())
        .isFalse();
  }

  @Test
  public void does_not_have_any_children() {
    file = new MemoryFile(parent, name);
    assertThat(file.hasChild(otherName))
        .isFalse();
  }

  @Test
  public void accessing_children_causes_exception() {
    file = new MemoryFile(parent, name);
    assertCall(() -> file.child(otherName))
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void child_names_throws_exception() {
    file = new MemoryFile(parent, name);
    assertCall(() -> file.childNames())
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void add_child_throws_exception() {
    file = new MemoryFile(parent, name);
    assertCall(() -> file.addChild(mock(MemoryElement.class)))
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void opening_input_stream_for_non_existent_file_fails() {
    file = new MemoryFile(parent, name);
    assertCall(() -> file.source())
        .throwsException(IOException.class);
  }

  @Test
  public void data_written_to_memory_file_can_be_read_back() throws Exception {
    file = new MemoryFile(parent, name);
    sink = file.sink();
    sink.write(bytes);
    sink.close();
    assertThat(file.source().readByteString())
        .isEqualTo(bytes);
  }
}
