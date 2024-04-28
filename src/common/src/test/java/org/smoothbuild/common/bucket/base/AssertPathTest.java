package org.smoothbuild.common.bucket.base;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.bucket.base.PathState.DIR;
import static org.smoothbuild.common.bucket.base.PathState.FILE;
import static org.smoothbuild.common.bucket.base.PathState.NOTHING;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class AssertPathTest {
  private final Path path = Path.path("some/path");

  @Test
  void assert_path_is_dir_returns_normally_for_dir_path() throws Exception {
    Bucket bucket = bucketWith(path, DIR);
    AssertPath.assertPathIsDir(bucket, path);
  }

  @Test
  void assert_path_is_dir_throws_exception_for_file_path() {
    Bucket bucket = bucketWith(path, FILE);
    assertCall(() -> AssertPath.assertPathIsDir(bucket, path))
        .throwsException(new IOException("Dir " + path.q() + " doesn't exist. It is a file."));
  }

  @Test
  void assert_path_is_dir_throws_exception_when_path_does_not_exist() {
    Bucket bucket = bucketWith(path, NOTHING);
    assertCall(() -> AssertPath.assertPathIsDir(bucket, path))
        .throwsException(new IOException("Dir " + path.q() + " doesn't exist."));
  }

  @Test
  void assert_path_is_file_returns_normally_for_file_path() throws Exception {
    Bucket bucket = bucketWith(path, FILE);
    AssertPath.assertPathIsFile(bucket, path);
  }

  @Test
  void assert_path_is_file_throws_exception_for_dir_path() {
    Bucket bucket = bucketWith(path, DIR);
    assertCall(() -> AssertPath.assertPathIsFile(bucket, path))
        .throwsException(new IOException("File " + path.q() + " doesn't exist. It is a dir."));
  }

  @Test
  void assert_path_is_file_throws_exception_when_path_does_not_exist() {
    Bucket bucket = bucketWith(path, NOTHING);
    assertCall(() -> AssertPath.assertPathIsFile(bucket, path))
        .throwsException(new IOException("File " + path.q() + " doesn't exist."));
  }

  @Test
  void assert_path_exists_returns_normally_for_file_path() throws Exception {
    Bucket bucket = bucketWith(path, FILE);
    AssertPath.assertPathExists(bucket, path);
  }

  @Test
  void assert_path_exists_returns_normally_for_dir_path() throws Exception {
    Bucket bucket = bucketWith(path, DIR);
    AssertPath.assertPathExists(bucket, path);
  }

  @Test
  void assert_path_exists_throws_exception_when_path_does_not_exist() {
    Bucket bucket = bucketWith(path, NOTHING);
    assertCall(() -> AssertPath.assertPathExists(bucket, path))
        .throwsException(new IOException("Path " + path.q() + " doesn't exist."));
  }

  @Test
  void assert_path_is_unused_throws_exception_for_file_path() {
    Bucket bucket = bucketWith(path, FILE);
    assertCall(() -> AssertPath.assertPathIsUnused(bucket, path))
        .throwsException(new IOException("Cannot use " + path.q() + " path. It is already taken."));
  }

  @Test
  void assert_path_is_unused_throws_exception_for_dir_path() {
    Bucket bucket = bucketWith(path, DIR);
    assertCall(() -> AssertPath.assertPathIsUnused(bucket, path))
        .throwsException(new IOException("Cannot use " + path.q() + " path. It is already taken."));
  }

  @Test
  void assert_path_is_unused_returns_normally_when_path_does_not_exist() throws Exception {
    Bucket bucket = bucketWith(path, NOTHING);
    AssertPath.assertPathIsUnused(bucket, path);
  }

  private static Bucket bucketWith(Path path, PathState state) {
    Bucket bucket = mock(Bucket.class);
    when(bucket.pathState(path)).thenReturn(state);
    return bucket;
  }
}
