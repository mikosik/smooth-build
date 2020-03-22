package org.smoothbuild.io.fs.base;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsDir;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsFile;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class AssertPathTest {
  private final Path path = Path.path("some/path");

  @Test
  public void assert_path_is_dir_returns_normally_for_dir_path() throws Exception {
    FileSystem fileSystem = fileSystemWith(path, DIR);
    assertPathIsDir(fileSystem, path);
  }

  @Test
  public void assert_path_is_dir_throws_exception_for_file_path() {
    FileSystem fileSystem = fileSystemWith(path, FILE);
    assertCall(() -> assertPathIsDir(fileSystem, path))
        .throwsException(new IOException("Dir " + path + " doesn't exist. It is a file."));
  }

  @Test
  public void assert_path_is_dir_throws_exception_when_path_does_not_exist() {
    FileSystem fileSystem = fileSystemWith(path, NOTHING);
    assertCall(() -> assertPathIsDir(fileSystem, path))
        .throwsException(new IOException("Dir " + path + " doesn't exist."));
  }

  @Test
  public void assert_path_is_file_returns_normally_for_file_path() throws Exception {
    FileSystem fileSystem = fileSystemWith(path, FILE);
    assertPathIsFile(fileSystem, path);
  }

  @Test
  public void assert_path_is_file_throws_exception_for_dir_path() {
    FileSystem fileSystem = fileSystemWith(path, DIR);
    assertCall(() -> assertPathIsFile(fileSystem, path))
        .throwsException(new IOException("File " + path + " doesn't exist. It is a dir."));
  }

  @Test
  public void assert_path_is_file_throws_exception_when_path_does_not_exist() {
    FileSystem fileSystem = fileSystemWith(path, NOTHING);
    assertCall(() -> assertPathIsFile(fileSystem, path))
        .throwsException(new IOException("File " + path + " doesn't exist."));
  }

  @Test
  public void assert_path_exists_returns_normally_for_file_path() throws Exception {
    FileSystem fileSystem = fileSystemWith(path, FILE);
    assertPathExists(fileSystem, path);
  }

  @Test
  public void assert_path_exists_returns_normally_for_dir_path() throws Exception {
    FileSystem fileSystem = fileSystemWith(path, DIR);
    assertPathExists(fileSystem, path);
  }

  @Test
  public void assert_path_exists_throws_exception_when_path_does_not_exist() {
    FileSystem fileSystem = fileSystemWith(path, NOTHING);
    assertCall(() -> assertPathExists(fileSystem, path))
        .throwsException(new IOException("Path " + path + " doesn't exist."));
  }

  @Test
  public void assert_path_is_unused_throws_exception_for_file_path() {
    FileSystem fileSystem = fileSystemWith(path, FILE);
    assertCall(() -> assertPathIsUnused(fileSystem, path))
        .throwsException(new IOException("Cannot use " + path + " path. It is already taken."));
  }

  @Test
  public void assert_path_is_unused_throws_exception_for_dir_path() {
    FileSystem fileSystem = fileSystemWith(path, DIR);
    assertCall(() -> assertPathIsUnused(fileSystem, path))
        .throwsException(new IOException("Cannot use " + path + " path. It is already taken."));
  }

  @Test
  public void assert_path_is_unused_returns_normally_when_path_does_not_exist() throws Exception {
    FileSystem fileSystem = fileSystemWith(path, NOTHING);
    assertPathIsUnused(fileSystem, path);
  }

  private static FileSystem fileSystemWith(Path path, PathState state) {
    FileSystem fileSystem = mock(FileSystem.class);
    when(fileSystem.pathState(path)).thenReturn(state);
    return fileSystem;
  }
}
