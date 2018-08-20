package org.smoothbuild.io.fs.base;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.smoothbuild.util.Streams.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;

public abstract class GenericFileSystemTestCase {
  protected FileSystem fileSystem;
  protected byte[] bytes = new byte[] { 1, 2, 3 };
  protected Path path = path("some/dir/myFile");
  protected Path path2 = path("other/dir/otherFile");

  private final Path dir = path("my/dir");
  private final Path linkPath = path("my/link");

  // pathKind()

  @Test
  public void rooth_path_is_a_dir() throws Exception {
    when(fileSystem.pathState(Path.root()));
    thenReturned(DIR);
  }

  @Test
  public void non_root_path_are_initially_nothing_state() throws Exception {
    when(fileSystem.pathState(path("abc")));
    thenReturned(NOTHING);
  }

  @Test
  public void file_path_has_file_state() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).pathState(path);
    thenReturned(FILE);
  }

  @Test
  public void dir_path_has_dir_state() throws Exception {
    given(this).createEmptyFile(dir.append(path));
    when(fileSystem).pathState(dir);
    thenReturned(DIR);
  }

  @Test
  public void path_state_is_nothing_even_when_its_first_part_is_a_file() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).pathState(path.append(path("something")));
    thenReturned(NOTHING);
  }

  @Test
  public void creating_file_twice_is_possible() throws Exception {
    given(this).createEmptyFile(path);
    when(this).createEmptyFile(path);
    thenReturned();
  }

  // filesFrom()

  @Test
  public void files_from_throws_exception_when_dir_does_not_exist() throws Exception {
    when(fileSystem).files(path("abc"));
    thenThrown(exception(new FileSystemException("Dir " + path("abc") + " doesn't exists.")));
  }

  public void files_from_throws_exception_when_path_is_a_file() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).files(path);
    thenThrown(exception(new FileSystemException("Dir " + path + " doesn't exist. It is a file.")));
  }

  @Test
  public void files_from_returns_all_children() throws Exception {
    given(this).createEmptyFile("abc/dir1/file1.txt");
    given(this).createEmptyFile("abc/dir2/file2.txt");
    given(this).createEmptyFile("abc/text.txt");
    when(fileSystem).files(path("abc"));
    thenReturned(containsInAnyOrder(path("dir1"), path("dir2"), path("text.txt")));
  }

  @Test
  public void files_from_throws_exception_when_path_does_not_exist() throws Exception {
    when(fileSystem).files(path("abc"));
    thenThrown(FileSystemException.class);
  }

  // openInputStream()

  @Test
  public void open_input_stream_reads_file_content() throws Exception {
    given(this).createFile(path, bytes);
    when(inputStreamToByteArray(fileSystem.openInputStream(path)));
    thenReturned(bytes);
  }

  @Test
  public void cannot_open_output_stream_when_path_is_dir() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).openOutputStream(path.parent());
    thenThrown(exception(new FileSystemException("Cannot use " + path.parent()
        + " path. It is already taken by dir.")));
  }

  @Test
  public void cannot_open_output_stream_when_path_is_root_dir() throws Exception {
    when(fileSystem).openOutputStream(Path.root());
    thenThrown(exception(new FileSystemException("Cannot use " + Path.root()
        + " path. It is already taken by dir.")));
  }

  // openOutputStream()

  @Test
  public void data_written_via_open_output_stream_can_be_read_by_open_input_stream()
      throws Exception {
    writeAndClose(fileSystem.openOutputStream(path), bytes);
    when(inputStreamToByteArray(fileSystem.openInputStream(path)));
    thenReturned(bytes);
  }

  @Test
  public void open_output_stream_overwrites_existing_file() throws Exception {
    given(bytes = new byte[] { 1, 2, 3 });
    writeAndClose(fileSystem.openOutputStream(path), new byte[] { 4, 5, 6, 7, 8 });
    writeAndClose(fileSystem.openOutputStream(path), bytes);
    when(inputStreamToByteArray(fileSystem.openInputStream(path)));
    thenReturned(bytes);
  }

  @Test
  public void open_output_stream_returns() throws Exception {
    given(this).createEmptyFile(dir.append(path));
    when(fileSystem).openOutputStream(dir);
    thenThrown(FileSystemException.class);
  }

  public void create_input_stream_throws_exception_when_file_does_not_exist() throws Exception {
    when(fileSystem).openInputStream(path("dir/file"));
    thenThrown(exception(new FileSystemException("File " + path("dir/file") + " doesn't exist.")));
  }

  @Test
  public void cannot_open_input_stream_when_file_is_a_dir() throws Exception {
    given(this).createEmptyFile("abc/def/file.txt");
    when(fileSystem).openInputStream(path("abc/def"));
    thenThrown(FileSystemException.class);
  }

  // source()

  @Test
  public void source_reads_file_content() throws Exception {
    given(this).createFile(path, bytes);
    when(() -> fileSystem.source(path).readByteArray());
    thenReturned(bytes);
  }

  public void source_throws_exception_when_file_does_not_exist() throws Exception {
    when(() -> fileSystem.source(path("dir/file")));
    thenThrown(exception(new FileSystemException("File " + path("dir/file") + " doesn't exist.")));
  }

  @Test
  public void source_throws_exception_when_path_is_dir() throws Exception {
    given(this).createEmptyFile(path);
    when(() -> fileSystem.source(path.parent()));
    thenThrown(exception(new FileSystemException("File 'some/dir' doesn't exist. It is a dir.")));
  }

  @Test
  public void source_throws_exception_when_path_is_root_dir() throws Exception {
    when(() -> fileSystem.source(Path.root()));
    thenThrown(exception(new FileSystemException("File '' doesn't exist. It is a dir.")));
  }

  // sink()

  @Test
  public void data_written_via_sink_can_be_read_by_source()
      throws Exception {
    writeAndClose(fileSystem.sink(path).outputStream(), bytes);
    when(() -> fileSystem.source(path).readByteArray());
    thenReturned(bytes);
  }

  @Test
  public void sink_overwrites_existing_file() throws Exception {
    given(bytes = new byte[] { 1, 2, 3 });
    writeAndClose(fileSystem.sink(path).outputStream(), new byte[] { 4, 5, 6, 7, 8 });
    writeAndClose(fileSystem.sink(path).outputStream(), bytes);
    when(() -> fileSystem.source(path).readByteArray());
    thenReturned(bytes);
  }

  @Test
  public void sink_fails_when_target_file_is_a_dir() throws Exception {
    given(this).createEmptyFile(dir.append(path));
    when(() -> fileSystem.sink(dir));
    thenThrown(FileSystemException.class);
  }

  // move()

  @Test
  public void moving_nonexistent_file_fails() throws Exception {
    when(fileSystem).move(path("source"), path("target"));
    thenThrown(exception(new FileSystemException(
        "Cannot move " + path("source") + ". It doesn't exist.")));
  }

  @Test
  public void moving_directory_fails() throws Exception {
    given(this).createEmptyFile(path("source/file"));
    when(fileSystem).move(path("source"), path("target"));
    thenThrown(exception(new FileSystemException(
        "Cannot move " + path("source") + ". It is directory.")));
  }

  @Test
  public void moving_to_directory_fails() throws Exception {
    given(this).createEmptyFile(path("source"));
    given(this).createEmptyFile(path("target/file"));
    when(fileSystem).move(path("source"), path("target"));
    thenThrown(exception(new FileSystemException(
        "Cannot move to " + path("target") + ". It is directory.")));
  }

  @Test
  public void moved_file_is_deleted_from_source() throws Exception {
    given(this).createEmptyFile(path("source"));
    when(fileSystem).move(path("source"), path("target"));
    thenEqual(fileSystem.pathState(path("source")), NOTHING);
  }

  @Test
  public void moved_file_is_copied_to_target() throws Exception {
    given(this).createFile(path("source"), bytes);
    when(fileSystem).move(path("source"), path("target"));
    thenEqual(fileSystem.pathState(path("source")), NOTHING);
    when(inputStreamToByteArray(fileSystem.openInputStream(path("target"))));
    thenReturned(bytes);
  }

  @Test
  public void moved_file_overwrites_target_file() throws Exception {
    given(this).createFile(path("source"), bytes);
    given(this).createEmptyFile(path("target"));
    when(fileSystem).move(path("source"), path("target"));
    thenEqual(fileSystem.pathState(path("source")), NOTHING);
    when(inputStreamToByteArray(fileSystem.openInputStream(path("target"))));
    thenReturned(bytes);
  }

  @Test
  public void moving_creates_missing_parent_directories_in_target_path() throws Exception {
    given(this).createFile(path("source"), bytes);
    when(fileSystem).move(path("source"), path("dir/target"));
    when(inputStreamToByteArray(fileSystem.openInputStream(path("dir/target"))));
    thenReturned(bytes);
  }

  // delete()

  @Test
  public void deleting_dir_removes_its_files() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).delete(path.parent());
    thenEqual(fileSystem.pathState(path), NOTHING);
  }

  @Test
  public void delete_file() throws Exception {
    given(this).createEmptyFile(path);
    given(fileSystem).delete(path);
    when(fileSystem).pathState(path);
    thenReturned(NOTHING);
  }

  @Test
  public void delete_does_nothing_for_nonexistet_path() throws Exception {
    when(fileSystem).delete(path);
    thenReturned();
  }

  @Test
  public void deleting_root_path_removes_all_files() throws Exception {
    given(this).createEmptyFile(path);
    given(this).createEmptyFile(path2);
    when(fileSystem).delete(Path.root());
    thenEqual(fileSystem.pathState(path), NOTHING);
    thenEqual(fileSystem.pathState(path2), NOTHING);
  }

  // links

  @Test
  public void link_contains_data_from_target() throws Exception {
    given(this).createFile(path, bytes);
    when(fileSystem).createLink(linkPath, path);
    thenEqual(inputStreamToByteArray(fileSystem.openInputStream(linkPath)), bytes);
  }

  @Test
  public void creating_links_creates_missing_dirs() throws Exception {
    given(this).createFile(path, bytes);
    when(fileSystem).createLink(linkPath, path);
    thenEqual(fileSystem.pathState(linkPath), FILE);
  }

  @Test
  public void deleted_link_is_removed() throws Exception {
    given(this).createFile(path, bytes);
    given(fileSystem).createLink(linkPath, path);
    when(fileSystem).delete(linkPath);
    thenEqual(fileSystem.pathState(linkPath), NOTHING);
  }

  @Test
  public void deleting_link_to_file_does_not_delete_target() throws Exception {
    given(this).createFile(path, bytes);
    given(fileSystem).createLink(linkPath, path);
    when(fileSystem).delete(linkPath);
    thenEqual(fileSystem.pathState(path), FILE);
  }

  @Test
  public void link_to_dir_can_be_used_to_access_its_file() throws Exception {
    given(this).createFile(path, bytes);
    when(fileSystem).createLink(linkPath, path.parent());
    thenEqual(inputStreamToByteArray(fileSystem.openInputStream(linkPath.append(path.lastPart()))),
        bytes);
  }

  @Test
  public void deleting_link_to_dir_does_not_delete_target() throws Exception {
    given(this).createEmptyFile(dir.append(path("ignore")));
    given(fileSystem).createLink(path, dir);
    when(fileSystem).delete(path);
    thenEqual(fileSystem.pathState(path), NOTHING);
    thenEqual(fileSystem.pathState(dir), DIR);
  }

  @Test
  public void cannot_create_link_when_path_is_taken_by_file() throws Exception {
    given(this).createEmptyFile(path);
    given(this).createEmptyFile(linkPath);
    when(fileSystem).createLink(linkPath, path);
    thenThrown(exception(new FileSystemException("Cannot use " + linkPath
        + " path. It is already taken.")));
  }

  @Test
  public void cannot_create_link_when_path_is_taken_by_dir() throws Exception {
    given(this).createEmptyFile(path);
    given(this).createEmptyFile(linkPath);
    when(fileSystem).createLink(linkPath.parent(), path);
    thenThrown(exception(new FileSystemException("Cannot use " + linkPath.parent()
        + " path. It is already taken.")));
  }

  // createDir()

  @Test
  public void created_dir_exists() throws Exception {
    given(fileSystem).createDir(path);
    when(fileSystem).pathState(path);
    thenReturned(DIR);
  }

  @Test
  public void creating_existing_dir_does_not_cause_errors() throws Exception {
    given(fileSystem).createDir(path);
    when(fileSystem).createDir(path);
    thenReturned();
  }

  @Test
  public void cannot_create_dir_if_such_file_already_exists() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).createDir(path);
    thenThrown(exception(new FileSystemException("Cannot use " + path
        + " path. It is already taken by file.")));
  }

  // helpers

  protected abstract void createEmptyFile(String path) throws IOException;

  protected abstract void createEmptyFile(Path path) throws IOException;

  protected abstract void createFile(Path path, byte[] content) throws IOException;
}
