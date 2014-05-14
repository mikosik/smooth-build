package org.smoothbuild.io.fs.mem;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.rootPath;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.io.fs.base.err.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.io.fs.base.err.PathIsAlreadyTakenByDirError;
import org.smoothbuild.io.fs.base.err.PathIsAlreadyTakenByFileError;
import org.smoothbuild.io.fs.base.err.PathIsAlreadyTakenError;

public abstract class GenericFileSystemTestCase {
  protected FileSystem fileSystem;
  protected String content = "file content";
  protected Path path = path("some/dir/myFile");
  protected Path path2 = path("other/dir/otherFile");

  private final Path dir = path("my/dir");
  private final Path linkPath = path("my/link");

  // pathKind()

  @Test
  public void rooth_path_is_a_dir() throws Exception {
    when(fileSystem.pathState(Path.rootPath()));
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

  // childeNames()

  @Test
  public void child_names_throws_exception_when_dir_does_not_exist() throws Exception {
    when(fileSystem).childNames(path("abc"));
    thenThrown(NoSuchDirError.class);
  }

  public void child_names_throws_exception_when_path_is_a_file() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).childNames(path);
    thenThrown(NoSuchDirButFileError.class);
  }

  @Test
  public void child_names_returns_all_children() throws Exception {
    given(this).createEmptyFile("abc/dir1/file1.txt");
    given(this).createEmptyFile("abc/dir2/file2.txt");
    given(this).createEmptyFile("abc/text.txt");
    when(fileSystem).childNames(path("abc"));
    thenReturned(containsInAnyOrder(path("dir1"), path("dir2"), path("text.txt")));
  }

  @Test
  public void child_names_throws_exception_when_path_does_not_exist() throws Exception {
    when(fileSystem).childNames(path("abc"));
    thenThrown(FileSystemError.class);
  }

  // filesFromRecursive()

  @Test
  public void files_from_recursive_throws_exception_when_dir_does_not_exist() throws Exception {
    when(fileSystem).filesFromRecursive(path("abc"));
    thenThrown(NoSuchDirError.class);
  }

  public void files_from_recursive_throws_exception_when_path_is_a_file() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).filesFromRecursive(path);
    thenThrown(NoSuchDirButFileError.class);
  }

  @Test
  public void files_from_recursive_returns_all_files_recursively() throws Exception {
    given(this).createEmptyFile("abc/dir1/file1");
    given(this).createEmptyFile("abc/dir2/file2");
    given(this).createEmptyFile("abc/text.txt");
    when(fileSystem).filesFromRecursive(path("abc"));
    thenReturned(containsInAnyOrder(path("dir1/file1"), path("dir2/file2"), path("text.txt")));
  }

  @Test
  public void files_from_recursive_throws_exception_when_path_does_not_exist() throws Exception {
    when(fileSystem).filesFromRecursive(path("abc"));
    thenThrown(FileSystemError.class);
  }

  // openInputStream()

  @Test
  public void open_input_stream_reads_file_content() throws Exception {
    given(this).createFile(path, content);
    when(inputStreamToString(fileSystem.openInputStream(path)));
    thenReturned(content);
  }

  @Test
  public void cannot_open_output_stream_when_path_is_directory() throws Exception {
    given(this).createEmptyFile(path);
    when(fileSystem).openOutputStream(path.parent());
    thenThrown(PathIsAlreadyTakenByDirError.class);
  }

  @Test
  public void cannot_open_output_stream_when_path_is_root_directory() throws Exception {
    when(fileSystem).openOutputStream(Path.rootPath());
    thenThrown(PathIsAlreadyTakenByDirError.class);
  }

  // openOutputStream()

  @Test
  public void data_written_via_open_output_stream_can_be_read_by_open_input_stream()
      throws Exception {
    writeAndClose(fileSystem.openOutputStream(path), content);
    when(inputStreamToString(fileSystem.openInputStream(path)));
    thenReturned(content);
  }

  @Test
  public void open_output_stream_overwrites_existing_file() throws Exception {
    writeAndClose(fileSystem.openOutputStream(path), "different " + content);
    writeAndClose(fileSystem.openOutputStream(path), content);
    when(inputStreamToString(fileSystem.openInputStream(path)));
    thenReturned(content);
  }

  @Test
  public void open_output_stream_returns() throws Exception {
    given(this).createEmptyFile(dir.append(path));
    when(fileSystem).openOutputStream(dir);
    thenThrown(FileSystemError.class);
  }

  public void create_input_stream_throws_exception_when_file_does_not_exist() throws Exception {
    when(fileSystem).openInputStream(path("dir/file"));
    thenThrown(NoSuchFileError.class);
  }

  @Test
  public void cannot_open_input_stream_when_file_is_a_directory() throws Exception {
    given(this).createEmptyFile("abc/def/file.txt");
    when(fileSystem).openInputStream(path("abc/def"));
    thenThrown(FileSystemError.class);
  }

  // delete()

  @Test
  public void deleting_directory_removes_its_files() throws Exception {
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
    when(fileSystem).delete(rootPath());
    thenEqual(fileSystem.pathState(path), NOTHING);
    thenEqual(fileSystem.pathState(path2), NOTHING);
  }

  // links

  @Test
  public void link_contains_data_from_target() throws Exception {
    given(this).createFile(path, content);
    when(fileSystem).createLink(linkPath, path);
    thenEqual(inputStreamToString(fileSystem.openInputStream(linkPath)), content);
  }

  @Test
  public void creating_links_creates_missing_directories() throws Exception {
    given(this).createFile(path, content);
    when(fileSystem).createLink(linkPath, path);
    thenEqual(fileSystem.pathState(linkPath), FILE);
  }

  @Test
  public void deleted_link_is_removed() throws Exception {
    given(this).createFile(path, content);
    given(fileSystem).createLink(linkPath, path);
    when(fileSystem).delete(linkPath);
    thenEqual(fileSystem.pathState(linkPath), NOTHING);
  }

  @Test
  public void deleting_link_to_file_does_not_delete_target() throws Exception {
    given(this).createFile(path, content);
    given(fileSystem).createLink(linkPath, path);
    when(fileSystem).delete(linkPath);
    thenEqual(fileSystem.pathState(path), FILE);
  }

  @Test
  public void link_to_a_directory_can_be_used_to_access_its_file() throws Exception {
    given(this).createFile(path, content);
    when(fileSystem).createLink(linkPath, path.parent());
    thenEqual(inputStreamToString(fileSystem.openInputStream(linkPath.append(path.lastPart()))),
        content);
  }

  @Test
  public void deleting_link_to_directory_does_not_delete_target() throws Exception {
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
    thenThrown(PathIsAlreadyTakenError.class);
  }

  @Test
  public void cannot_create_link_when_path_is_taken_by_dir() throws Exception {
    given(this).createEmptyFile(path);
    given(this).createEmptyFile(linkPath);
    when(fileSystem).createLink(linkPath.parent(), path);
    thenThrown(PathIsAlreadyTakenError.class);
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
    thenThrown(PathIsAlreadyTakenByFileError.class);
  }

  // helpers

  protected abstract void createEmptyFile(String path) throws IOException;

  protected abstract void createEmptyFile(Path path) throws IOException;

  protected abstract void createFile(Path path, String content) throws IOException;
}
