package org.smoothbuild.common.filesystem.mem;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.truth.Truth;
import java.io.IOException;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smoothbuild.common.filesystem.base.Path;

public class MemoryFileTest {
  private final MemoryDir parent = Mockito.mock(MemoryDir.class);
  private final Path name = Path.path("some/path");
  private final Path otherName = Path.path("other/path");
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  void name() {
    var file = new MemoryFile(parent, name);
    Truth.assertThat(file.name()).isEqualTo(name);
  }

  @Test
  void parent() {
    var file = new MemoryFile(parent, name);
    assertThat(file.parent()).isSameInstanceAs(parent);
  }

  @Test
  void memory_file_is_file() {
    var file = new MemoryFile(parent, name);
    assertThat(file.isFile()).isTrue();
  }

  @Test
  void memory_file_is_not_dir() {
    var file = new MemoryFile(parent, name);
    assertThat(file.isDir()).isFalse();
  }

  @Test
  void does_not_have_any_children() {
    var file = new MemoryFile(parent, name);
    assertThat(file.hasChild(otherName)).isFalse();
  }

  @Test
  void accessing_children_causes_exception() {
    var file = new MemoryFile(parent, name);
    assertCall(() -> file.child(otherName)).throwsException(UnsupportedOperationException.class);
  }

  @Test
  void child_names_throws_exception() {
    var file = new MemoryFile(parent, name);
    assertCall(() -> file.childNames()).throwsException(UnsupportedOperationException.class);
  }

  @Test
  void add_child_throws_exception() {
    var file = new MemoryFile(parent, name);
    assertCall(() -> file.addChild(new MemoryDir(null, null)))
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  void opening_input_stream_for_non_existent_file_fails() {
    var file = new MemoryFile(parent, name);
    assertCall(() -> file.source()).throwsException(IOException.class);
  }

  @Test
  void data_written_to_memory_file_can_be_read_back() throws Exception {
    var file = new MemoryFile(parent, name);
    try (var sink = buffer(file.sink())) {
      sink.write(bytes);
    }
    try (var source = buffer(file.source())) {
      assertThat(source.readByteString()).isEqualTo(bytes);
    }
  }

  // test reproducing a bug found in MemoryFile
  @Test
  void closing_sink_twice_not_corrupts_stored_data() throws Exception {
    var file = new MemoryFile(parent, name);
    try (var sink = file.sink()) {
      try (var buffered = buffer(sink)) {
        buffered.write(bytes);
      }
    }

    try (var source = buffer(file.source())) {
      assertThat(source.readByteString()).isEqualTo(bytes);
    }
  }
}
