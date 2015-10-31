package org.smoothbuild.io.util;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
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

public class TempDirectoryTest {
  private final Path path = path("my/path");
  private final String content = "content";
  private final java.nio.file.Path rootPath = Paths.get("/fake/path");

  private ValuesDb valuesDb;
  private FileSystem fileSystem;
  private TempDirectory tempDirectory;
  private Array<SFile> array;

  @Before
  public void before() {
    valuesDb = valuesDb();
    fileSystem = new MemoryFileSystem();
    tempDirectory = new TempDirectory(valuesDb, rootPath, fileSystem);
  }

  @After
  public void after() {
    try {
      tempDirectory.destroy();
    } catch (IllegalStateException e) {
      // ignore exception as tempDirectory might have been already destroyed by
      // test and destroying it second time causes exception
    }
  }

  @Test
  public void root_os_path() {
    when(tempDirectory.rootOsPath());
    thenReturned(rootPath.toString());
  }

  @Test
  public void file_is_written_to_file_system() throws Exception {
    when(tempDirectory).writeFile(file(valuesDb, path, content));
    then(inputStreamToString(fileSystem.openInputStream(path)).equals(content));
  }

  @Test
  public void writing_file_after_destroy_throws_exception() throws Exception {
    given(tempDirectory).destroy();
    when(tempDirectory).writeFile(file(valuesDb, path, content));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void path_and_content_are_written_to_file_system() throws Exception {
    when(tempDirectory).writeFile(path, blob(valuesDb, content));
    then(inputStreamToString(fileSystem.openInputStream(path)).equals(content));
  }

  @Test
  public void writing_content_after_destroy_throws_exception() throws Exception {
    given(tempDirectory).destroy();
    when(tempDirectory).writeFile(path, blob(valuesDb, content));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void files_are_written_to_file_system() throws Exception {
    given(array = array(valuesDb, SFile.class, file(valuesDb, path, content)));
    when(tempDirectory).writeFiles(array);
    then(inputStreamToString(fileSystem.openInputStream(path)).equals(content));
  }

  @Test
  public void writing_files_after_destroy_throws_exception() throws Exception {
    given(array = array(valuesDb, SFile.class, file(valuesDb, path, content)));
    given(tempDirectory).destroy();
    when(tempDirectory).writeFiles(array);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void files_are_read_from_file_system() throws Exception {
    given(createFile(fileSystem, path, content));
    when(tempDirectory.readFiles());
    thenReturned(contains(file(valuesDb, path, content)));
  }

  @Test
  public void reading_files_after_destroy_throws_exception() throws Exception {
    given(tempDirectory).destroy();
    when(tempDirectory).readFiles();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void content_is_read_from_file_system() throws Exception {
    given(createFile(fileSystem, path, content));
    when(tempDirectory).readContent(path);
    thenReturned(blobContains(content));
  }

  @Test
  public void reading_content_after_destroy_throws_exception() throws Exception {
    given(createFile(fileSystem, path, content));
    given(tempDirectory).destroy();
    when(tempDirectory).readContent(path);
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
