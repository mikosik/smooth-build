package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.PathState.DIR;
import static org.smoothbuild.common.bucket.base.PathState.FILE;
import static org.smoothbuild.common.bucket.base.PathState.NOTHING;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.testing.TestingBucket;

public abstract class AbstractBucketTestSuite {
  protected Bucket bucket;

  @Nested
  class _path_state {
    @Test
    public void of_nonexistent_path_is_nothing() {
      assertThat(bucket.pathState(path("abc"))).isEqualTo(NOTHING);
    }

    @Test
    public void of_file_path_is_file() throws Exception {
      var path = path("myFile");
      createFile(path);
      assertThat(bucket.pathState(path)).isEqualTo(FILE);
    }

    @Test
    public void of_directory_path_is_dir() throws Exception {
      var dir = path("my/dir");
      createDir(dir);
      assertThat(bucket.pathState(dir)).isEqualTo(DIR);
    }

    @Test
    public void of_nonexistent_path_state_is_nothing_even_when_its_first_part_is_a_dir()
        throws Exception {
      var file = path("some/dir/myFile");
      createDir(file.parent());
      assertThat(bucket.pathState(file)).isEqualTo(NOTHING);
    }
  }

  @Nested
  class _files {
    @Test
    public void throws_exception_when_path_does_not_exist() {
      var path = path("abc");
      assertCall(() -> bucket.files(path))
          .throwsException(new IOException("Dir " + resolve(path) + " doesn't exist."));
    }

    @Test
    public void throws_exception_when_path_is_a_file() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file);
      assertCall(() -> bucket.files(file))
          .throwsException(
              new IOException("Dir " + resolve(file) + " doesn't exist. It is a file."));
    }

    @Test
    public void returns_all_children() throws Exception {
      createFile(path("abc/dir1/file1.txt"));
      createFile(path("abc/dir2/file2.txt"));
      createFile(path("abc/text.txt"));
      assertThat(bucket.files(path("abc")))
          .containsExactly(path("dir1"), path("dir2"), path("text.txt"));
    }
  }

  @Nested
  class _size {
    @Test
    public void returns_zero_for_empty_file() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file);
      assertThat(bucket.size(file)).isEqualTo(0);
    }

    @Test
    public void returns_file_size() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file, byteString());
      assertThat(bucket.size(file)).isEqualTo(3);
    }

    @Test
    public void reading_size_of_dir_causes_exception() throws Exception {
      var dir = path("my/dir");
      createDir(dir);
      assertCall(() -> bucket.size(dir))
          .throwsException(
              new IOException("File " + resolve(dir) + " doesn't exist. It is a dir."));
    }

    @Test
    public void fails_for_nonexistent_path() {
      var dir = path("myFile");
      assertCall(() -> bucket.size(dir)).throwsException(IOException.class);
    }

    @Test
    public void returns_size_of_target_file_for_link() throws IOException {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file, byteString());

      bucket.createLink(link, file);

      assertThat(bucket.size(link)).isEqualTo(byteString().size());
    }

    @Test
    public void reading_size_of_link_that_targets_dir_causes_exception() throws IOException {
      var dir = path("my/dir");
      var link = path("myLink");
      createDir(dir);

      bucket.createLink(link, dir);

      assertCall(() -> bucket.size(dir))
          .throwsException(
              new IOException("File " + resolve(dir) + " doesn't exist. It is a dir."));
    }
  }

  @Nested
  class _source {
    @Test
    public void provides_content_of_file() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file, byteString());
      assertThat(readFile(file)).isEqualTo(byteString());
    }

    @Test
    public void provides_content_of_target_file_for_a_link() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file, byteString());

      bucket.createLink(link, file);

      assertThat(readFile(link)).isEqualTo(byteString());
    }

    @Test
    public void provides_file_content_of_file_when_one_part_of_path_is_link_to_directory()
        throws Exception {
      var file = path("some/dir/myFile");
      var link = path("link");
      createFile(file, byteString());

      bucket.createLink(link, file.parent());

      assertThat(readFile(link.append(file.lastPart()))).isEqualTo(byteString());
    }

    @Test
    public void throws_exception_when_file_does_not_exist() {
      var file = path("myFile");
      assertCall(() -> readFile(file)).throwsException(IOException.class);
    }

    @Test
    public void throws_exception_when_path_is_dir() throws Exception {
      var dir = path("some/dir");
      createDir(dir);
      assertCall(() -> readFile(dir)).throwsException(IOException.class);
    }
  }

  @Nested
  class _sink {
    @Test
    public void data_written_by_sink_can_be_read_by_source() throws Exception {
      var file = path("myFile");
      try (BufferedSink sink = bucket.sink(file)) {
        sink.write(byteString());
      }
      assertThat(readFile(file)).isEqualTo(byteString());
    }

    @Test
    public void data_written_to_sink_overwrites_existing_file() throws Exception {
      var file = path("myFile");
      try (BufferedSink sink = bucket.sink(file)) {
        sink.write(ByteString.encodeUtf8("abc"));
      }
      try (BufferedSink sink = bucket.sink(file)) {
        sink.write(ByteString.encodeUtf8("def"));
      }
      assertThat(readFile(file)).isEqualTo(ByteString.encodeUtf8("def"));
    }

    @Test
    public void fails_when_path_is_a_directory() throws Exception {
      var dir = path("myDir");
      createDir(dir);
      assertCall(() -> writeFile(dir)).throwsException(IOException.class);
    }

    @Test
    public void fails_when_parent_is_link_targeting_file() throws Exception {
      var file = path("myFile");
      var link = path("link");
      createFile(file);
      bucket.createLink(link, file);

      assertThrows(FileSystemException.class, () -> writeFile(link.appendPart("newFile")));
    }

    @Test
    public void succeeds_when_parent_is_link_targeting_directory() throws Exception {
      var dir = path("myFile");
      var link = path("link");
      createDir(dir);
      bucket.createLink(link, dir);
      var newFile = link.appendPart("newFile");
      var content = byteString();

      writeFile(newFile, content);

      assertThat(readFile(newFile)).isEqualTo(content);
    }

    @Test
    public void fails_when_parent_exists_and_is_a_file() throws Exception {
      var file = path("myDir/myFile");
      createFile(file);
      var path = file.append(path("otherFile"));
      assertThrows(FileSystemException.class, () -> writeFile(path));
    }
  }

  @Nested
  class _move {
    @Test
    public void of_nonexistent_file_fails() {
      var source = path("source");
      var target = path("target");
      assertCall(() -> bucket.move(source, target))
          .throwsException(
              new IOException("Cannot move " + resolve(source) + ". It doesn't exist."));
    }

    @Test
    public void of_directory_fails() throws Exception {
      var dir = path("dir");
      var source = dir.appendPart("file");
      var target = path("target");
      createFile(source);
      assertCall(() -> bucket.move(dir, target))
          .throwsException(new IOException("Cannot move " + resolve(dir) + ". It is directory."));
    }

    @Test
    public void that_targets_directory_fails() throws Exception {
      var source = path("source");
      var dir = path("dir");
      createFile(source);
      createFile(path("dir/file"));
      assertCall(() -> bucket.move(source, dir))
          .throwsException(
              new IOException("Cannot move to " + resolve(dir) + ". It is directory."));
    }

    @Test
    public void deletes_source_file() throws Exception {
      var source = path("source");
      var target = path("target");
      createFile(source);

      bucket.move(source, target);

      assertThat(bucket.pathState(source)).isEqualTo(NOTHING);
    }

    @Test
    public void copies_file_content_to_target() throws Exception {
      var source = path("source");
      var target = path("target");
      createFile(source, byteString());

      bucket.move(source, target);

      assertThat(bucket.pathState(source)).isEqualTo(NOTHING);
      assertThat(readFile(target)).isEqualTo(byteString());
    }

    @Test
    public void overwrites_target_file() throws Exception {
      var source = path("source");
      var target = path("target");
      createFile(source, byteString());
      createFile(target);

      bucket.move(source, target);

      assertThat(bucket.pathState(source)).isEqualTo(NOTHING);
      assertThat(readFile(target)).isEqualTo(byteString());
    }
  }

  @Nested
  class _delete {
    @Test
    public void directory_removes_its_files_recursively() throws Exception {
      var dir = path("some/dir");
      var file1 = dir.append(path("myFile"));
      var file2 = dir.append(path("dir2/myFile"));
      createFile(file1);

      bucket.delete(dir);

      assertThat(bucket.pathState(file1)).isEqualTo(NOTHING);
      assertThat(bucket.pathState(file2)).isEqualTo(NOTHING);
    }

    @Test
    public void file_removes_it() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file);

      bucket.delete(file);

      assertThat(bucket.pathState(file)).isEqualTo(NOTHING);
    }

    @Test
    public void not_fails_for_nonexistent_path() throws Exception {
      var path = path("some/dir/myFile");

      bucket.delete(path);

      assertThat(bucket.pathState(path)).isEqualTo(NOTHING);
    }

    @Test
    public void root_path_removes_all_files() throws Exception {
      var file = path("some/dir/myFile");
      var file2 = path("other/dir/otherFile");
      createFile(file);
      createFile(file2);

      bucket.delete(Path.root());

      assertThat(bucket.pathState(file)).isEqualTo(NOTHING);
      assertThat(bucket.pathState(file2)).isEqualTo(NOTHING);
    }

    @Test
    public void link_removes_it() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file, byteString());
      bucket.createLink(link, file);

      bucket.delete(link);

      assertThat(bucket.pathState(link)).isEqualTo(NOTHING);
    }

    @Test
    public void link_not_removes_target_file() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file, byteString());
      bucket.createLink(link, file);

      bucket.delete(link);

      assertThat(bucket.pathState(file)).isEqualTo(FILE);
    }

    @Test
    public void link_to_directory_not_removes_target_directory_nor_file_it_contains()
        throws Exception {
      var dir = path("my/dir");
      var file = dir.appendPart("myFile");
      var link = path("myLink");
      createFile(file);
      bucket.createLink(link, dir);

      bucket.delete(link);

      assertThat(bucket.pathState(link)).isEqualTo(NOTHING);
      assertThat(bucket.pathState(dir)).isEqualTo(DIR);
      assertThat(bucket.pathState(file)).isEqualTo(FILE);
    }
  }

  @Nested
  class _link {
    @Test
    public void cannot_create_link_when_link_path_is_taken_by_file() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file);
      createFile(link);

      assertCall(() -> bucket.createLink(link, file))
          .throwsException(
              new IOException("Cannot use " + resolve(link) + " path. It is already taken."));
    }

    @Test
    public void cannot_create_link_when_link_path_is_taken_by_dir() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file);
      createDir(link);

      assertCall(() -> bucket.createLink(link.parent(), file))
          .throwsException(new IOException(
              "Cannot use " + resolve(link.parent()) + " path. It is already taken."));
    }
  }

  @Nested
  class _create_dir {
    @Test
    public void creates_directory() throws Exception {
      var file = path("some/dir/myFile");
      bucket.createDir(file);
      assertThat(bucket.pathState(file)).isEqualTo(DIR);
    }

    @Test
    public void not_fails_when_directory_exists() throws Exception {
      var file = path("some/dir/myFile");
      bucket.createDir(file);
      bucket.createDir(file);
      assertThat(bucket.pathState(file)).isEqualTo(DIR);
    }

    @Test
    public void fails_when_file_at_given_path_exists() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file);
      assertCall(() -> bucket.createDir(file)).throwsException(FileAlreadyExistsException.class);
    }
  }

  // helpers

  private void writeFile(Path path) throws IOException {
    writeFile(path, ByteString.EMPTY);
  }

  private void writeFile(Path path, ByteString byteString) throws IOException {
    try (var bufferedSink = bucket.sink(path)) {
      bufferedSink.write(byteString);
    }
  }

  private ByteString readFile(Path path) throws IOException {
    return TestingBucket.readFile(bucket, path);
  }

  protected void createFile(Path path) throws IOException {
    createDir(path.parent());
    createFile(path, ByteString.of());
  }

  private static ByteString byteString() {
    return ByteString.encodeUtf8("abc");
  }

  protected abstract void createFile(Path path, ByteString content) throws IOException;

  protected abstract void createDir(Path path) throws IOException;

  protected abstract String resolve(Path path);
}
