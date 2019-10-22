package org.smoothbuild.io.util;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.db.values.ValueCreators.array;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.TestingValueFactory;
import org.smoothbuild.lang.value.ValueFactory;
import org.smoothbuild.task.exec.Container;
import org.smoothbuild.task.exec.TestingContainer;
import org.testory.common.Matcher;

import okio.BufferedSink;
import okio.ByteString;

public class TempDirTest {
  private final Path path = path("my/path");
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private final Path rootPath = path("fake/path");

  private ValuesDb valuesDb;
  private TestingRuntimeTypes types;
  private ValueFactory valueFactory;
  private FileSystem fileSystem;
  private TempDir tempDir;
  private Array array;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    valuesDb = new ValuesDb(hashedDb);
    types = new TestingRuntimeTypes(valuesDb);
    valueFactory = new TestingValueFactory(types, valuesDb);
    fileSystem = new MemoryFileSystem();
    Container container = new TestingContainer(fileSystem, types, valuesDb);
    tempDir = new TempDir(container, fileSystem, rootPath);
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
    when(tempDir).writeFile(file(valueFactory, path, bytes));
    thenEqual(fileSystem.source(rootPath.append(path)).readByteString(), bytes);
  }

  @Test
  public void writing_file_after_destroy_throws_exception() throws Exception {
    given(tempDir).destroy();
    when(tempDir).writeFile(file(valueFactory, path, bytes));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void files_are_written_to_file_system() throws Exception {
    given(array = array(valuesDb, types.file(), file(valueFactory, path, bytes)));
    when(tempDir).writeFiles(array);
    thenEqual(fileSystem.source(rootPath.append(path)).readByteString(), bytes);
  }

  @Test
  public void writing_files_after_destroy_throws_exception() throws Exception {
    given(array = array(valuesDb, types.file(), file(valueFactory, path, bytes)));
    given(tempDir).destroy();
    when(tempDir).writeFiles(array);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void files_are_read_from_file_system() throws Exception {
    try (BufferedSink sink = fileSystem.sink(rootPath.append(path))) {
      sink.write(bytes);
    }
    when(() -> tempDir.readFiles().asIterable(Struct.class));
    thenReturned(contains(file(valueFactory, path, bytes)));
  }

  @Test
  public void reading_files_after_destroy_throws_exception() throws Exception {
    given(tempDir).destroy();
    when(tempDir).readFiles();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void content_is_read_from_file_system() throws Exception {
    try (BufferedSink sink = fileSystem.sink(rootPath.append(path))) {
      sink.write(bytes);
    }
    when(tempDir).readContent(path);
    thenReturned(blobContains(bytes));
  }

  @Test
  public void reading_content_after_destroy_throws_exception() throws Exception {
    try (BufferedSink sink = fileSystem.sink(path)) {
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
