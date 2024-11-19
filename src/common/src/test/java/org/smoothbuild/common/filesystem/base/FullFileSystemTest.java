package org.smoothbuild.common.filesystem.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.collect.Set.setOfAll;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.filesystem.base.PathState.DIR;
import static org.smoothbuild.common.filesystem.base.PathState.FILE;
import static org.smoothbuild.common.filesystem.base.PathState.NOTHING;
import static org.smoothbuild.common.testing.TestingByteString.byteString;
import static org.smoothbuild.common.testing.TestingFilesystem.createFile;
import static org.smoothbuild.common.testing.TestingFilesystem.readFile;
import static org.smoothbuild.common.testing.TestingFilesystem.writeFile;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.HashSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;

public class FullFileSystemTest {
  public static final Alias UNKNOWN_ALIAS = new Alias("unknown");

  @Nested
  class _path_state {
    @Test
    void returns_unknown_when_file_not_exists() throws IOException {
      var filesystem = filesystem();
      var path = fullPath(alias(), "file");
      assertThat(filesystem.pathState(path)).isEqualTo(NOTHING);
    }

    @Test
    void return_file_when_path_points_to_a_file() throws IOException {
      var filesystem = filesystem();
      var path = fullPath(alias(), "file");
      createFile(filesystem, path);
      assertThat(filesystem.pathState(path)).isEqualTo(FILE);
    }

    @Test
    void returns_dir_when_path_points_to_a_dir() throws IOException {
      var filesystem = filesystem();
      var path = fullPath(alias(), "dir");
      filesystem.createDir(path);
      assertThat(filesystem.pathState(path)).isEqualTo(DIR);
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var path = fullPath(new Alias("unknown"), "file");
      assertCall(() -> filesystem.pathState(path)).throwsException(IOException.class);
    }
  }

  @Nested
  class _files_recursively {
    @Test
    void returns_all_files() throws IOException {
      var filesystem = filesystem();
      createFile(filesystem, fullPath(alias(), "file"));
      createFile(filesystem, fullPath(alias(), "dir/file"));
      createFile(filesystem, fullPath(alias(), "dir/file2"));
      createFile(filesystem, fullPath(alias(), "dir/subdir/file3"));

      var files = toSet(filesystem.filesRecursively(fullPath(alias(), "dir")));
      assertThat(files).isEqualTo(set(path("file"), path("file2"), path("subdir/file3")));
    }

    @Test
    void returns_nothing_for_empty_dir() throws IOException {
      var filesystem = filesystem();
      createFile(filesystem, fullPath(alias(), "file"));
      filesystem.createDir(fullPath(alias(), "dir"));

      var files = toSet(filesystem.filesRecursively(fullPath(alias(), "dir")));
      assertThat(files).isEqualTo(set());
    }

    @Test
    void fails_when_dir_not_exists() throws IOException {
      var filesystem = filesystem();
      createFile(filesystem, fullPath(alias(), "file"));

      assertCall(() -> filesystem.filesRecursively(fullPath(alias(), "dir")))
          .throwsException(IOException.class);
    }

    @Test
    void fails_for_unknown_alias() {
      var filesystem = filesystem();

      assertCall(() -> filesystem.filesRecursively(fullPath(UNKNOWN_ALIAS, "file")))
          .throwsException(new IOException("Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }

    private static HashSet<Path> toSet(PathIterator iterator) throws IOException {
      var files = new HashSet<Path>();
      while (iterator.hasNext()) {
        files.add(iterator.next());
      }
      return files;
    }
  }

  @Nested
  class _files {
    @Test
    void fails_when_path_not_exists() {
      var filesystem = filesystem();
      var path = fullPath(alias(), path("abc"));
      assertCall(() -> filesystem.files(path))
          .throwsException(
              new IOException("Error listing files in '{alias-1}/abc'. Dir 'abc' doesn't exist."));
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var path = fullPath(UNKNOWN_ALIAS, path("abc"));
      assertCall(() -> filesystem.files(path))
          .throwsException(new IOException("Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }

    @Test
    void fails_when_path_is_a_file() throws Exception {
      var filesystem = filesystem();
      var path = fullPath(alias(), path("some/dir/myFile"));
      createFile(filesystem, path);
      assertCall(() -> filesystem.files(path))
          .throwsException(new IOException("Error listing files in '{alias-1}/some/dir/myFile'. "
              + "Dir 'some/dir/myFile' doesn't exist. It is a file."));
    }

    @Test
    void returns_all_children() throws Exception {
      var filesystem = filesystem();
      createFile(filesystem, fullPath(alias(), "file"));
      createFile(filesystem, fullPath(alias(), "dir/file"));
      createFile(filesystem, fullPath(alias(), "dir/file2"));
      createFile(filesystem, fullPath(alias(), "dir/subdir/file3"));

      var files = setOfAll(filesystem.files(fullPath(alias(), "dir")));
      assertThat(files).containsExactly(path("file"), path("file2"), path("subdir"));
    }
  }

  @Nested
  class _move {
    @Test
    void fails_when_source_not_exists() {
      var filesystem = filesystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      assertCall(() -> filesystem.move(source, target))
          .throwsException(new IOException("Error moving '{alias-1}/source' to '{alias-1}/target'. "
              + "Cannot move 'source'. It doesn't exist."));
    }

    @Test
    void fails_when_source_bucket_and_target_bucket_are_different() {
      var filesystem = filesystem();
      var source = fullPath(UNKNOWN_ALIAS, "source");
      var target = fullPath(alias(), "target");
      assertCall(() -> filesystem.move(source, target))
          .throwsException(new IllegalArgumentException(
              "Alias 'unknown' in source is different from alias 'alias-1' in target."));
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var source = fullPath(UNKNOWN_ALIAS, "source");
      var target = fullPath(UNKNOWN_ALIAS, "target");
      assertCall(() -> filesystem.move(source, target))
          .throwsException(new IOException("Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }

    @Test
    void fails_when_source_is_a_dir() throws Exception {
      var filesystem = filesystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      filesystem.createDir(source);

      assertCall(() -> filesystem.move(source, target))
          .throwsException(new IOException("Error moving '{alias-1}/source' to '{alias-1}/target'. "
              + "Cannot move 'source'. It is directory."));
    }

    @Test
    void fails_when_target_is_a_dir() throws IOException {
      var filesystem = filesystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      filesystem.createDir(target);
      assertCall(() -> filesystem.move(source, target))
          .throwsException(new IOException("Error moving '{alias-1}/source' to '{alias-1}/target'. "
              + "Cannot move 'source'. It doesn't exist."));
    }

    @Test
    void deletes_source_file() throws Exception {
      var filesystem = filesystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      createFile(filesystem, source);

      filesystem.move(source, target);

      assertThat(filesystem.pathState(source)).isEqualTo(NOTHING);
    }

    @Test
    void copies_file_content_to_target() throws Exception {
      var filesystem = filesystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      createFile(filesystem, source, "abc");

      filesystem.move(source, target);

      assertThat(readFile(filesystem, target)).isEqualTo(byteString("abc"));
    }

    @Test
    void overwrites_target_file() throws Exception {
      var filesystem = filesystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      createFile(filesystem, source, "abc");
      createFile(filesystem, target, "def");

      filesystem.move(source, target);

      assertThat(readFile(filesystem, target)).isEqualTo(byteString("abc"));
    }
  }

  @Nested
  class _delete {
    @Test
    void directory_removes_it_and_its_files_recursively() throws Exception {
      var filesystem = filesystem();
      var dir = fullPath(alias(), path("some/dir"));
      var file1 = dir.append(path("myFile"));
      var file2 = dir.append(path("dir2/myFile"));
      createFile(filesystem, file1);

      filesystem.delete(dir);

      assertThat(filesystem.pathState(file1)).isEqualTo(NOTHING);
      assertThat(filesystem.pathState(file2)).isEqualTo(NOTHING);
      assertThat(filesystem.pathState(dir)).isEqualTo(NOTHING);
    }

    @Test
    void file_removes_it() throws Exception {
      var filesystem = filesystem();
      var dir = fullPath(alias(), path("some/dir"));
      var file = dir.append(path("myFile"));
      createFile(filesystem, file);

      filesystem.delete(file);

      assertThat(filesystem.pathState(file)).isEqualTo(NOTHING);
    }

    @Test
    void not_fails_when_path_not_exists() throws Exception {
      var filesystem = filesystem();
      var path = fullPath(alias(), path("file"));

      filesystem.delete(path);

      assertThat(filesystem.pathState(path)).isEqualTo(NOTHING);
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var path = fullPath(UNKNOWN_ALIAS, path("file"));

      assertCall(() -> filesystem.delete(path))
          .throwsException(new IOException("Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }

    @Test
    void root_path_removes_all_files() throws Exception {
      var filesystem = filesystem();
      var file1 = fullPath(alias(), path("some/dir/myFile"));
      var file2 = fullPath(alias(), path("other/dir/otherFile"));
      createFile(filesystem, file1);
      createFile(filesystem, file2);

      filesystem.delete(fullPath(alias(), Path.root()));

      assertThat(filesystem.pathState(file1)).isEqualTo(NOTHING);
      assertThat(filesystem.pathState(file2)).isEqualTo(NOTHING);
    }

    @Test
    void link_removes_it_but_not_target_file() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), path("some/dir/myFile"));
      var link = fullPath(alias(), path("myLink"));
      createFile(filesystem, file);
      filesystem.createLink(link, file);

      filesystem.delete(link);

      assertThat(filesystem.pathState(link)).isEqualTo(NOTHING);
      assertThat(filesystem.pathState(file)).isEqualTo(FILE);
    }

    @Test
    void link_to_directory_not_removes_target_directory_nor_file_it_contains() throws Exception {
      var filesystem = filesystem();
      var dir = fullPath(alias(), path("some/dir"));
      var file = dir.append("myFile");
      var link = fullPath(alias(), path("myLink"));
      createFile(filesystem, file);
      filesystem.createLink(link, dir);

      filesystem.delete(link);

      assertThat(filesystem.pathState(link)).isEqualTo(NOTHING);
      assertThat(filesystem.pathState(dir)).isEqualTo(DIR);
      assertThat(filesystem.pathState(file)).isEqualTo(FILE);
    }
  }

  @Nested
  class _size {
    @Test
    void returns_zero_for_empty_file() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "file");
      createFile(filesystem, file);

      assertThat(filesystem.size(file)).isEqualTo(0);
    }

    @Test
    void returns_file_size() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "file");
      createFile(filesystem, file, "abc");

      assertThat(filesystem.size(file)).isEqualTo(3);
    }

    @Test
    void reading_size_of_dir_causes_exception() throws Exception {
      var filesystem = filesystem();
      var path = fullPath(alias(), "dir");
      filesystem.createDir(path);

      assertCall(() -> filesystem.size(path))
          .throwsException(new IOException(
              "Error fetching size of '{alias-1}/dir'. File 'dir' doesn't exist. It is a dir."));
    }

    @Test
    void fails_when_path_not_exists() {
      var filesystem = filesystem();
      var path = fullPath(alias(), "dir");
      assertCall(() -> filesystem.size(path)).throwsException(IOException.class);
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var path = fullPath(UNKNOWN_ALIAS, "dir");
      assertCall(() -> filesystem.size(path)).throwsException(IOException.class);
    }

    @Test
    void returns_size_of_target_file_for_link() throws IOException {
      var filesystem = filesystem();
      var file = fullPath(alias(), path("some/dir/myFile"));
      var link = fullPath(alias(), path("myLink"));
      createFile(filesystem, file, "abc");
      filesystem.createLink(link, file);

      assertThat(filesystem.size(link)).isEqualTo(3);
    }

    @Test
    void reading_size_of_link_that_targets_dir_causes_exception() throws IOException {
      var filesystem = filesystem();
      var dir = fullPath(alias(), path("some/dir"));
      var link = fullPath(alias(), path("myLink"));
      filesystem.createDir(dir);
      filesystem.createLink(link, dir);

      assertCall(() -> filesystem.size(dir))
          .throwsException(
              new IOException(
                  "Error fetching size of '{alias-1}/some/dir'. File 'some/dir' doesn't exist. It is a dir."));
    }
  }

  @Nested
  class _source {
    @Test
    void provides_content_of_file() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "file");
      createFile(filesystem, file, "abc");

      assertThat(readFile(filesystem, file)).isEqualTo(byteString("abc"));
    }

    @Test
    void provides_content_of_target_file_for_a_link() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "file");
      var link = fullPath(alias(), "myLink");
      createFile(filesystem, file, "abc");
      filesystem.createLink(link, file);

      assertThat(readFile(filesystem, link)).isEqualTo(byteString("abc"));
    }

    @Test
    void provides_file_content_of_file_when_one_part_of_path_is_link_to_directory()
        throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");
      createFile(filesystem, file, "abc");
      filesystem.createLink(link, file.parent());

      assertThat(readFile(filesystem, link.append(file.path().lastPart()))).isEqualTo(byteString());
    }

    @Test
    void fails_when_file_not_exists() {
      var filesystem = filesystem();
      var file = fullPath(alias(), "myFile");
      assertCall(() -> readFile(filesystem, file))
          .throwsException(new IOException(
              "Error reading file '{alias-1}/myFile'. File 'myFile' doesn't exist."));
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var file = fullPath(alias(), "myFile");
      assertCall(() -> readFile(filesystem, file))
          .throwsException(new IOException(
              "Error reading file '{alias-1}/myFile'. File 'myFile' doesn't exist."));
    }

    @Test
    void fails_when_path_is_dir() throws Exception {
      var filesystem = filesystem();
      var dir = fullPath(alias(), "dir");
      filesystem.createDir(dir);
      assertCall(() -> readFile(filesystem, dir))
          .throwsException(new IOException(
              "Error reading file '{alias-1}/dir'. File 'dir' doesn't exist. It is a dir."));
    }
  }

  @Nested
  class _sink {
    @Test
    void data_written_by_sink_can_be_read_by_source() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "myFile");
      var content = byteString("abc");

      writeFile(filesystem, file, content);
      assertThat(readFile(filesystem, file)).isEqualTo(content);
    }

    @Test
    void data_written_to_sink_overwrites_existing_file() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "myFile");
      var content = byteString("abc");
      var content2 = byteString("def");

      writeFile(filesystem, file, content);
      writeFile(filesystem, file, content2);
      assertThat(readFile(filesystem, file)).isEqualTo(content2);
    }

    @Test
    void fails_when_parent_directory_not_exists() {
      var filesystem = filesystem();
      var file = fullPath(alias(), "dir/myFile");

      assertCall(() -> filesystem.sink(file))
          .throwsException(new IOException("Error writing file '{alias-1}/dir/myFile'. 'dir'"));
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var file = fullPath(UNKNOWN_ALIAS, "myFile");

      assertCall(() -> filesystem.sink(file)).throwsException(IOException.class);
    }

    @Test
    void fails_when_path_is_a_directory() throws Exception {
      var filesystem = filesystem();
      var dir = fullPath(alias(), "dir");
      filesystem.createDir(dir);
      assertCall(() -> filesystem.sink(dir)).throwsException(IOException.class);
    }

    @Test
    void fails_when_parent_is_link_targeting_file() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");
      createFile(filesystem, file);
      filesystem.createLink(link, file);

      assertCall(() -> filesystem.sink(link.appendPart("newFile")))
          .throwsException(IOException.class);
    }

    @Test
    void succeeds_when_parent_is_link_targeting_directory() throws Exception {
      var filesystem = filesystem();
      var dir = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");
      filesystem.createDir(dir);
      filesystem.createLink(link, dir);
      var newFile = link.appendPart("newFile");
      var content = byteString();

      writeFile(filesystem, newFile, content);

      assertThat(readFile(filesystem, newFile)).isEqualTo(content);
    }

    @Test
    void fails_when_parent_exists_and_is_a_file() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "dir/myFile");

      createFile(filesystem, file);
      var path = file.append(path("otherFile"));
      assertCall(() -> writeFile(filesystem, path))
          .throwsException(new IOException("Error writing file '{alias-1}/dir/myFile/otherFile'. "
              + "Cannot create object because its parent 'dir/myFile' exists and is a file."));
    }
  }

  @Nested
  class _create_link {
    @Test
    void fails_when_link_parent_directory_not_exists() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "missing_directory/myLink");

      createFile(filesystem, file);

      assertCall(() -> filesystem.createLink(link, file))
          .throwsException(
              new IOException("Error creating link '{alias-1}/missing_directory/myLink' ->"
                  + " '{alias-1}/some/dir/myFile'. 'missing_directory'"));
    }

    @Test
    void fails_when_link_path_is_taken_by_file() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");

      createFile(filesystem, file);
      createFile(filesystem, link);

      assertCall(() -> filesystem.createLink(link, file))
          .throwsException(new IOException(
              "Error creating link '{alias-1}/myLink' -> '{alias-1}/some/dir/myFile'. "
                  + "Cannot use 'myLink' path. It is already taken."));
    }

    @Test
    void fails_when_link_path_is_taken_by_dir() throws Exception {
      var filesystem = filesystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");
      createFile(filesystem, file);
      filesystem.createDir(link);

      assertCall(() -> filesystem.createLink(link, file))
          .throwsException(new IOException(
              "Error creating link '{alias-1}/myLink' -> '{alias-1}/some/dir/myFile'. "
                  + "Cannot use 'myLink' path. It is already taken."));
    }

    @Test
    void fails_when_link_bucket_and_target_bucket_are_different() {
      var filesystem = filesystem();
      var link = fullPath(UNKNOWN_ALIAS, "source");
      var target = fullPath(alias(), "target");
      assertCall(() -> filesystem.createLink(link, target))
          .throwsException(new IllegalArgumentException(
              "Alias 'unknown' in source is different from alias 'alias-1' in target."));
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var link = fullPath(UNKNOWN_ALIAS, "source");
      var target = fullPath(UNKNOWN_ALIAS, "target");
      assertCall(() -> filesystem.createLink(link, target))
          .throwsException(new IOException("Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }
  }

  @Nested
  class _create_dir {
    @Test
    void creates_directory() throws Exception {
      var filesystem = filesystem();
      var path = fullPath(alias(), "dir/subdir");

      filesystem.createDir(path);

      assertThat(filesystem.pathState(path)).isEqualTo(DIR);
    }

    @Test
    void not_fails_when_directory_exists() throws Exception {
      var filesystem = filesystem();
      var path = fullPath(alias(), "dir/subdir");

      filesystem.createDir(path);
      filesystem.createDir(path);

      assertThat(filesystem.pathState(path)).isEqualTo(DIR);
    }

    @Test
    void fails_when_file_at_given_path_exists() throws Exception {
      var filesystem = filesystem();
      var path = fullPath(alias(), "dir/file");

      createFile(filesystem, path);

      assertCall(() -> filesystem.createDir(path))
          .throwsException(new IOException("Error creating dir '{alias-1}/dir/file'. "
              + "Cannot use dir/file path. It is already taken by file."));
    }

    @Test
    void fails_when_bucket_not_exists() {
      var filesystem = filesystem();
      var path = fullPath(UNKNOWN_ALIAS, "dir/subdir");

      assertCall(() -> filesystem.createDir(path))
          .throwsException(new IOException("Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }
  }

  private static FullFileSystem filesystem() {
    return new FullFileSystem(map(alias(), new MemoryBucket()));
  }

  private static Alias alias() {
    return new Alias("alias-1");
  }
}
