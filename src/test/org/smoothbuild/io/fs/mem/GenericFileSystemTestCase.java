package org.smoothbuild.io.fs.mem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.CannotCreateFileException;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.io.fs.base.exc.NoSuchDirException;
import org.smoothbuild.io.fs.base.exc.NoSuchFileException;

import com.google.common.io.LineReader;

public abstract class GenericFileSystemTestCase {
  protected FileSystem fileSystem;

  protected String content = "file content";
  protected Path path = path("my/dir/myFile");

  @Test
  public void root() throws Exception {
    assertThat(fileSystem.root()).isEqualTo(Path.rootPath());
  }

  // pathKind()

  @Test
  public void rootPathIsADir() throws Exception {
    assertThat(fileSystem.pathState(Path.rootPath())).isEqualTo(DIR);
  }

  @Test
  public void nonRootPathsAreInitiallyNothing() throws Exception {
    assertThat(fileSystem.pathState(path("abc"))).isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(path("abc/def"))).isEqualTo(NOTHING);
  }

  @Test
  public void pathKindOfAFile() throws Exception {
    createEmptyFile("abc/def/ghi/file.txt");
    assertThat(fileSystem.pathState(path("abc/def/ghi/file.txt"))).isEqualTo(FILE);
  }

  @Test
  public void pathKindOfADir() throws Exception {
    createEmptyFile("abc/file.txt");
    assertThat(fileSystem.pathState(path("abc"))).isEqualTo(DIR);
  }

  @Test
  public void pathKindIsNothingWhenFirstPartOfItIsExistingFile() throws Exception {
    createEmptyFile("abc/def");
    assertThat(fileSystem.pathState(path("abc/def/ghi"))).isEqualTo(NOTHING);
  }

  // childeNames()

  @Test
  public void creatingFileTwiceIsPossible() throws Exception {
    createEmptyFile("abc/def/ghi/text.txt");
    createEmptyFile("abc/def/ghi/text.txt");
  }

  @Test(expected = NoSuchDirException.class)
  public void childNamesThrowsExceptionWhenDirDoesNotExist() throws Exception {
    fileSystem.childNames(path("abc"));
  }

  @Test(expected = NoSuchDirException.class)
  public void childNamesThrowsExceptionForFilePassedAsDir() throws Exception {
    createEmptyFile(path);
    fileSystem.childNames(path);
  }

  @Test
  public void childNamesThatAreDirectories() throws Exception {
    createEmptyFile("abc/dir1/text.txt");
    createEmptyFile("abc/dir2/text.txt");
    createEmptyFile("abc/dir3/text.txt");
    assertThat(fileSystem.childNames(path("abc"))).containsOnly("dir1", "dir2", "dir3");
  }

  // filesFrom()

  @Test
  public void childNamesThatAreFiles() throws Exception {
    createEmptyFile("abc/text1.txt");
    createEmptyFile("abc/text2.txt");
    createEmptyFile("abc/text3.txt");

    assertThat(fileSystem.childNames(path("abc"))).containsOnly("text1.txt", "text2.txt",
        "text3.txt");
  }

  @Test
  public void childNamesThrowsExceptionWhenPathDoesNotExist() throws Exception {
    try {
      fileSystem.childNames(path("abc"));
      Assert.fail("exception expected");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void childNamesThrowsExceptionWhenPathIsAFile() throws Exception {
    createEmptyFile(path);
    try {
      fileSystem.childNames(path("abc"));
      Assert.fail("exception expected");
    } catch (NoSuchDirException e) {
      // expected
    }
  }

  // openInputStream()

  public void filesFromDoesNotThrowExceptionWhenDirDoesNotExist() throws Exception {
    fileSystem.filesFrom(path("abc"));
  }

  @Test
  public void filesFrom() throws Exception {
    createEmptyFile("abc/text1.txt");
    createEmptyFile("abc/text2.txt");
    createEmptyFile("abc/def/text3.txt");
    assertThat(fileSystem.filesFrom(path("abc"))).containsOnly(path("text1.txt"),
        path("text2.txt"), path("def/text3.txt"));
  }

  @Test
  public void filesFromReturnsEmptyIterableForNonexistentDir() throws Exception {
    assertThat(fileSystem.filesFrom(path("nonexistent"))).isEmpty();
  }

  @Test
  public void writingAndReading() throws Exception {
    createFile(path, content);

    LineReader reader = new LineReader(new InputStreamReader(fileSystem.openInputStream(path)));

    assertThat(reader.readLine()).isEqualTo(content);
    assertThat(reader.readLine()).isNull();
  }

  @Test
  public void cannotCreateOutputStreamWhenFileIsADirectory() throws Exception {
    createEmptyFile("abc/def/file.txt");
    try {
      fileSystem.openOutputStream(path("abc/def"));
    } catch (FileSystemException e) {
      // expected
    }
  }

  // openOutputStream()

  @Test
  public void openOutputStream() throws Exception {
    writeAndClose(fileSystem.openOutputStream(path), content);
    assertThat(inputStreamToString(fileSystem.openInputStream(path))).isEqualTo(content);
  }

  @Test
  public void openOutputStreamOverwritesExistingFile() throws Exception {
    writeAndClose(fileSystem.openOutputStream(path), "different " + content);
    writeAndClose(fileSystem.openOutputStream(path), content);
    assertThat(inputStreamToString(fileSystem.openInputStream(path))).isEqualTo(content);
  }

  @Test
  public void openOutputStreamThrowsExceptionForDirectory() throws Exception {
    Path dir = path("my/directory");
    createEmptyFile(dir.append(path));

    try {
      fileSystem.openOutputStream(dir);
      fail("exception should be thrown");
    } catch (CannotCreateFileException e) {
      // expected
    }
  }

  @Test(expected = NoSuchFileException.class)
  public void createInputStreamThrowsExceptionWhenDirDoesNotExist() throws Exception {
    fileSystem.openInputStream(path("abc"));
  }

  @Test
  public void cannotCreateInputStreamWhenFileIsADirectory() throws Exception {
    createEmptyFile("abc/def/file.txt");
    try {
      fileSystem.openInputStream(path("abc/def"));
    } catch (FileSystemException e) {
      // expected
    }
  }

  // delete()

  @Test
  public void delete_directory() throws Exception {
    // given
    Path fileOutsideMain = path("fileOutsideMain");
    Path mainDir = path("mainDir");

    Path directFile = mainDir.append(path("directFile"));
    Path directDir = mainDir.append(path("directDir"));

    Path notDirectFile = directDir.append(path("notDirectFile"));
    Path notDirectDir = directDir.append(path("notDirectDir"));

    createEmptyFile(fileOutsideMain);
    createEmptyFile(directFile);
    createEmptyFile(notDirectFile);

    // when
    fileSystem.delete(mainDir);

    // then
    assertThat(fileSystem.pathState(fileOutsideMain)).isEqualTo(FILE);

    assertThat(fileSystem.pathState(directFile)).isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(directDir)).isEqualTo(NOTHING);

    assertThat(fileSystem.pathState(notDirectFile)).isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(notDirectDir)).isEqualTo(NOTHING);
  }

  @Test
  public void delete_file() throws Exception {
    createEmptyFile(path);
    fileSystem.delete(path);
    assertThat(fileSystem.pathState(path)).isEqualTo(NOTHING);
  }

  @Test
  public void delete_does_nothing_for_nonexistet_path() throws Exception {
    fileSystem.delete(path("nonexistent"));
  }

  // links

  @Test
  public void link_contains_data_from_target() throws Exception {
    createFile(path, content);
    Path linkPath = path("my/link");

    fileSystem.createLink(linkPath, path);

    assertContent(fileSystem.openInputStream(linkPath), content);
  }

  @Test
  public void creating_links_creates_missing_directories() throws Exception {
    writeAndClose(fileSystem.openOutputStream(path), content);
    Path linkPath = path("very/long/directory/name/link");

    fileSystem.createLink(linkPath, path);

    assertThat(fileSystem.pathState(linkPath)).isEqualTo(FILE);
  }

  @Test
  public void deleting_link_to_file_does_not_delete_target() throws Exception {
    createFile(path, content);
    Path linkPath = path("my/link");
    fileSystem.createLink(linkPath, path);

    fileSystem.delete(linkPath);

    assertThat(fileSystem.pathState(path)).isEqualTo(FILE);
    assertThat(fileSystem.pathState(linkPath)).isEqualTo(NOTHING);
  }

  @Test
  public void link_to_a_directory() throws Exception {
    Path dir = path("dir1");
    Path file = path("file");
    Path targetPath = dir.append(file);

    Path dirLink = path("dir2");
    Path linkPath = dirLink.append(file);

    createFile(targetPath, content);

    fileSystem.createLink(linkPath, targetPath);

    assertContent(fileSystem.openInputStream(linkPath), content);
  }

  @Test
  public void deleting_link_to_directory_does_not_delete_target() throws Exception {
    Path dir = path("dir1");
    Path dirLink = path("dir2");
    createEmptyFile(dir.append(path("ignore")));

    fileSystem.createLink(dirLink, dir);
    fileSystem.delete(dirLink);

    assertThat(fileSystem.pathState(dirLink)).isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(dir)).isEqualTo(DIR);
  }

  // createDir()

  @Test
  public void created_dir_exists() throws Exception {
    fileSystem.createDir(path);
    assertThat(fileSystem.pathState(path)).isEqualTo(DIR);
  }

  @Test
  public void creating_existing_dir_does_not_cause_errors() throws Exception {
    fileSystem.createDir(path);
    fileSystem.createDir(path);
  }

  @Test
  public void cannot_create_dir_if_such_file_already_exists() throws Exception {
    createEmptyFile(path);
    try {
      fileSystem.createDir(path);
      fail("exception should be thrown");
    } catch (FileSystemException e) {
      // expected
    }
  }

  // helpers

  protected abstract void createEmptyFile(String path) throws IOException;

  protected abstract void createEmptyFile(Path path) throws IOException;

  protected abstract void createFile(Path path, String content) throws IOException;
}
