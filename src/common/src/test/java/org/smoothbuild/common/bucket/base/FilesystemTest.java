package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.PathState.DIR;
import static org.smoothbuild.common.bucket.base.PathState.FILE;
import static org.smoothbuild.common.bucket.base.PathState.NOTHING;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.testing.TestingBucketId.BUCKET_ID;
import static org.smoothbuild.common.testing.TestingFilesystem.createFile;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.HashSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.mem.MemoryBucket;

public class FilesystemTest {
  @Nested
  class _path_state {
    @Test
    void of_nonexistent_file_is_unknown() {
      var filesystem = filesystem();
      var path = fullPath(BUCKET_ID, "file");
      assertThat(filesystem.pathState(path)).isEqualTo(NOTHING);
    }

    @Test
    void of_file_is_file() throws IOException {
      var filesystem = filesystem();
      var path = fullPath(BUCKET_ID, "file");
      createFile(filesystem, path);
      assertThat(filesystem.pathState(path)).isEqualTo(FILE);
    }

    @Test
    void of_dir_is_dir() throws IOException {
      var filesystem = filesystem();
      var path = fullPath(BUCKET_ID, "dir");
      filesystem.createDir(path);
      assertThat(filesystem.pathState(path)).isEqualTo(DIR);
    }

    @Test
    void of_path_containing_unknown_bucket_throws_exception() {
      var filesystem = filesystem();
      var path = fullPath(new BucketId("unknown"), "file");
      assertCall(() -> filesystem.pathState(path)).throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _files_recursively {
    @Test
    void returns_all_files() throws IOException {
      var filesystem = filesystem();
      createFile(filesystem, fullPath(BUCKET_ID, "file"));
      createFile(filesystem, fullPath(BUCKET_ID, "dir/file"));
      createFile(filesystem, fullPath(BUCKET_ID, "dir/file2"));
      createFile(filesystem, fullPath(BUCKET_ID, "dir/subdir/file3"));

      var files = toSet(filesystem.filesRecursively(fullPath(BUCKET_ID, "dir")));
      assertThat(files).isEqualTo(set(path("file"), path("file2"), path("subdir/file3")));
    }

    @Test
    void returns_nothing_for_empty_dir() throws IOException {
      var filesystem = filesystem();
      createFile(filesystem, fullPath(BUCKET_ID, "file"));
      filesystem.createDir(fullPath(BUCKET_ID, "dir"));

      var files = toSet(filesystem.filesRecursively(fullPath(BUCKET_ID, "dir")));
      assertThat(files).isEqualTo(set());
    }

    @Test
    void throws_exception_for_non_existent_dir() throws IOException {
      var filesystem = filesystem();
      createFile(filesystem, fullPath(BUCKET_ID, "file"));

      assertCall(() -> filesystem.filesRecursively(fullPath(BUCKET_ID, "dir")))
          .throwsException(IOException.class);
    }

    private static HashSet<Path> toSet(PathIterator iterator) throws IOException {
      var files = new HashSet<Path>();
      while (iterator.hasNext()) {
        files.add(iterator.next());
      }
      return files;
    }
  }

  private static Filesystem filesystem() {
    return new Filesystem(new BucketResolver(map(BUCKET_ID, new MemoryBucket())));
  }
}
