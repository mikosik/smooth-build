package org.smoothbuild.common.filesystem.base;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.junit.Assert.assertThrows;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.filesystem.base.PathState.DIR;
import static org.smoothbuild.common.filesystem.base.PathState.FILE;
import static org.smoothbuild.common.filesystem.base.PathState.NOTHING;
import static org.smoothbuild.common.testing.TestingByteString.byteString;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.testing.TestingSmallFileSystem;

public abstract class AbstractBucketTestSuite {
  protected FileSystem<Path> fileSystem;

  @Nested
  class _path_state {
    @Test
    void of_nonexistent_path_is_nothing() throws IOException {
      assertThat(fileSystem.pathState(path("abc"))).isEqualTo(NOTHING);
    }

    @Test
    void of_file_path_is_file() throws Exception {
      var path = path("myFile");
      createFile(path);
      assertThat(fileSystem.pathState(path)).isEqualTo(FILE);
    }

    @Test
    void of_directory_path_is_dir() throws Exception {
      var dir = path("my/dir");
      createDir(dir);
      assertThat(fileSystem.pathState(dir)).isEqualTo(DIR);
    }

    @Test
    void of_nonexistent_path_state_is_nothing_even_when_its_first_part_is_a_dir() throws Exception {
      var file = path("some/dir/myFile");
      createDir(file.parent());
      assertThat(fileSystem.pathState(file)).isEqualTo(NOTHING);
    }
  }

  @Nested
  class _files {
    @Test
    void fails_when_path_does_not_exist() {
      var path = path("abc");
      assertCall(() -> fileSystem.files(path))
          .throwsException(new IOException("Dir " + resolve(path) + " doesn't exist."));
    }

    @Test
    void fails_when_path_is_a_file() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file);
      assertCall(() -> fileSystem.files(file))
          .throwsException(
              new IOException("Dir " + resolve(file) + " doesn't exist. It is a file."));
    }

    @Test
    void returns_all_children() throws Exception {
      createFile(path("abc/dir1/file1.txt"));
      createFile(path("abc/dir2/file2.txt"));
      createFile(path("abc/text.txt"));
      assertThat(fileSystem.files(path("abc")))
          .containsExactly(path("dir1"), path("dir2"), path("text.txt"));
    }
  }

  @Nested
  class _size {
    @Test
    void returns_zero_for_empty_file() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file);
      assertThat(fileSystem.size(file)).isEqualTo(0);
    }

    @Test
    void returns_file_size() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file, byteString());
      assertThat(fileSystem.size(file)).isEqualTo(3);
    }

    @Test
    void reading_size_of_dir_causes_exception() throws Exception {
      var dir = path("my/dir");
      createDir(dir);
      assertCall(() -> fileSystem.size(dir))
          .throwsException(
              new IOException("File " + resolve(dir) + " doesn't exist. It is a dir."));
    }

    @Test
    void fails_for_nonexistent_path() {
      var dir = path("myFile");
      assertCall(() -> fileSystem.size(dir)).throwsException(IOException.class);
    }

    @Test
    void returns_size_of_target_file_for_link() throws IOException {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file, byteString());

      fileSystem.createLink(link, file);

      assertThat(fileSystem.size(link)).isEqualTo(byteString().size());
    }

    @Test
    void reading_size_of_link_that_targets_dir_causes_exception() throws IOException {
      var dir = path("my/dir");
      var link = path("myLink");
      createDir(dir);

      fileSystem.createLink(link, dir);

      assertCall(() -> fileSystem.size(dir))
          .throwsException(
              new IOException("File " + resolve(dir) + " doesn't exist. It is a dir."));
    }
  }

  @Nested
  class _source {
    @Test
    void provides_content_of_file() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file, byteString());
      assertThat(readFile(file)).isEqualTo(byteString());
    }

    @Test
    void provides_content_of_target_file_for_a_link() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file, byteString());

      fileSystem.createLink(link, file);

      assertThat(readFile(link)).isEqualTo(byteString());
    }

    @Test
    void provides_file_content_of_file_when_one_part_of_path_is_link_to_directory()
        throws Exception {
      var file = path("some/dir/myFile");
      var link = path("link");
      createFile(file, byteString());

      fileSystem.createLink(link, file.parent());

      assertThat(readFile(link.append(file.lastPart()))).isEqualTo(byteString());
    }

    @Test
    void fails_when_file_does_not_exist() {
      var file = path("myFile");
      assertCall(() -> readFile(file)).throwsException(IOException.class);
    }

    @Test
    void fails_when_path_is_dir() throws Exception {
      var dir = path("some/dir");
      createDir(dir);
      assertCall(() -> readFile(dir)).throwsException(IOException.class);
    }
  }

  @Nested
  class _sink {
    @Test
    void data_written_by_sink_can_be_read_by_source() throws Exception {
      var file = path("myFile");
      try (BufferedSink sink = buffer(fileSystem.sink(file))) {
        sink.write(byteString());
      }
      assertThat(readFile(file)).isEqualTo(byteString());
    }

    @Test
    void data_written_to_sink_overwrites_existing_file() throws Exception {
      var file = path("myFile");
      try (BufferedSink sink = buffer(fileSystem.sink(file))) {
        sink.write(byteString("abc"));
      }
      try (BufferedSink sink = buffer(fileSystem.sink(file))) {
        sink.write(byteString("def"));
      }
      assertThat(readFile(file)).isEqualTo(byteString("def"));
    }

    @Test
    void fails_when_parent_directory_not_exists() {
      assertCall(() -> writeFile(path("dir/file"))).throwsException(NoSuchFileException.class);
    }

    @Test
    void fails_when_path_is_a_directory() throws Exception {
      var dir = path("myDir");
      createDir(dir);
      assertCall(() -> writeFile(dir)).throwsException(IOException.class);
    }

    @Test
    void fails_when_parent_is_link_targeting_file() throws Exception {
      var file = path("myFile");
      var link = path("link");
      createFile(file);
      fileSystem.createLink(link, file);

      assertThrows(FileSystemException.class, () -> writeFile(link.appendPart("newFile")));
    }

    @Test
    void succeeds_when_parent_is_link_targeting_directory() throws Exception {
      var dir = path("myFile");
      var link = path("link");
      createDir(dir);
      fileSystem.createLink(link, dir);
      var newFile = link.appendPart("newFile");
      var content = byteString();

      writeFile(newFile, content);

      assertThat(readFile(newFile)).isEqualTo(content);
    }

    @Test
    void fails_when_parent_exists_and_is_a_file() throws Exception {
      var file = path("myDir/myFile");
      createFile(file);
      var path = file.append(path("otherFile"));
      assertThrows(FileSystemException.class, () -> writeFile(path));
    }
  }

  @Nested
  class _move {
    @Test
    void of_nonexistent_file_fails() {
      var source = path("source");
      var target = path("target");
      assertCall(() -> fileSystem.move(source, target))
          .throwsException(
              new IOException("Cannot move " + resolve(source) + ". It doesn't exist."));
    }

    @Test
    void of_directory_fails() throws Exception {
      var dir = path("dir");
      var source = dir.appendPart("file");
      var target = path("target");
      createFile(source);
      assertCall(() -> fileSystem.move(dir, target))
          .throwsException(new IOException("Cannot move " + resolve(dir) + ". It is directory."));
    }

    @Test
    void that_targets_directory_fails() throws Exception {
      var source = path("source");
      var dir = path("dir");
      createFile(source);
      createFile(path("dir/file"));
      assertCall(() -> fileSystem.move(source, dir))
          .throwsException(
              new IOException("Cannot move to " + resolve(dir) + ". It is directory."));
    }

    @Test
    void deletes_source_file() throws Exception {
      var source = path("source");
      var target = path("target");
      createFile(source);

      fileSystem.move(source, target);

      assertThat(fileSystem.pathState(source)).isEqualTo(NOTHING);
    }

    @Test
    void copies_file_content_to_target() throws Exception {
      var source = path("source");
      var target = path("target");
      createFile(source, byteString());

      fileSystem.move(source, target);

      assertThat(fileSystem.pathState(source)).isEqualTo(NOTHING);
      assertThat(readFile(target)).isEqualTo(byteString());
    }

    @Test
    void overwrites_target_file() throws Exception {
      var source = path("source");
      var target = path("target");
      createFile(source, byteString());
      createFile(target);

      fileSystem.move(source, target);

      assertThat(fileSystem.pathState(source)).isEqualTo(NOTHING);
      assertThat(readFile(target)).isEqualTo(byteString());
    }
  }

  @Nested
  class _delete {
    @Test
    void directory_removes_it_and_its_files_recursively() throws Exception {
      var dir = path("some/dir");
      var file1 = dir.append(path("myFile"));
      var file2 = dir.append(path("dir2/myFile"));
      createFile(file1);

      fileSystem.delete(dir);

      assertThat(fileSystem.pathState(file1)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(file2)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(dir)).isEqualTo(NOTHING);
    }

    @Test
    void file_removes_it() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file);

      fileSystem.delete(file);

      assertThat(fileSystem.pathState(file)).isEqualTo(NOTHING);
    }

    @Test
    void not_fails_for_nonexistent_path() throws Exception {
      var path = path("some/dir/myFile");

      fileSystem.delete(path);

      assertThat(fileSystem.pathState(path)).isEqualTo(NOTHING);
    }

    @Test
    void root_path_removes_all_files() throws Exception {
      var file = path("some/dir/myFile");
      var file2 = path("other/dir/otherFile");
      createFile(file);
      createFile(file2);

      fileSystem.delete(Path.root());

      assertThat(fileSystem.pathState(file)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(file2)).isEqualTo(NOTHING);
    }

    @Test
    void link_removes_it() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file, byteString());
      fileSystem.createLink(link, file);

      fileSystem.delete(link);

      assertThat(fileSystem.pathState(link)).isEqualTo(NOTHING);
    }

    @Test
    void link_not_removes_target_file() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file, byteString());
      fileSystem.createLink(link, file);

      fileSystem.delete(link);

      assertThat(fileSystem.pathState(file)).isEqualTo(FILE);
    }

    @Test
    void link_to_directory_not_removes_target_directory_nor_file_it_contains() throws Exception {
      var dir = path("my/dir");
      var file = dir.appendPart("myFile");
      var link = path("myLink");
      createFile(file);
      fileSystem.createLink(link, dir);

      fileSystem.delete(link);

      assertThat(fileSystem.pathState(link)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(dir)).isEqualTo(DIR);
      assertThat(fileSystem.pathState(file)).isEqualTo(FILE);
    }
  }

  @Nested
  class _create_link {
    @Test
    void fails_when_link_parent_directory_not_exists() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("missing_directory/myLink");
      createFile(file);

      assertCall(() -> fileSystem.createLink(link, file))
          .throwsException(NoSuchFileException.class);
    }

    @Test
    void fails_when_link_path_is_taken_by_file() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file);
      createFile(link);

      assertCall(() -> fileSystem.createLink(link, file))
          .throwsException(
              new IOException("Cannot use " + resolve(link) + " path. It is already taken."));
    }

    @Test
    void fails_when_link_path_is_taken_by_dir() throws Exception {
      var file = path("some/dir/myFile");
      var link = path("myLink");
      createFile(file);
      createDir(link);

      assertCall(() -> fileSystem.createLink(link.parent(), file))
          .throwsException(new IOException(
              "Cannot use " + resolve(link.parent()) + " path. It is already taken."));
    }
  }

  @Nested
  class _create_dir {
    @Test
    void creates_directory() throws Exception {
      var file = path("some/dir/myFile");
      fileSystem.createDir(file);
      assertThat(fileSystem.pathState(file)).isEqualTo(DIR);
    }

    @Test
    void not_fails_when_directory_exists() throws Exception {
      var file = path("some/dir/myFile");
      fileSystem.createDir(file);
      fileSystem.createDir(file);
      assertThat(fileSystem.pathState(file)).isEqualTo(DIR);
    }

    @Test
    void fails_when_file_at_given_path_exists() throws Exception {
      var file = path("some/dir/myFile");
      createFile(file);
      assertCall(() -> fileSystem.createDir(file))
          .throwsException(FileAlreadyExistsException.class);
    }
  }

  // helpers

  private void writeFile(Path path) throws IOException {
    writeFile(path, ByteString.EMPTY);
  }

  private void writeFile(Path path, ByteString byteString) throws IOException {
    TestingSmallFileSystem.writeFile(fileSystem, path, byteString);
  }

  private ByteString readFile(Path path) throws IOException {
    return TestingSmallFileSystem.readFile(fileSystem, path);
  }

  protected void createFile(Path path) throws IOException {
    createDir(path.parent());
    createFile(path, ByteString.of());
  }

  protected abstract void createFile(Path path, ByteString content) throws IOException;

  protected abstract void createDir(Path path) throws IOException;

  protected abstract String resolve(Path path);
}
