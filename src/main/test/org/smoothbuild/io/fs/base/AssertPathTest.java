package org.smoothbuild.io.fs.base;

import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsDir;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsFile;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.io.IOException;

import org.junit.Test;

public class AssertPathTest {
  private FileSystem fileSystem;
  private final Path path = Path.path("some/path");

  @Test
  public void assert_path_is_dir_returns_normally_for_dir_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(DIR), fileSystem).pathState(path);
    when(() -> assertPathIsDir(fileSystem, path));
    thenReturned();
  }

  @Test
  public void assert_path_is_dir_throws_exception_for_file_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(FILE), fileSystem).pathState(path);
    when(() -> assertPathIsDir(fileSystem, path));
    thenThrown(exception(new IOException("Dir " + path + " doesn't exist. It is a file.")));
  }

  @Test
  public void assert_path_is_dir_throws_exception_when_path_does_not_exist() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(NOTHING), fileSystem).pathState(path);
    when(() -> assertPathIsDir(fileSystem, path));
    thenThrown(exception(new IOException("Dir " + path + " doesn't exist.")));
  }

  @Test
  public void assert_path_is_file_returns_normally_for_file_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(FILE), fileSystem).pathState(path);
    when(() -> assertPathIsFile(fileSystem, path));
    thenReturned();
  }

  @Test
  public void assert_path_is_file_throws_exception_for_dir_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(DIR), fileSystem).pathState(path);
    when(() -> assertPathIsFile(fileSystem, path));
    thenThrown(exception(new IOException("File " + path + " doesn't exist. It is a dir.")));
  }

  @Test
  public void assert_path_is_file_throws_exception_when_path_does_not_exist() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(NOTHING), fileSystem).pathState(path);
    when(() -> assertPathIsFile(fileSystem, path));
    thenThrown(exception(new IOException("File " + path + " doesn't exist.")));
  }

  @Test
  public void assert_path_exists_returns_normally_for_file_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(FILE), fileSystem).pathState(path);
    when(() -> assertPathExists(fileSystem, path));
    thenReturned();
  }

  @Test
  public void assert_path_exists_returns_normally_for_dir_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(DIR), fileSystem).pathState(path);
    when(() -> assertPathExists(fileSystem, path));
    thenReturned();
  }

  @Test
  public void assert_path_exists_throws_exception_when_path_does_not_exist() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(NOTHING), fileSystem).pathState(path);
    when(() -> assertPathExists(fileSystem, path));
    thenThrown(exception(new IOException("Path " + path + " doesn't exist.")));
  }

  @Test
  public void assert_path_is_unused_throws_exception_for_file_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(FILE), fileSystem).pathState(path);
    when(() -> assertPathIsUnused(fileSystem, path));
    thenThrown(exception(new IOException("Cannot use " + path
        + " path. It is already taken.")));
  }

  @Test
  public void assert_path_is_unused_throws_exception_for_dir_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(DIR), fileSystem).pathState(path);
    when(() -> assertPathIsUnused(fileSystem, path));
    thenThrown(exception(new IOException("Cannot use " + path
        + " path. It is already taken.")));
  }

  @Test
  public void assert_path_is_unused_returns_normally_when_path_does_not_exist() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(NOTHING), fileSystem).pathState(path);
    when(() -> assertPathIsUnused(fileSystem, path));
    thenReturned();
  }
}
