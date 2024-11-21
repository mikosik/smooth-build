package org.smoothbuild.common.filesystem.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.collect.Set.setOfAll;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.filesystem.base.PathState.DIR;
import static org.smoothbuild.common.filesystem.base.PathState.FILE;
import static org.smoothbuild.common.filesystem.base.PathState.NOTHING;
import static org.smoothbuild.common.testing.TestingByteString.byteString;
import static org.smoothbuild.common.testing.TestingFileSystem.createFile;
import static org.smoothbuild.common.testing.TestingFileSystem.readFile;
import static org.smoothbuild.common.testing.TestingFileSystem.writeFile;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.HashSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Set;

public abstract class AbstractFullFileSystemTest {
  protected FileSystem<FullPath> fileSystem() {
    return fileSystem(set(alias()));
  }

  protected abstract FileSystem<FullPath> fileSystem(Set<Alias> aliases);

  @Nested
  class _path_state {
    @Test
    void returns_unknown_when_file_not_exists() throws IOException {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), "file");
      assertThat(fileSystem.pathState(path)).isEqualTo(NOTHING);
    }

    @Test
    void return_file_when_path_points_to_a_file() throws IOException {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), "file");
      createFile(fileSystem, path);
      assertThat(fileSystem.pathState(path)).isEqualTo(FILE);
    }

    @Test
    void returns_dir_when_path_points_to_a_dir() throws IOException {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), "dir");
      fileSystem.createDir(path);
      assertThat(fileSystem.pathState(path)).isEqualTo(DIR);
    }

    @Test
    void fails_when_alias_is_unknown() {
      var fileSystem = fileSystem();
      var path = fullPath(unknown(), "file");
      assertCall(() -> fileSystem.pathState(path)).throwsException(IOException.class);
    }
  }

  @Nested
  class _files_recursively {
    @Test
    void returns_all_files() throws IOException {
      var fileSystem = fileSystem();
      createFile(fileSystem, fullPath(alias(), "file"));
      createFile(fileSystem, fullPath(alias(), "dir/file"));
      createFile(fileSystem, fullPath(alias(), "dir/file2"));
      createFile(fileSystem, fullPath(alias(), "dir/subdir/file3"));

      var files = toSet(fileSystem.filesRecursively(fullPath(alias(), "dir")));
      assertThat(files).isEqualTo(set(path("file"), path("file2"), path("subdir/file3")));
    }

    @Test
    void returns_nothing_for_empty_dir() throws IOException {
      var fileSystem = fileSystem();
      createFile(fileSystem, fullPath(alias(), "file"));
      fileSystem.createDir(fullPath(alias(), "dir"));

      var files = toSet(fileSystem.filesRecursively(fullPath(alias(), "dir")));
      assertThat(files).isEqualTo(set());
    }

    @Test
    void fails_when_dir_not_exists() throws IOException {
      var fileSystem = fileSystem();
      createFile(fileSystem, fullPath(alias(), "file"));

      assertCall(() -> fileSystem.filesRecursively(fullPath(alias(), "dir")))
          .throwsException(IOException.class);
    }

    @Test
    void fails_for_unknown_alias() {
      var fileSystem = fileSystem();

      assertCall(() -> fileSystem.filesRecursively(fullPath(unknown(), "file")))
          .throwsException(new IOException("Cannot list files recursively in '{unknown}/file'. "
              + "Unknown alias 'unknown'. Known aliases = ['alias-1']"));
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
      var fileSystem = fileSystem();
      var path = fullPath(alias(), path("abc"));
      assertCall(() -> fileSystem.files(path))
          .throwsException(
              new IOException("Error listing files in '{alias-1}/abc'. Dir 'abc' doesn't exist."));
    }

    @Test
    void fails_when_alias_is_unknown() {
      var fileSystem = fileSystem();
      var path = fullPath(unknown(), path("abc"));
      assertCall(() -> fileSystem.files(path))
          .throwsException(
              new IOException(
                  "Cannot list files in '{unknown}/abc'. Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }

    @Test
    void fails_when_path_is_a_file() throws Exception {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), path("some/dir/myFile"));
      createFile(fileSystem, path);
      assertCall(() -> fileSystem.files(path))
          .throwsException(new IOException("Error listing files in '{alias-1}/some/dir/myFile'. "
              + "Dir 'some/dir/myFile' doesn't exist. It is a file."));
    }

    @Test
    void returns_all_children() throws Exception {
      var fileSystem = fileSystem();
      createFile(fileSystem, fullPath(alias(), "file"));
      createFile(fileSystem, fullPath(alias(), "dir/file"));
      createFile(fileSystem, fullPath(alias(), "dir/file2"));
      createFile(fileSystem, fullPath(alias(), "dir/subdir/file3"));

      var files = setOfAll(fileSystem.files(fullPath(alias(), "dir")));
      assertThat(files).containsExactly(path("file"), path("file2"), path("subdir"));
    }
  }

  @Nested
  class _move {
    @Test
    void fails_when_source_not_exists() {
      var fileSystem = fileSystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      assertCall(() -> fileSystem.move(source, target))
          .throwsException(new IOException("Cannot move '{alias-1}/source' to '{alias-1}/target'. "
              + "Cannot move 'source'. It doesn't exist."));
    }

    @Test
    void fails_when_source_alias_and_target_alias_are_different() {
      var fileSystem = fileSystem(set(alias(), alias()));
      var source = fullPath(alias(), "source");
      var target = fullPath(alias2(), "target");
      assertCall(() -> fileSystem.move(source, target))
          .throwsException(new IOException(
              "Alias 'alias-1' in source is different from alias 'alias-2' in target."));
    }

    @Test
    void fails_when_alias_is_unknown() {
      var fileSystem = fileSystem();
      var source = fullPath(unknown(), "source");
      var target = fullPath(unknown(), "target");
      assertCall(() -> fileSystem.move(source, target))
          .throwsException(
              new IOException(
                  "Cannot move '{unknown}/source' to '{unknown}/target'. Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }

    @Test
    void fails_when_source_is_a_dir() throws Exception {
      var fileSystem = fileSystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      fileSystem.createDir(source);

      assertCall(() -> fileSystem.move(source, target))
          .throwsException(new IOException("Cannot move '{alias-1}/source' to '{alias-1}/target'. "
              + "Cannot move 'source'. It is directory."));
    }

    @Test
    void fails_when_target_is_a_dir() throws IOException {
      var fileSystem = fileSystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      fileSystem.createDir(target);
      assertCall(() -> fileSystem.move(source, target))
          .throwsException(new IOException("Cannot move '{alias-1}/source' to '{alias-1}/target'. "
              + "Cannot move 'source'. It doesn't exist."));
    }

    @Test
    void deletes_source_file() throws Exception {
      var fileSystem = fileSystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      createFile(fileSystem, source);

      fileSystem.move(source, target);

      assertThat(fileSystem.pathState(source)).isEqualTo(NOTHING);
    }

    @Test
    void copies_file_content_to_target() throws Exception {
      var fileSystem = fileSystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      createFile(fileSystem, source, "abc");

      fileSystem.move(source, target);

      assertThat(readFile(fileSystem, target)).isEqualTo(byteString("abc"));
    }

    @Test
    void overwrites_target_file() throws Exception {
      var fileSystem = fileSystem();
      var source = fullPath(alias(), "source");
      var target = fullPath(alias(), "target");
      createFile(fileSystem, source, "abc");
      createFile(fileSystem, target, "def");

      fileSystem.move(source, target);

      assertThat(readFile(fileSystem, target)).isEqualTo(byteString("abc"));
    }
  }

  @Nested
  class _delete {
    @Test
    void directory_removes_it_and_its_files_recursively() throws Exception {
      var fileSystem = fileSystem();
      var dir = fullPath(alias(), path("some/dir"));
      var file1 = dir.append(path("myFile"));
      var file2 = dir.append(path("dir2/myFile"));
      createFile(fileSystem, file1);

      fileSystem.delete(dir);

      assertThat(fileSystem.pathState(file1)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(file2)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(dir)).isEqualTo(NOTHING);
    }

    @Test
    void file_removes_it() throws Exception {
      var fileSystem = fileSystem();
      var dir = fullPath(alias(), path("some/dir"));
      var file = dir.append(path("myFile"));
      createFile(fileSystem, file);

      fileSystem.delete(file);

      assertThat(fileSystem.pathState(file)).isEqualTo(NOTHING);
    }

    @Test
    void not_fails_when_path_not_exists() throws Exception {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), path("file"));

      fileSystem.delete(path);

      assertThat(fileSystem.pathState(path)).isEqualTo(NOTHING);
    }

    @Test
    void fails_when_alias_is_unknown() {
      var fileSystem = fileSystem();
      var path = fullPath(unknown(), path("file"));

      assertCall(() -> fileSystem.delete(path))
          .throwsException(
              new IOException(
                  "Cannot delete '{unknown}/file'. Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }

    @Test
    void root_path_removes_all_files() throws Exception {
      var fileSystem = fileSystem();
      var file1 = fullPath(alias(), path("some/dir/myFile"));
      var file2 = fullPath(alias(), path("other/dir/otherFile"));
      createFile(fileSystem, file1);
      createFile(fileSystem, file2);

      fileSystem.delete(fullPath(alias(), Path.root()));

      assertThat(fileSystem.pathState(file1)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(file2)).isEqualTo(NOTHING);
    }

    @Test
    void link_removes_it_but_not_target_file() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), path("some/dir/myFile"));
      var link = fullPath(alias(), path("myLink"));
      createFile(fileSystem, file);
      fileSystem.createLink(link, file);

      fileSystem.delete(link);

      assertThat(fileSystem.pathState(link)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(file)).isEqualTo(FILE);
    }

    @Test
    void link_to_directory_not_removes_target_directory_nor_file_it_contains() throws Exception {
      var fileSystem = fileSystem();
      var dir = fullPath(alias(), path("some/dir"));
      var file = dir.append("myFile");
      var link = fullPath(alias(), path("myLink"));
      createFile(fileSystem, file);
      fileSystem.createLink(link, dir);

      fileSystem.delete(link);

      assertThat(fileSystem.pathState(link)).isEqualTo(NOTHING);
      assertThat(fileSystem.pathState(dir)).isEqualTo(DIR);
      assertThat(fileSystem.pathState(file)).isEqualTo(FILE);
    }
  }

  @Nested
  class _size {
    @Test
    void returns_zero_for_empty_file() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "file");
      createFile(fileSystem, file);

      assertThat(fileSystem.size(file)).isEqualTo(0);
    }

    @Test
    void returns_file_size() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "file");
      createFile(fileSystem, file, "abc");

      assertThat(fileSystem.size(file)).isEqualTo(3);
    }

    @Test
    void reading_size_of_dir_causes_exception() throws Exception {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), "dir");
      fileSystem.createDir(path);

      assertCall(() -> fileSystem.size(path))
          .throwsException(new IOException(
              "Cannot fetch size of '{alias-1}/dir'. File 'dir' doesn't exist. It is a dir."));
    }

    @Test
    void fails_when_path_not_exists() {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), "dir");
      assertCall(() -> fileSystem.size(path)).throwsException(IOException.class);
    }

    @Test
    void fails_when_alias_is_unknown() {
      var fileSystem = fileSystem();
      var path = fullPath(unknown(), "dir");
      assertCall(() -> fileSystem.size(path)).throwsException(IOException.class);
    }

    @Test
    void returns_size_of_target_file_for_link() throws IOException {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), path("some/dir/myFile"));
      var link = fullPath(alias(), path("myLink"));
      createFile(fileSystem, file, "abc");
      fileSystem.createLink(link, file);

      assertThat(fileSystem.size(link)).isEqualTo(3);
    }

    @Test
    void reading_size_of_link_that_targets_dir_causes_exception() throws IOException {
      var fileSystem = fileSystem();
      var dir = fullPath(alias(), path("some/dir"));
      var link = fullPath(alias(), path("myLink"));
      fileSystem.createDir(dir);
      fileSystem.createLink(link, dir);

      assertCall(() -> fileSystem.size(dir))
          .throwsException(new IOException("Cannot fetch size of '{alias-1}/some/dir'. "
              + "File 'some/dir' doesn't exist. It is a dir."));
    }
  }

  @Nested
  class _source {
    @Test
    void provides_content_of_file() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "file");
      createFile(fileSystem, file, "abc");

      assertThat(readFile(fileSystem, file)).isEqualTo(byteString("abc"));
    }

    @Test
    void provides_content_of_target_file_for_a_link() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "file");
      var link = fullPath(alias(), "myLink");
      createFile(fileSystem, file, "abc");
      fileSystem.createLink(link, file);

      assertThat(readFile(fileSystem, link)).isEqualTo(byteString("abc"));
    }

    @Test
    void provides_file_content_of_file_when_one_part_of_path_is_link_to_directory()
        throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");
      createFile(fileSystem, file, "abc");
      fileSystem.createLink(link, file.parent());

      assertThat(readFile(fileSystem, link.append(file.path().lastPart()))).isEqualTo(byteString());
    }

    @Test
    void fails_when_file_not_exists() {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "myFile");
      assertCall(() -> readFile(fileSystem, file))
          .throwsException(
              new IOException("Cannot read '{alias-1}/myFile'. File 'myFile' doesn't exist."));
    }

    @Test
    void fails_when_alias_is_unknown() {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "myFile");
      assertCall(() -> readFile(fileSystem, file))
          .throwsException(
              new IOException("Cannot read '{alias-1}/myFile'. File 'myFile' doesn't exist."));
    }

    @Test
    void fails_when_path_is_dir() throws Exception {
      var fileSystem = fileSystem();
      var dir = fullPath(alias(), "dir");
      fileSystem.createDir(dir);
      assertCall(() -> readFile(fileSystem, dir))
          .throwsException(new IOException(
              "Cannot read '{alias-1}/dir'. File 'dir' doesn't exist. It is a dir."));
    }
  }

  @Nested
  class _sink {
    @Test
    void data_written_by_sink_can_be_read_by_source() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "myFile");
      var content = byteString("abc");

      writeFile(fileSystem, file, content);
      assertThat(readFile(fileSystem, file)).isEqualTo(content);
    }

    @Test
    void data_written_to_sink_overwrites_existing_file() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "myFile");
      var content = byteString("abc");
      var content2 = byteString("def");

      writeFile(fileSystem, file, content);
      writeFile(fileSystem, file, content2);
      assertThat(readFile(fileSystem, file)).isEqualTo(content2);
    }

    @Test
    void fails_when_parent_directory_not_exists() {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "dir/myFile");

      assertCall(() -> fileSystem.sink(file))
          .throwsException(
              new IOException("Cannot create sink for '{alias-1}/dir/myFile'. No such dir 'dir'."));
    }

    @Test
    void fails_when_alias_is_unknown() {
      var fileSystem = fileSystem();
      var file = fullPath(unknown(), "myFile");

      assertCall(() -> fileSystem.sink(file)).throwsException(IOException.class);
    }

    @Test
    void fails_when_path_is_a_directory() throws Exception {
      var fileSystem = fileSystem();
      var dir = fullPath(alias(), "dir");
      fileSystem.createDir(dir);
      assertCall(() -> fileSystem.sink(dir)).throwsException(IOException.class);
    }

    @Test
    void fails_when_parent_is_link_targeting_file() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");
      createFile(fileSystem, file);
      fileSystem.createLink(link, file);

      assertCall(() -> fileSystem.sink(link.appendPart("newFile")))
          .throwsException(IOException.class);
    }

    @Test
    void succeeds_when_parent_is_link_targeting_directory() throws Exception {
      var fileSystem = fileSystem();
      var dir = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");
      fileSystem.createDir(dir);
      fileSystem.createLink(link, dir);
      var newFile = link.appendPart("newFile");
      var content = byteString();

      writeFile(fileSystem, newFile, content);

      assertThat(readFile(fileSystem, newFile)).isEqualTo(content);
    }

    @Test
    void fails_when_parent_exists_and_is_a_file() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "dir/myFile");

      createFile(fileSystem, file);
      var path = file.append(path("otherFile"));
      assertCall(() -> writeFile(fileSystem, path))
          .throwsException(
              new IOException("Cannot create sink for '{alias-1}/dir/myFile/otherFile'. "
                  + "One of parents exists and is a file."));
    }
  }

  @Nested
  class _create_link {
    @Test
    void fails_when_link_parent_directory_not_exists() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "missing_directory/myLink");

      createFile(fileSystem, file);

      assertCall(() -> fileSystem.createLink(link, file))
          .throwsException(
              new IOException("Cannot create link '{alias-1}/missing_directory/myLink' ->"
                  + " '{alias-1}/some/dir/myFile'. No such dir 'missing_directory'."));
    }

    @Test
    void fails_when_link_path_is_taken_by_file() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");

      createFile(fileSystem, file);
      createFile(fileSystem, link);

      assertCall(() -> fileSystem.createLink(link, file))
          .throwsException(new IOException(
              "Cannot create link '{alias-1}/myLink' -> '{alias-1}/some/dir/myFile'. "
                  + "Cannot use 'myLink' path. It is already taken."));
    }

    @Test
    void fails_when_link_path_is_taken_by_dir() throws Exception {
      var fileSystem = fileSystem();
      var file = fullPath(alias(), "some/dir/myFile");
      var link = fullPath(alias(), "myLink");
      createFile(fileSystem, file);
      fileSystem.createDir(link);

      assertCall(() -> fileSystem.createLink(link, file))
          .throwsException(new IOException(
              "Cannot create link '{alias-1}/myLink' -> '{alias-1}/some/dir/myFile'. "
                  + "Cannot use 'myLink' path. It is already taken."));
    }

    @Test
    void fails_when_link_alias_and_target_alias_are_different() {
      var fileSystem = fileSystem();
      var link = fullPath(unknown(), "source");
      var target = fullPath(alias(), "target");
      assertCall(() -> fileSystem.createLink(link, target))
          .throwsException(new IOException(
              "Alias 'unknown' in source is different from alias 'alias-1' in target."));
    }

    @Test
    void fails_when_alias_not_exists() {
      var fileSystem = fileSystem();
      var link = fullPath(unknown(), "source");
      var target = fullPath(unknown(), "target");
      assertCall(() -> fileSystem.createLink(link, target))
          .throwsException(
              new IOException("Cannot create link '{unknown}/source' -> '{unknown}/target'."
                  + " Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }
  }

  @Nested
  class _create_dir {
    @Test
    void creates_directory() throws Exception {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), "dir/subdir");

      fileSystem.createDir(path);

      assertThat(fileSystem.pathState(path)).isEqualTo(DIR);
    }

    @Test
    void not_fails_when_directory_exists() throws Exception {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), "dir/subdir");

      fileSystem.createDir(path);
      fileSystem.createDir(path);

      assertThat(fileSystem.pathState(path)).isEqualTo(DIR);
    }

    @Test
    void fails_when_file_at_given_path_exists() throws Exception {
      var fileSystem = fileSystem();
      var path = fullPath(alias(), "dir/file");

      createFile(fileSystem, path);

      assertCall(() -> fileSystem.createDir(path))
          .throwsException(new IOException("Cannot create dir '{alias-1}/dir/file'. "
              + "Cannot use 'dir/file'. It is already taken by file."));
    }

    @Test
    void fails_when_alias_is_unknown() {
      var fileSystem = fileSystem();
      var path = fullPath(unknown(), "dir");

      assertCall(() -> fileSystem.createDir(path))
          .throwsException(
              new IOException(
                  "Cannot create dir '{unknown}/dir'. Unknown alias 'unknown'. Known aliases = ['alias-1']"));
    }
  }

  protected Alias alias() {
    return new Alias("alias-1");
  }

  protected Alias alias2() {
    return new Alias("alias-2");
  }

  protected static Alias unknown() {
    return new Alias("unknown");
  }
}
