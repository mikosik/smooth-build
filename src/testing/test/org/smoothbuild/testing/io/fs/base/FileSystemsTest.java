package org.smoothbuild.testing.io.fs.base;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.io.fs.base.FileSystems.createFile;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

public class FileSystemsTest {
  private MemoryFileSystem fileSystem;
  private Path path;

  @Test
  public void create_file_returns_path() throws Exception {
    given(fileSystem = new MemoryFileSystem());
    given(path = path("some/path"));
    when(createFile(fileSystem, path, "content"));
    thenReturned(path);
  }

  @Test
  public void create_file_creates_file() throws Exception {
    given(fileSystem = new MemoryFileSystem());
    given(path = path("some/path"));
    when(createFile(fileSystem, path, "content"));
    thenEqual(inputStreamToString(fileSystem.openInputStream(path)), "content");
  }
}
