package org.smoothbuild.fs.disk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.base.PathState.DIR;
import static org.smoothbuild.fs.base.PathState.FILE;
import static org.smoothbuild.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.common.JdkFileTester.assertContent;
import static org.smoothbuild.testing.common.JdkFileTester.createDir;
import static org.smoothbuild.testing.common.JdkFileTester.createEmptyFile;
import static org.smoothbuild.testing.common.JdkFileTester.createFileContent;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;

import org.junit.Test;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.CannotCreateFileException;
import org.smoothbuild.fs.base.exc.NoSuchDirException;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.testing.common.TestCaseWithTempDir;

public class DiskFileSystemTest extends TestCaseWithTempDir {
  File root = getTempDirectory();
  FileSystem fileSystem = new DiskFileSystem(root.getAbsolutePath());
  String content = "file content";
  Path path = path("my/dir/myFile");

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
    createEmptyFile(root, "file.txt");
    assertThat(fileSystem.pathState(path("file.txt"))).isEqualTo(FILE);
  }

  @Test
  public void pathKindOfADir() throws Exception {
    File myDirectory = new File(root, "abc");
    myDirectory.mkdirs();

    assertThat(fileSystem.pathState(path("abc"))).isEqualTo(DIR);
  }

  @Test
  public void pathKindIsNothingWhenFirstPartOfItIsExistingFile() throws Exception {
    createEmptyFile(root, "abc");
    assertThat(fileSystem.pathState(path("abc/def/ghi"))).isEqualTo(NOTHING);
  }

  // childNames()

  @Test(expected = NoSuchDirException.class)
  public void childNamesThrowsExceptionForNonexistentDir() throws Exception {
    fileSystem.childNames(path("abc"));
  }

  @Test(expected = NoSuchDirException.class)
  public void childNamesThrowsExceptionForFilePassedAsDir() throws Exception {
    String fileName = "myFile";
    createEmptyFile(root, fileName);

    fileSystem.childNames(path(fileName));
  }

  @Test
  public void childNamesReturnsDirectFileAndDirs() throws Exception {
    createEmptyFile(root, "fileOutsideMain");

    String mainDirName = "mainDir";
    File mainDir = createDir(root, mainDirName);

    String directFileName = "directFile";
    createEmptyFile(mainDir, directFileName);

    String directDirName = "directDir";
    File directDir = createDir(mainDir, directDirName);

    String notDirectFileName = "notDirectFile";
    createEmptyFile(directDir, notDirectFileName);

    String notDirectDirName = "notDirectDir";
    createDir(directDir, notDirectDirName);

    assertThat(fileSystem.childNames(path(mainDirName)))
        .containsOnly(directFileName, directDirName);
  }

  // filesFrom()

  @Test
  public void filesFromReturnsAllFilesRecursively() throws Exception {
    createEmptyFile(root, "fileOutsideMain");

    String mainDirName = "mainDir";
    File mainDir = createDir(root, mainDirName);

    String directFileName = "directFile";
    createEmptyFile(mainDir, directFileName);

    String directDirName = "directDir";
    File directDir = createDir(mainDir, directDirName);

    String notDirectFileName = "notDirectFile";
    createEmptyFile(directDir, notDirectFileName);

    String notDirectDirName = "notDirectDir";
    createDir(directDir, notDirectDirName);

    Path first = path(directFileName);
    Path second = path(directDirName).append(path(notDirectFileName));

    assertThat(fileSystem.filesFrom(path(mainDirName))).containsOnly(first, second);
  }

  @Test
  public void filesFromReturnsEmptyIterableForNonexistentDir() throws Exception {
    assertThat(fileSystem.filesFrom(path("nonexistent"))).isEmpty();
  }

  @Test
  public void filesFromThrowsExceptionWhenPassedPathIsFileInsteadOfDir() throws Exception {
    String fileName = "fileName";
    createEmptyFile(root, fileName);

    try {
      fileSystem.filesFrom(path(fileName));
      fail("exception should be thrown");
    } catch (Exception e) {
      // expected
    }
  }

  // openInputStream()

  @Test
  public void openInputStream() throws Exception {
    String fileName = "fileName";
    String content = "file content";
    createFileContent(root, fileName, content);

    assertContent(fileSystem.openInputStream(path(fileName)), content);
  }

  @Test
  public void openInputStreamReturnsBufferedStream() throws Exception {
    String fileName = "fileName";
    String content = "file content";
    createFileContent(root, fileName, content);

    assertThat(fileSystem.openInputStream(path(fileName))).isInstanceOf(BufferedInputStream.class);
  }

  @Test
  public void openInputStreamThrowsExceptionForNonexistentFile() throws Exception {
    Path path = path("nonexistent");
    try {
      fileSystem.openInputStream(path);
      fail("exception should be thrown");
    } catch (NoSuchFileException e) {
      // expected
    }
  }

  @Test
  public void openInputStreamThrowsExceptionForDirFile() throws Exception {
    String dirName = "dirName";
    createDir(root, dirName);
    Path path = path(dirName);
    try {
      fileSystem.openInputStream(path);
      fail("exception should be thrown");
    } catch (NoSuchFileException e) {
      // expected
    }
  }

  // openOutputStream()

  @Test
  public void openOutputStream() throws Exception {
    String fileName = "fileName";
    Path path = path(fileName);
    String content = "content";

    writeAndClose(fileSystem.openOutputStream(path), content);

    assertContent(root, fileName, content);
  }

  @Test
  public void openOutputStreamReturnsBufferedStream() throws Exception {
    Path path = path("fileName");

    assertThat(fileSystem.openOutputStream(path)).isInstanceOf(BufferedOutputStream.class);
  }

  @Test
  public void openOutputStreamOverwritesExistentFile() throws Exception {
    String fileName = "fileName";
    Path path = path(fileName);
    String content = "content";
    createFileContent(root, fileName, "old content");

    writeAndClose(fileSystem.openOutputStream(path), content);

    assertContent(root, fileName, content);
  }

  @Test
  public void openOutputStreamThrowsExceptionForDirectory() throws Exception {
    String dirName = "dirname";
    Path path = path(dirName);
    createDir(root, dirName);

    try {
      fileSystem.openOutputStream(path);
      fail("exception should be thrown");
    } catch (CannotCreateFileException e) {
      // expected
    }
  }

  // delete()

  @Test
  public void delete_directory() throws Exception {
    // given
    File fileOutside = createEmptyFile(root, "fileOutsideMain");

    String mainDirName = "mainDir";
    File mainDir = createDir(root, mainDirName);

    String directFileName = "directFile";
    File directFile = createEmptyFile(mainDir, directFileName);

    String directDirName = "directDir";
    File directDir = createDir(mainDir, directDirName);

    String notDirectFileName = "notDirectFile";
    File notDirectFile = createEmptyFile(directDir, notDirectFileName);

    String notDirectDirName = "notDirectDir";
    File notDirectDir = createDir(directDir, notDirectDirName);

    // when
    fileSystem.delete(path(mainDirName));

    // then
    assertThat(fileOutside.exists()).isTrue();

    assertThat(mainDir.exists()).isFalse();
    assertThat(directFile.exists()).isFalse();
    assertThat(directDir.exists()).isFalse();
    assertThat(notDirectFile.exists()).isFalse();
    assertThat(notDirectDir.exists()).isFalse();
  }

  @Test
  public void delete_file() throws Exception {
    String fileName = "fileName";
    File file = createEmptyFile(root, fileName);
    Path path = path(fileName);

    fileSystem.delete(path);
    assertThat(file.exists()).isFalse();
  }

  @Test
  public void delete_does_nothing_for_nonexistent_path() throws Exception {
    fileSystem.delete(path("nonexistent"));
  }

  // links

  @Test
  public void link_contains_data_from_target() throws Exception {
    writeAndClose(fileSystem.openOutputStream(path), content);
    Path linkPath = path("link");

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
    writeAndClose(fileSystem.openOutputStream(path), content);
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
    writeAndClose(fileSystem.openOutputStream(targetPath), content);

    Path dirLink = path("dir2");
    Path linkPath = dirLink.append(file);

    fileSystem.createLink(dirLink, dir);

    assertContent(fileSystem.openInputStream(linkPath), content);
  }

  @Test
  public void deleting_link_to_directory_does_not_delete_target() throws Exception {
    Path dir = path("dir1");
    Path dirLink = path("dir2");
    writeAndClose(fileSystem.openOutputStream(dir.append(path("ignore"))), content);

    fileSystem.createLink(dirLink, dir);
    fileSystem.delete(dirLink);

    assertThat(fileSystem.pathState(dirLink)).isEqualTo(NOTHING);
    assertThat(fileSystem.pathState(dir)).isEqualTo(DIR);
  }
}
