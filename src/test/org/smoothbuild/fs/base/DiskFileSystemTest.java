package org.smoothbuild.fs.base;

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
import org.smoothbuild.fs.base.exc.CannotCreateFileException;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.fs.base.exc.NoSuchDirException;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.testing.common.TestCaseWithTempDir;

public class DiskFileSystemTest extends TestCaseWithTempDir {
  File root = getTempDirectory();
  FileSystem fileSystem = new DiskFileSystem(root.getAbsolutePath());

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

  // copy()

  @Test
  public void copy() throws Exception {
    String content = "old content";
    String sourceFileName = "sourceFile";
    String destinationFileName = "destinationFile";
    createFileContent(root, sourceFileName, content);

    fileSystem.copy(path(sourceFileName), path(destinationFileName));

    assertContent(root, destinationFileName, content);
  }

  @Test
  public void copyThrowsExceptionWhenSourceDoesNotExist() throws Exception {
    Path source = path("source");
    Path destination = path("destination");
    try {
      fileSystem.copy(source, destination);
      fail("exception should be thrown");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void copyThrowsExceptionWhenDestinationIsDir() throws Exception {
    String sourceFileName = "source";
    createEmptyFile(root, sourceFileName);
    Path source = path(sourceFileName);

    String dirName = "destination";
    createDir(root, dirName);
    Path destination = path(dirName);

    try {
      fileSystem.copy(source, destination);
      fail("exception should be thrown");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void deleteDirectoryRecursively() throws Exception {
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
    fileSystem.deleteDirectoryRecursively(path(mainDirName));

    // then
    assertThat(fileOutside.exists()).isTrue();

    assertThat(mainDir.exists()).isFalse();
    assertThat(directFile.exists()).isFalse();
    assertThat(directDir.exists()).isFalse();
    assertThat(notDirectFile.exists()).isFalse();
    assertThat(notDirectDir.exists()).isFalse();
  }

  @Test
  public void deleteDirectoryRecursivelyThrowsExceptionForNonDir() throws Exception {
    String fileName = "fileName";
    createEmptyFile(root, fileName);
    Path path = path(fileName);

    try {
      fileSystem.deleteDirectoryRecursively(path);
      fail("exception should be thrown");
    } catch (NoSuchDirException e) {
      // expected
    }
  }

  @Test
  public void deleteDirectoryRecursivelyThrowsExceptionForNonexistentDir() throws Exception {
    Path path = path("nonexistent");

    try {
      fileSystem.deleteDirectoryRecursively(path);
      fail("exception should be thrown");
    } catch (NoSuchDirException e) {
      // expected
    }
  }
}
