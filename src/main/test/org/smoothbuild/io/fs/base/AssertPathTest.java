package org.smoothbuild.io.fs.base;

import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.io.fs.base.err.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.io.fs.base.err.NoSuchPathError;
import org.smoothbuild.io.fs.base.err.PathIsAlreadyTakenError;
import org.testory.Closure;

public class AssertPathTest {
  private FileSystem fileSystem;
  private final Path path = Path.path("some/path");

  @Test
  public void assert_path_is_dir_returns_normally_for_dir_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(DIR), fileSystem).pathState(path);
    when(assertPathIsDir(fileSystem, path));
    thenReturned();
  }

  @Test
  public void assert_path_is_dir_throws_exception_for_file_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(FILE), fileSystem).pathState(path);
    when(assertPathIsDir(fileSystem, path));
    thenThrown(NoSuchDirButFileError.class);
  }

  @Test
  public void assert_path_is_dir_throws_exception_when_path_does_not_exist() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(NOTHING), fileSystem).pathState(path);
    when(assertPathIsDir(fileSystem, path));
    thenThrown(NoSuchDirError.class);
  }

  private Closure assertPathIsDir(final FileSystem fileSystem, final Path path) {
    return new Closure() {
      @Override
      public Void invoke() throws Throwable {
        AssertPath.assertPathIsDir(fileSystem, path);
        return null;
      }
    };
  }

  @Test
  public void assert_path_is_file_returns_normally_for_file_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(FILE), fileSystem).pathState(path);
    when(assertPathIsFile(fileSystem, path));
    thenReturned();
  }

  @Test
  public void assert_path_is_file_throws_exception_for_dir_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(DIR), fileSystem).pathState(path);
    when(assertPathIsFile(fileSystem, path));
    thenThrown(NoSuchFileButDirError.class);
  }

  @Test
  public void assert_path_is_file_throws_exception_when_path_does_not_exist() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(NOTHING), fileSystem).pathState(path);
    when(assertPathIsFile(fileSystem, path));
    thenThrown(NoSuchFileError.class);
  }

  private Closure assertPathIsFile(final FileSystem fileSystem, final Path path) {
    return new Closure() {
      @Override
      public Void invoke() throws Throwable {
        AssertPath.assertPathIsFile(fileSystem, path);
        return null;
      }
    };
  }

  @Test
  public void assert_path_exists_returns_normally_for_file_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(FILE), fileSystem).pathState(path);
    when(assertPathExists(fileSystem, path));
    thenReturned();
  }

  @Test
  public void assert_path_exists_returns_normally_for_dir_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(DIR), fileSystem).pathState(path);
    when(assertPathExists(fileSystem, path));
    thenReturned();
  }

  @Test
  public void assert_path_exists_throws_exception_when_path_does_not_exist() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(NOTHING), fileSystem).pathState(path);
    when(assertPathExists(fileSystem, path));
    thenThrown(NoSuchPathError.class);
  }

  private Closure assertPathExists(final FileSystem fileSystem, final Path path) {
    return new Closure() {
      @Override
      public Void invoke() throws Throwable {
        AssertPath.assertPathExists(fileSystem, path);
        return null;
      }
    };
  }

  @Test
  public void assert_path_is_unused_throws_exception_for_file_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(FILE), fileSystem).pathState(path);
    when(assertPathIsUnused(fileSystem, path));
    thenThrown(PathIsAlreadyTakenError.class);
  }

  @Test
  public void assert_path_is_unused_throws_exception_for_dir_path() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(DIR), fileSystem).pathState(path);
    when(assertPathIsUnused(fileSystem, path));
    thenThrown(PathIsAlreadyTakenError.class);
  }

  @Test
  public void assert_path_is_unused_returns_normally_when_path_does_not_exist() {
    given(fileSystem = mock(FileSystem.class));
    given(willReturn(NOTHING), fileSystem).pathState(path);
    when(assertPathIsUnused(fileSystem, path));
    thenReturned();
  }

  private Closure assertPathIsUnused(final FileSystem fileSystem, final Path path) {
    return new Closure() {
      @Override
      public Void invoke() throws Throwable {
        AssertPath.assertPathIsUnused(fileSystem, path);
        return null;
      }
    };
  }
}
