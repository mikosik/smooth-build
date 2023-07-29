package org.smoothbuild.common.fs.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.fs.base.PathState.DIR;
import static org.smoothbuild.common.fs.base.PathState.FILE;
import static org.smoothbuild.common.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import org.junit.jupiter.api.Test;

import okio.BufferedSink;
import okio.ByteString;

public abstract class AbstractFileSystemTestSuite {
  private static final ByteString bytes = ByteString.encodeUtf8("abc");
  protected FileSystem fileSystem;
  protected PathS path = PathS.path("some/dir/myFile");
  protected PathS path2 = PathS.path("other/dir/otherFile");

  private final PathS dir = PathS.path("my/dir");
  private final PathS linkPath = PathS.path("my/link");

  // pathKind()

  @Test
  public void root_path_is_a_dir() {
    assertThat(fileSystem.pathState(PathS.root()))
        .isEqualTo(DIR);
  }

  @Test
  public void non_root_path_are_initially_nothing_state() {
    assertThat(fileSystem.pathState(PathS.path("abc")))
        .isEqualTo(NOTHING);
  }

  @Test
  public void file_path_has_file_state() throws Exception {
    createEmptyFile(path);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(FILE);
  }

  @Test
  public void dir_path_has_dir_state() throws Exception {
    createEmptyFile(dir.append(path));
    assertThat(fileSystem.pathState(dir))
        .isEqualTo(DIR);
  }

  @Test
  public void path_state_is_nothing_even_when_its_first_part_is_a_file() throws Exception {
    createEmptyFile(path);
    assertThat(fileSystem.pathState(path.appendPart("something")))
        .isEqualTo(NOTHING);
  }

  @Test
  public void creating_file_twice_is_possible() throws Exception {
    createEmptyFile(path);
    createEmptyFile(path);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(FILE);
  }

  // filesFrom()

  @Test
  public void files_throws_exception_when_dir_does_not_exist() {
    assertCall(() -> fileSystem.files(PathS.path("abc")))
        .throwsException(new IOException("Dir 'abc' doesn't exist."));
  }

  @Test
  public void files_throws_exception_when_path_is_a_file() throws Exception {
    createEmptyFile(path);
    assertCall(() -> fileSystem.files(path))
        .throwsException(new IOException("Dir " + path.q() + " doesn't exist. It is a file."));
  }

  @Test
  public void files_returns_all_children() throws Exception {
    createEmptyFile("abc/dir1/file1.txt");
    createEmptyFile("abc/dir2/file2.txt");
    createEmptyFile("abc/text.txt");
    assertThat(fileSystem.files(PathS.path("abc")))
        .containsExactly(PathS.path("dir1"), PathS.path("dir2"), PathS.path("text.txt"));
  }

  @Test
  public void files_throws_exception_when_path_does_not_exist() {
    assertCall(() -> fileSystem.files(PathS.path("abc")))
        .throwsException(IOException.class);
  }

  // size

  @Test
  public void empty_file_has_zero_size() throws Exception {
    createFile(path, ByteString.encodeUtf8(""));
    assertThat(fileSystem.size(path))
        .isEqualTo(0);
  }

  @Test
  public void file_with_non_zero_size() throws Exception {
    createFile(path, ByteString.encodeUtf8("abc"));
    assertThat(fileSystem.size(path))
        .isEqualTo(3);
  }

  @Test
  public void reading_size_of_dir_causes_exception() throws Exception {
    createEmptyFile(dir.append(path));
    assertCall(() -> fileSystem.size(dir))
        .throwsException(new IOException("File 'my/dir' doesn't exist. It is a dir."));
  }

  @Test
  public void reading_size_of_nothing_causes_exception() {
    assertCall(() -> fileSystem.size(dir))
        .throwsException(new IOException("File 'my/dir' doesn't exist."));
  }

  @Test
  public void reading_size_of_link_returns_size_of_target_file() throws IOException {
    createFile(path, bytes);
    fileSystem.createLink(linkPath, path);
    assertThat(fileSystem.size(linkPath))
        .isEqualTo(bytes.size());
  }

  @Test
  public void reading_size_of_link_that_targets_dir_causes_exception() throws IOException {
    createEmptyFile(dir.append(path));
    fileSystem.createLink(linkPath, dir);
    assertCall(() -> fileSystem.size(dir))
        .throwsException(new IOException("File 'my/dir' doesn't exist. It is a dir."));
  }

  // source()

  @Test
  public void source_reads_file_content() throws Exception {
    createFile(path, bytes);
    assertThat(fileSystem.source(path).readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void source_throws_exception_when_file_does_not_exist() {
    assertCall(() -> fileSystem.source(PathS.path("dir/file")))
        .throwsException(new IOException("File 'dir/file' doesn't exist."));
  }

  @Test
  public void source_throws_exception_when_path_is_dir() throws Exception {
    createEmptyFile(path);
    assertCall(() -> fileSystem.source(path.parent()))
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
    try (BufferedSink sink = fileSystem.sink(path)) {
      sink.write(bytes);
    }
    assertThat(fileSystem.source(path).readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void sink_overwrites_existing_file() throws Exception {
    try (BufferedSink sink = fileSystem.sink(path)) {
      sink.write(ByteString.encodeUtf8("abc"));
    }
    try (BufferedSink sink = fileSystem.sink(path)) {
      sink.write(ByteString.encodeUtf8("def"));
    }
    assertThat(fileSystem.source(path).readByteString())
        .isEqualTo(ByteString.encodeUtf8("def"));
  }

  @Test
  public void sink_fails_when_target_file_is_a_dir() throws Exception {
    createEmptyFile(dir.append(path));
    assertCall(() -> fileSystem.sink(dir))
        .throwsException(IOException.class);
  }

  // move()

  @Test
  public void moving_nonexistent_file_fails() {
    assertCall(() -> fileSystem.move(PathS.path("source"), PathS.path("target")))
        .throwsException(new IOException("Cannot move 'source'. It doesn't exist."));
  }

  @Test
  public void moving_directory_fails() throws Exception {
    createEmptyFile(PathS.path("source/file"));
    assertCall(() -> fileSystem.move(PathS.path("source"), PathS.path("target")))
        .throwsException(new IOException("Cannot move 'source'. It is directory."));
  }

  @Test
  public void moving_to_directory_fails() throws Exception {
    createEmptyFile(PathS.path("source"));
    createEmptyFile(PathS.path("target/file"));
    assertCall(() -> fileSystem.move(PathS.path("source"), PathS.path("target")))
        .throwsException(new IOException(
            "Cannot move to 'target'. It is directory."));
  }

  @Test
  public void moved_file_is_deleted_from_source() throws Exception {
    createEmptyFile(PathS.path("source"));
    fileSystem.move(PathS.path("source"), PathS.path("target"));
    assertThat(fileSystem.pathState(PathS.path("source")))
        .isEqualTo(NOTHING);
  }

  @Test
  public void moved_file_is_copied_to_target() throws Exception {
    createFile(PathS.path("source"), bytes);
    fileSystem.move(PathS.path("source"), PathS.path("target"));
    assertThat(fileSystem.pathState(PathS.path("source")))
        .isEqualTo(NOTHING);
    assertThat(fileSystem.source(PathS.path("target")).readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void moved_file_overwrites_target_file() throws Exception {
    createFile(PathS.path("source"), bytes);
    createEmptyFile(PathS.path("target"));
    fileSystem.move(PathS.path("source"), PathS.path("target"));
    assertThat(fileSystem.pathState(PathS.path("source")))
        .isEqualTo(NOTHING);
    assertThat(fileSystem.source(PathS.path("target")).readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void moving_creates_missing_parent_directories_in_target_path() throws Exception {
    createFile(PathS.path("source"), bytes);
    fileSystem.move(PathS.path("source"), PathS.path("dir/target"));
    assertThat(fileSystem.source(PathS.path("dir/target")).readByteString())
        .isEqualTo(bytes);
  }

  // delete()

  @Test
  public void deleting_dir_removes_its_files() throws Exception {
    createEmptyFile(path);
    fileSystem.delete(path.parent());
    assertThat(fileSystem.pathState(path))
        .isEqualTo(NOTHING);
  }

  @Test
  public void delete_file() throws Exception {
    createEmptyFile(path);
    fileSystem.delete(path);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(NOTHING);
  }

  @Test
  public void delete_does_nothing_for_nonexistet_path() throws Exception {
    fileSystem.delete(path);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(NOTHING);
  }

  @Test
  public void deleting_root_path_removes_all_files() throws Exception {
    createEmptyFile(path);
    createEmptyFile(path2);
    fileSystem.delete(PathS.root());
    assertThat(fileSystem.pathState(path))
        .isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(path2))
        .isEqualTo(NOTHING);
  }

  // links

  @Test
  public void link_contains_data_from_target() throws Exception {
    createFile(path, bytes);
    fileSystem.createLink(linkPath, path);
    assertThat(fileSystem.source(linkPath).readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void creating_links_creates_missing_dirs() throws Exception {
    createFile(path, bytes);
    fileSystem.createLink(linkPath, path);
    assertThat(fileSystem.pathState(linkPath))
        .isEqualTo(FILE);
  }

  @Test
  public void deleted_link_is_removed() throws Exception {
    createFile(path, bytes);
    fileSystem.createLink(linkPath, path);
    fileSystem.delete(linkPath);
    assertThat(fileSystem.pathState(linkPath))
        .isEqualTo(NOTHING);
  }

  @Test
  public void deleting_link_to_file_does_not_delete_target() throws Exception {
    createFile(path, bytes);
    fileSystem.createLink(linkPath, path);
    fileSystem.delete(linkPath);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(FILE);
  }

  @Test
  public void link_to_dir_can_be_used_to_access_its_file() throws Exception {
    createFile(path, bytes);
    fileSystem.createLink(linkPath, path.parent());
    assertThat(fileSystem.source(linkPath.append(path.lastPart())).readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void deleting_link_to_dir_does_not_delete_target() throws Exception {
    createEmptyFile(dir.appendPart("ignore"));
    fileSystem.createLink(path, dir);
    fileSystem.delete(path);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(dir))
        .isEqualTo(DIR);
  }

  @Test
  public void cannot_create_link_when_path_is_taken_by_file() throws Exception {
    createEmptyFile(path);
    createEmptyFile(linkPath);
    assertCall(() -> fileSystem.createLink(linkPath, path))
        .throwsException(new IOException("Cannot use " + linkPath + " path. It is already taken."));
  }

  @Test
  public void cannot_create_link_when_path_is_taken_by_dir() throws Exception {
    createEmptyFile(path);
    createEmptyFile(linkPath);
    assertCall(() -> fileSystem.createLink(linkPath.parent(), path))
        .throwsException(new IOException(
            "Cannot use " + linkPath.parent() + " path. It is already taken."));
  }

  // createDir()

  @Test
  public void created_dir_exists() throws Exception {
    fileSystem.createDir(path);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(DIR);
  }

  @Test
  public void creating_existing_dir_does_not_cause_errors() throws Exception {
    fileSystem.createDir(path);
    fileSystem.createDir(path);
    assertThat(fileSystem.pathState(path))
        .isEqualTo(DIR);
  }

  @Test
  public void cannot_create_dir_if_such_file_already_exists() throws Exception {
    createEmptyFile(path);
    assertCall(() -> fileSystem.createDir(path))
        .throwsException(FileAlreadyExistsException.class);
  }

  // helpers

  protected void createEmptyFile(String stringPath) throws IOException {
    createFile(PathS.path(stringPath), ByteString.of());
  }

  protected void createEmptyFile(PathS path) throws IOException {
    createFile(path, ByteString.of());
  }

  protected abstract void createFile(PathS path, ByteString content) throws IOException;
}
