 package org.smoothbuild.common.filesystem.base;

 import static com.google.common.truth.Truth.assertThat;
 import static org.smoothbuild.common.filesystem.base.PathS.path;
 import static org.smoothbuild.common.filesystem.base.PathState.DIR;
 import static org.smoothbuild.common.filesystem.base.PathState.FILE;
 import static org.smoothbuild.common.filesystem.base.PathState.NOTHING;
 import static org.smoothbuild.testing.common.AssertCall.assertCall;

 import java.io.IOException;
 import java.nio.file.FileAlreadyExistsException;

 import org.junit.jupiter.api.Test;

 import okio.BufferedSink;
 import okio.ByteString;

public abstract class AbstractFileSystemTestSuite {
  protected FileSystem fileSystem;

  // pathState()

  @Test
  public void root_path_is_a_dir() {
    assertThat(fileSystem.pathState(PathS.root()))
        .isEqualTo(DIR);
  }

  @Test
  public void non_root_path_are_initially_nothing_state() {
    assertThat(fileSystem.pathState(path("abc")))
        .isEqualTo(NOTHING);
  }

  @Test
  public void file_path_has_file_state() throws Exception {
    var path = path("some/dir/myFile");
    createEmptyFile(path);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(FILE);
  }

  @Test
  public void dir_path_has_dir_state() throws Exception {
    var dir = path("my/dir");
    createEmptyFile(dir.append(path("some/dir/myFile")));
    assertThat(fileSystem.pathState(dir))
        .isEqualTo(DIR);
  }

  @Test
  public void path_state_is_nothing_even_when_its_first_part_is_a_file() throws Exception {
    var file = path("some/dir/myFile");
    createEmptyFile(file);
    assertThat(fileSystem.pathState(file.appendPart("something")))
        .isEqualTo(NOTHING);
  }

  @Test
  public void creating_file_twice_is_possible() throws Exception {
    var file = path("some/dir/myFile");
    try (BufferedSink sink = fileSystem.sink(file)) {
      sink.write(ByteString.of());
    }
    try (BufferedSink sink = fileSystem.sink(file)) {
      sink.write(ByteString.of());
    }
    assertThat(fileSystem.pathState(file))
        .isEqualTo(FILE);
  }

  // files()

  @Test
  public void files_throws_exception_when_dir_does_not_exist() {
    assertCall(() -> fileSystem.files(path("abc")))
        .throwsException(new IOException("Dir 'abc' doesn't exist."));
  }

  @Test
  public void files_throws_exception_when_path_is_a_file() throws Exception {
    var file = path("some/dir/myFile");
    createEmptyFile(file);
    assertCall(() -> fileSystem.files(file))
        .throwsException(new IOException("Dir " + file.q() + " doesn't exist. It is a file."));
  }

  @Test
  public void files_returns_all_children() throws Exception {
    createEmptyFile(path("abc/dir1/file1.txt"));
    createEmptyFile(path("abc/dir2/file2.txt"));
    createEmptyFile(path("abc/text.txt"));
    assertThat(fileSystem.files(path("abc")))
        .containsExactly(path("dir1"), path("dir2"), path("text.txt"));
  }

  @Test
  public void files_throws_exception_when_path_does_not_exist() {
    assertCall(() -> fileSystem.files(path("abc")))
        .throwsException(IOException.class);
  }

  // size

  @Test
  public void empty_file_has_zero_size() throws Exception {
    var file = path("some/dir/myFile");
    createEmptyFile(file);
    assertThat(fileSystem.size(file))
        .isEqualTo(0);
  }

  @Test
  public void file_with_non_zero_size() throws Exception {
    var file = path("some/dir/myFile");
    createFile(file, byteString());
    assertThat(fileSystem.size(file))
        .isEqualTo(3);
  }

  @Test
  public void reading_size_of_dir_causes_exception() throws Exception {
    var dir = path("my/dir");
    var file = dir.append(path("some/dir/myFile"));
    createEmptyFile(file);
    assertCall(() -> fileSystem.size(dir))
        .throwsException(new IOException("File 'my/dir' doesn't exist. It is a dir."));
  }

  @Test
  public void reading_size_of_nothing_causes_exception() {
    var dir = path("my/dir");
    assertCall(() -> fileSystem.size(dir))
        .throwsException(new IOException("File 'my/dir' doesn't exist."));
  }

  @Test
  public void reading_size_of_link_returns_size_of_target_file() throws IOException {
    var file = path("some/dir/myFile");
    var link = path("my/link");
    createFile(file, byteString());

    fileSystem.createLink(link, file);

    assertThat(fileSystem.size(link))
        .isEqualTo(byteString().size());
  }

  @Test
  public void reading_size_of_link_that_targets_dir_causes_exception() throws IOException {
    var dir = path("my/dir");
    var file = dir.append(path("some/dir/myFile"));
    var link = path("my/link");
    createEmptyFile(file);

    fileSystem.createLink(link, dir);

    assertCall(() -> fileSystem.size(dir))
        .throwsException(new IOException("File 'my/dir' doesn't exist. It is a dir."));
  }

  // source()

  @Test
  public void source_reads_file_content() throws Exception {
    var file = path("some/dir/myFile");
    createFile(file, byteString());
    assertThat(fileSystem.source(file).readByteString())
        .isEqualTo(byteString());
  }

  @Test
  public void source_throws_exception_when_file_does_not_exist() {
    var file = path("dir/file");
    assertCall(() -> fileSystem.source(file))
        .throwsException(new IOException("File 'dir/file' doesn't exist."));
  }

  @Test
  public void source_throws_exception_when_path_is_dir() throws Exception {
    var file = path("some/dir/myFile");
    createEmptyFile(file);
    assertCall(() -> fileSystem.source(file.parent()))
        .throwsException(new IOException("File 'some/dir' doesn't exist. It is a dir."));
  }

  @Test
  public void source_throws_exception_when_path_is_root_dir() {
    assertCall(() -> fileSystem.source(PathS.root()))
        .throwsException(new IOException("File '.' doesn't exist. It is a dir."));
  }

  // sink()

  @Test
  public void data_written_via_sink_can_be_read_by_source() throws Exception {
    var file = path("some/dir/myFile");
    try (BufferedSink sink = fileSystem.sink(file)) {
      sink.write(byteString());
    }
    assertThat(fileSystem.source(file).readByteString())
        .isEqualTo(byteString());
  }

  @Test
  public void sink_overwrites_existing_file() throws Exception {
    var file = path("some/dir/myFile");
    try (BufferedSink sink = fileSystem.sink(file)) {
      sink.write(ByteString.encodeUtf8("abc"));
    }
    try (BufferedSink sink = fileSystem.sink(file)) {
      sink.write(ByteString.encodeUtf8("def"));
    }
    assertThat(fileSystem.source(file).readByteString())
        .isEqualTo(ByteString.encodeUtf8("def"));
  }

  @Test
  public void sink_fails_when_target_file_is_a_dir() throws Exception {
    var dir = path("my/dir");
    var file = dir.append(path("some/dir/myFile"));
    createEmptyFile(file);
    assertCall(() -> fileSystem.sink(dir))
        .throwsException(IOException.class);
  }

  // move()

  @Test
  public void moving_nonexistent_file_fails() {
    var source = path("source");
    var target = path("target");
    assertCall(() -> fileSystem.move(source, target))
        .throwsException(new IOException("Cannot move 'source'. It doesn't exist."));
  }

  @Test
  public void moving_directory_fails() throws Exception {
    var dir = path("source");
    var source = dir.appendPart("file");
    var target = path("target");
    createEmptyFile(source);
    assertCall(() -> fileSystem.move(dir, target))
        .throwsException(new IOException("Cannot move 'source'. It is directory."));
  }

  @Test
  public void moving_to_directory_fails() throws Exception {
    var source = path("source");
    var dir = path("dir");
    createEmptyFile(source);
    createEmptyFile(path("dir/file"));
    assertCall(() -> fileSystem.move(source, dir))
        .throwsException(new IOException(
            "Cannot move to 'dir'. It is directory."));
  }

  @Test
  public void moved_file_is_deleted_from_source() throws Exception {
    var source = path("source");
    var target = path("target");
    createEmptyFile(source);

    fileSystem.move(source, target);

    assertThat(fileSystem.pathState(source))
        .isEqualTo(NOTHING);
  }

  @Test
  public void moved_file_is_copied_to_target() throws Exception {
    var source = path("source");
    var target = path("target");
    createFile(source, byteString());

    fileSystem.move(source, target);

    assertThat(fileSystem.pathState(source))
        .isEqualTo(NOTHING);
    assertThat(fileSystem.source(target).readByteString())
        .isEqualTo(byteString());
  }

  @Test
  public void moved_file_overwrites_target_file() throws Exception {
    var source = path("source");
    var target = path("target");
    createFile(source, byteString());
    createEmptyFile(target);

    fileSystem.move(source, target);

    assertThat(fileSystem.pathState(source))
        .isEqualTo(NOTHING);
    assertThat(fileSystem.source(target).readByteString())
        .isEqualTo(byteString());
  }

  @Test
  public void moving_creates_missing_parent_directories_in_target_path() throws Exception {
    var source = path("source");
    var target = path("dir/target");
    createFile(source, byteString());

    fileSystem.move(source, target);

    assertThat(fileSystem.source(target).readByteString())
        .isEqualTo(byteString());
  }

  // delete()

  @Test
  public void deleting_dir_removes_its_files() throws Exception {
    var file = path("some/dir/myFile");
    createEmptyFile(file);

    fileSystem.delete(file.parent());

    assertThat(fileSystem.pathState(file))
        .isEqualTo(NOTHING);
  }

  @Test
  public void delete_file() throws Exception {
    var file = path("some/dir/myFile");
    createEmptyFile(file);

    fileSystem.delete(file);

    assertThat(fileSystem.pathState(file))
        .isEqualTo(NOTHING);
  }

  @Test
  public void delete_does_nothing_for_nonexistet_path() throws Exception {
    var path = path("some/dir/myFile");

    fileSystem.delete(path);

    assertThat(fileSystem.pathState(path))
        .isEqualTo(NOTHING);
  }

  @Test
  public void deleting_root_path_removes_all_files() throws Exception {
    var file = path("some/dir/myFile");
    var file2 = path("other/dir/otherFile");
    createEmptyFile(file);
    createEmptyFile(file2);

    fileSystem.delete(PathS.root());

    assertThat(fileSystem.pathState(file))
        .isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(file2))
        .isEqualTo(NOTHING);
  }

  // links

  @Test
  public void link_contains_data_from_target() throws Exception {
    var file = path("some/dir/myFile");
    var link = path("my/link");
    createFile(file, byteString());

    fileSystem.createLink(link, file);

    assertThat(fileSystem.source(link).readByteString())
        .isEqualTo(byteString());
  }

  @Test
  public void creating_links_creates_missing_dirs() throws Exception {
    var file = path("some/dir/myFile");
    var link = path("my/link");
    createFile(file, byteString());

    fileSystem.createLink(link, file);

    assertThat(fileSystem.pathState(link))
        .isEqualTo(FILE);
  }

  @Test
  public void deleted_link_is_removed() throws Exception {
    var file = path("some/dir/myFile");
    var link = path("my/link");
    createFile(file, byteString());
    fileSystem.createLink(link, file);

    fileSystem.delete(link);

    assertThat(fileSystem.pathState(link))
        .isEqualTo(NOTHING);
  }

  @Test
  public void deleting_link_to_file_does_not_delete_target() throws Exception {
    var file = path("some/dir/myFile");
    var link = path("my/link");
    createFile(file, byteString());
    fileSystem.createLink(link, file);

    fileSystem.delete(link);

    assertThat(fileSystem.pathState(file))
        .isEqualTo(FILE);
  }

  @Test
  public void link_to_dir_can_be_used_to_access_its_file() throws Exception {
    var file = path("some/dir/myFile");
    var link = path("my/link");
    createFile(file, byteString());

    fileSystem.createLink(link, file.parent());

    assertThat(fileSystem.source(link.append(file.lastPart())).readByteString())
        .isEqualTo(byteString());
  }

  @Test
  public void deleting_link_to_dir_does_not_delete_target() throws Exception {
    var dir = path("my/dir");
    var link = path("some/dir/myFile");
    createEmptyFile(dir.appendPart("ignore"));
    fileSystem.createLink(link, dir);

    fileSystem.delete(link);

    assertThat(fileSystem.pathState(link))
        .isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(dir))
        .isEqualTo(DIR);
  }

  @Test
  public void cannot_create_link_when_path_is_taken_by_file() throws Exception {
    var file = path("some/dir/myFile");
    var link = path("my/link");
    createEmptyFile(file);
    createEmptyFile(link);

    assertCall(() -> fileSystem.createLink(link, file))
        .throwsException(new IOException("Cannot use " + link + " path. It is already taken."));
  }

  @Test
  public void cannot_create_link_when_path_is_taken_by_dir() throws Exception {
    var file = path("some/dir/myFile");
    var link = path("my/link");
    createEmptyFile(file);
    createEmptyFile(link);

    assertCall(() -> fileSystem.createLink(link.parent(), file))
        .throwsException(new IOException(
            "Cannot use " + link.parent() + " path. It is already taken."));
  }

  // createDir()

  @Test
  public void created_dir_exists() throws Exception {
    var file = path("some/dir/myFile");
    fileSystem.createDir(file);
    assertThat(fileSystem.pathState(file))
        .isEqualTo(DIR);
  }

  @Test
  public void creating_existing_dir_does_not_cause_errors() throws Exception {
    var file = path("some/dir/myFile");
    fileSystem.createDir(file);
    fileSystem.createDir(file);
    assertThat(fileSystem.pathState(file))
        .isEqualTo(DIR);
  }

  @Test
  public void cannot_create_dir_if_such_file_already_exists() throws Exception {
    var file = path("some/dir/myFile");
    createEmptyFile(file);
    assertCall(() -> fileSystem.createDir(file))
        .throwsException(FileAlreadyExistsException.class);
  }

  // helpers

  protected void createEmptyFile(PathS path) throws IOException {
    createFile(path, ByteString.of());
  }

  private static ByteString byteString() {
    return ByteString.encodeUtf8("abc");
  }

  protected abstract void createFile(PathS path, ByteString content) throws IOException;
}
