package org.smoothbuild.io.util;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.db.values.ValueCreators.array;
import static org.smoothbuild.testing.db.values.ValueCreators.blob;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.smoothbuild.testing.io.fs.base.FileSystems.createFile;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.testory.common.Matcher;

public class TempDirTest {
  private final Path path = path("my/path");
  private final String content = "content";
  private final java.nio.file.Path rootPath = Paths.get("/fake/path");

  private ValuesDb valuesDb;
  private FileSystem fileSystem;
  private TempDir tempDir;
  private Array<SFile> array;

  @Before
  public void before() {
    valuesDb = memoryValuesDb();
    fileSystem = new MemoryFileSystem();
    tempDir = new TempDir(valuesDb, rootPath, fileSystem);
  }

  @After
  public void after() {
    try {
      tempDir.destroy();
    } catch (IllegalStateException e) {
      // ignore exception as tempDir might have been already destroyed by
      // test and destroying it second time causes exception
    }
  }

  @Test
  public void root_os_path() {
    when(tempDir.rootOsPath());
    thenReturned(rootPath.toString());
  }

  @Test
  public void file_is_written_to_file_system() throws Exception {
    when(tempDir).writeFile(file(valuesDb, path, content));
    then(inputStreamToString(fileSystem.openInputStream(path)).equals(content));
  }

  @Test
  public void writing_file_after_destroy_throws_exception() throws Exception {
    given(tempDir).destroy();
    when(tempDir).writeFile(file(valuesDb, path, content));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void path_and_content_are_written_to_file_system() throws Exception {
    when(tempDir).writeFile(path, blob(valuesDb, content));
    then(inputStreamToString(fileSystem.openInputStream(path)).equals(content));
  }

  @Test
  public void writing_content_after_destroy_throws_exception() throws Exception {
    given(tempDir).destroy();
    when(tempDir).writeFile(path, blob(valuesDb, content));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void files_are_written_to_file_system() throws Exception {
    given(array = array(valuesDb, SFile.class, file(valuesDb, path, content)));
    when(tempDir).writeFiles(array);
    then(inputStreamToString(fileSystem.openInputStream(path)).equals(content));
  }

  @Test
  public void writing_files_after_destroy_throws_exception() throws Exception {
    given(array = array(valuesDb, SFile.class, file(valuesDb, path, content)));
    given(tempDir).destroy();
    when(tempDir).writeFiles(array);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void files_are_read_from_file_system() throws Exception {
    given(createFile(fileSystem, path, content));
    when(tempDir.readFiles());
    thenReturned(contains(file(valuesDb, path, content)));
  }

  @Test
  public void reading_files_after_destroy_throws_exception() throws Exception {
    given(tempDir).destroy();
    when(tempDir).readFiles();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void content_is_read_from_file_system() throws Exception {
    given(createFile(fileSystem, path, content));
    when(tempDir).readContent(path);
    thenReturned(blobContains(content));
  }

  @Test
  public void reading_content_after_destroy_throws_exception() throws Exception {
    given(createFile(fileSystem, path, content));
    given(tempDir).destroy();
    when(tempDir).readContent(path);
    thenThrown(IllegalStateException.class);
  }

  private static Matcher blobContains(final String expected) {
    return new Matcher() {
      @Override
      public boolean matches(Object object) {
        try {
          return inputStreamToString(((Blob) object).openInputStream()).equals(expected);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }
}
