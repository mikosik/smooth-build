package org.smoothbuild.common.filesystem.base;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.filesystem.base.PathState.DIR;
import static org.smoothbuild.common.filesystem.base.PathState.FILE;
import static org.smoothbuild.common.filesystem.base.PathState.NOTHING;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class AssertPathTest {
  private final Path path = Path.path("some/path");

  @Test
  void assert_path_is_dir_returns_normally_for_dir_path() throws Exception {
    FileSystem<Path> fileSystem = fileSystemWith(path, DIR);
    AssertPath.assertPathIsDir(fileSystem, path);
  }

  @Test
  void assert_path_is_dir_throws_exception_for_file_path() throws IOException {
    FileSystem<Path> fileSystem = fileSystemWith(path, FILE);
    assertCall(() -> AssertPath.assertPathIsDir(fileSystem, path))
        .throwsException(new IOException("Dir " + path.q() + " doesn't exist. It is a file."));
  }

  @Test
  void assert_path_is_dir_throws_exception_when_path_does_not_exist() throws IOException {
    FileSystem<Path> fileSystem = fileSystemWith(path, NOTHING);
    assertCall(() -> AssertPath.assertPathIsDir(fileSystem, path))
        .throwsException(new IOException("Dir " + path.q() + " doesn't exist."));
  }

  @Test
  void assert_path_is_file_returns_normally_for_file_path() throws Exception {
    FileSystem<Path> fileSystem = fileSystemWith(path, FILE);
    AssertPath.assertPathIsFile(fileSystem, path);
  }

  @Test
  void assert_path_is_file_throws_exception_for_dir_path() throws IOException {
    FileSystem<Path> fileSystem = fileSystemWith(path, DIR);
    assertCall(() -> AssertPath.assertPathIsFile(fileSystem, path))
        .throwsException(new IOException("File " + path.q() + " doesn't exist. It is a dir."));
  }

  @Test
  void assert_path_is_file_throws_exception_when_path_does_not_exist() throws IOException {
    FileSystem<Path> fileSystem = fileSystemWith(path, NOTHING);
    assertCall(() -> AssertPath.assertPathIsFile(fileSystem, path))
        .throwsException(new IOException("File " + path.q() + " doesn't exist."));
  }

  @Test
  void assert_path_exists_returns_normally_for_file_path() throws Exception {
    FileSystem<Path> fileSystem = fileSystemWith(path, FILE);
    AssertPath.assertPathExists(fileSystem, path);
  }

  @Test
  void assert_path_exists_returns_normally_for_dir_path() throws Exception {
    FileSystem<Path> fileSystem = fileSystemWith(path, DIR);
    AssertPath.assertPathExists(fileSystem, path);
  }

  @Test
  void assert_path_exists_throws_exception_when_path_does_not_exist() throws IOException {
    FileSystem<Path> fileSystem = fileSystemWith(path, NOTHING);
    assertCall(() -> AssertPath.assertPathExists(fileSystem, path))
        .throwsException(new IOException("Path " + path.q() + " doesn't exist."));
  }

  @Test
  void assert_path_is_unused_throws_exception_for_file_path() throws IOException {
    FileSystem<Path> fileSystem = fileSystemWith(path, FILE);
    assertCall(() -> AssertPath.assertPathIsUnused(fileSystem, path))
        .throwsException(new IOException("Cannot use " + path.q() + " path. It is already taken."));
  }

  @Test
  void assert_path_is_unused_throws_exception_for_dir_path() throws IOException {
    FileSystem<Path> fileSystem = fileSystemWith(path, DIR);
    assertCall(() -> AssertPath.assertPathIsUnused(fileSystem, path))
        .throwsException(new IOException("Cannot use " + path.q() + " path. It is already taken."));
  }

  @Test
  void assert_path_is_unused_returns_normally_when_path_does_not_exist() throws Exception {
    FileSystem<Path> fileSystem = fileSystemWith(path, NOTHING);
    AssertPath.assertPathIsUnused(fileSystem, path);
  }

  private static FileSystem<Path> fileSystemWith(Path path, PathState state) throws IOException {
    FileSystem<Path> fileSystem = mock();
    when(fileSystem.pathState(path)).thenReturn(state);
    return fileSystem;
  }
}
