package org.smoothbuild.io.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;

import okio.BufferedSink;
import okio.ByteString;

public class TempDirTest extends TestingContext {
  private final Path path = path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private final Path rootPath = path("fake/path");

  private TempDir tempDir;
  private Array array;

  @BeforeEach
  public void before() {
    tempDir = new TempDir(container(), fullFileSystem(), rootPath);
  }

  @Test
  public void destroying_twice_is_allowed() throws Exception {
    tempDir.destroy();
    tempDir.destroy();
  }

  @Test
  public void root_os_path() {
    assertThat(tempDir.rootOsPath())
        .isEqualTo("in-memory/" + rootPath.toString());
  }

  @Test
  public void file_is_written_to_file_system() throws Exception {
    tempDir.writeFile(file(path, bytes));
    assertThat(fullFileSystem().source(rootPath.append(path)).readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void writing_file_after_destroy_throws_exception() throws Exception {
    tempDir.destroy();
    assertCall(() -> tempDir.writeFile(file(path, bytes)))
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void files_are_written_to_file_system() throws Exception {
    array = array(file(path, bytes));
    tempDir.writeFiles(array);
    assertThat(fullFileSystem().source(rootPath.append(path)).readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void writing_files_after_destroy_throws_exception() throws Exception {
    array = array(file(path, bytes));
    tempDir.destroy();
    assertCall(() -> tempDir.writeFiles(array))
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void files_are_read_from_file_system() throws Exception {
    try (BufferedSink sink = fullFileSystem().sink(rootPath.append(path))) {
      sink.write(bytes);
    }
    assertThat(tempDir.readFiles().asIterable(Struct.class))
        .containsExactly(file(path, bytes));
  }

  @Test
  public void reading_files_after_destroy_throws_exception() throws Exception {
    tempDir.destroy();
    assertCall(() -> tempDir.readFiles())
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void content_is_read_from_file_system() throws Exception {
    try (BufferedSink sink = fullFileSystem().sink(rootPath.append(path))) {
      sink.write(bytes);
    }
    assertThat(blobToByteString(tempDir.readContent(path)))
        .isEqualTo(bytes);
  }

  @Test
  public void reading_content_after_destroy_throws_exception() throws Exception {
    try (BufferedSink sink = fullFileSystem().sink(path)) {
      sink.write(bytes);
    }
    tempDir.destroy();
    assertCall(() -> tempDir.readContent(path))
        .throwsException(IllegalStateException.class);
  }

  private static ByteString blobToByteString(Blob object) {
    try {
      return object.source().readByteString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
