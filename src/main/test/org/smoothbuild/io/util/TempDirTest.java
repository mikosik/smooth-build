package org.smoothbuild.io.util;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.testing.TestingContext;
import org.testory.common.Matcher;

import okio.BufferedSink;
import okio.ByteString;

public class TempDirTest extends TestingContext {
  private final Path path = path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private final Path rootPath = path("fake/path");

  private TempDir tempDir;
  private Array array;

  @Before
  public void before() {
    tempDir = new TempDir(container(), fullFileSystem(), rootPath);
  }

  @Test
  public void destroying_twice_is_allowed() throws Exception {
    given(tempDir).destroy();
    when(tempDir).destroy();
    thenReturned();
  }

  @Test
  public void root_os_path() {
    when(tempDir.rootOsPath());
    thenReturned(rootPath.value());
  }

  @Test
  public void file_is_written_to_file_system() throws Exception {
    when(tempDir).writeFile(file(path, bytes));
    thenEqual(fullFileSystem().source(rootPath.append(path)).readByteString(), bytes);
  }

  @Test
  public void writing_file_after_destroy_throws_exception() throws Exception {
    given(tempDir).destroy();
    when(tempDir).writeFile(file(path, bytes));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void files_are_written_to_file_system() throws Exception {
    given(array = array(file(path, bytes)));
    when(tempDir).writeFiles(array);
    thenEqual(fullFileSystem().source(rootPath.append(path)).readByteString(), bytes);
  }

  @Test
  public void writing_files_after_destroy_throws_exception() throws Exception {
    given(array = array(file(path, bytes)));
    given(tempDir).destroy();
    when(tempDir).writeFiles(array);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void files_are_read_from_file_system() throws Exception {
    try (BufferedSink sink = fullFileSystem().sink(rootPath.append(path))) {
      sink.write(bytes);
    }
    when(() -> tempDir.readFiles().asIterable(Struct.class));
    thenReturned(contains(file(path, bytes)));
  }

  @Test
  public void reading_files_after_destroy_throws_exception() throws Exception {
    given(tempDir).destroy();
    when(tempDir).readFiles();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void content_is_read_from_file_system() throws Exception {
    try (BufferedSink sink = fullFileSystem().sink(rootPath.append(path))) {
      sink.write(bytes);
    }
    when(tempDir).readContent(path);
    thenReturned(blobContains(bytes));
  }

  @Test
  public void reading_content_after_destroy_throws_exception() throws Exception {
    try (BufferedSink sink = fullFileSystem().sink(path)) {
      sink.write(bytes);
    }
    given(tempDir).destroy();
    when(tempDir).readContent(path);
    thenThrown(IllegalStateException.class);
  }

  private static Matcher blobContains(ByteString expected) {
    return (object) -> {
      try {
        return ((Blob) object).source().readByteString().equals(expected);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }
}
