package org.smoothbuild.fs.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingJdkFile.assertContent;
import static org.smoothbuild.testing.TestingJdkFile.createDir;
import static org.smoothbuild.testing.TestingJdkFile.createEmptyFile;
import static org.smoothbuild.testing.TestingJdkFile.createFileContent;
import static org.smoothbuild.testing.TestingStream.assertContent;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.exc.CannotCreateFileException;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.fs.base.exc.NoSuchDirException;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestCaseWithTempDir;

public class DiskFileSystemTest extends TestCaseWithTempDir {
  File root = getTempDirectory();
  FileSystem fileSystem = new DiskFileSystem(root.getAbsolutePath());

  @Test
  public void root() throws Exception {
    assertThat(fileSystem.root()).isEqualTo(Path.rootPath());
  }

  // pathExists()

  @Test
  public void pathExistsReturnsTrueForExistingDir() {
    String myDir = "myDirectory";
    File myDirectory = new File(root, myDir);
    myDirectory.mkdirs();

    assertThat(fileSystem.pathExists(path(myDir))).isTrue();
  }

  @Test
  public void pathExistsReturnsTrueForExistingFile() throws IOException {
    String fileName = "myFile";
    createEmptyFile(root, fileName);

    assertThat(fileSystem.pathExists(path(fileName))).isTrue();
  }

  @Test
  public void pathExistsReturnsFalseWhenPathDoesNotExist() throws IOException {
    assertThat(fileSystem.pathExists(path("myFile"))).isFalse();
  }

  // pathExistsAndIsDirectory()

  @Test
  public void pathExistsAndIsDirectoryReturnsTrueForDirectoryPath() throws Exception {
    String myDir = "myDirectory";
    File myDirectory = new File(root, myDir);
    myDirectory.mkdirs();

    assertThat(fileSystem.pathExistsAndIsDirectory(path(myDir))).isTrue();
  }

  @Test
  public void pathExistsAndIsDirectoryReturnsFalseForFilePath() throws Exception {
    String fileName = "myFile";
    createEmptyFile(root, fileName);

    assertThat(fileSystem.pathExistsAndIsDirectory(path(fileName))).isFalse();
  }

  @Test
  public void pathExistsAndIsDirectoryReturnsFalseForNonexistentPathPath() throws Exception {
    assertThat(fileSystem.pathExistsAndIsDirectory(path("myFile"))).isFalse();
  }

  // pathExistsAndIsFile()

  @Test
  public void pathExistsAndIsFileReturnsFalseForDirectoryPath() throws Exception {
    String myDir = "myDirectory";
    File myDirectory = new File(root, myDir);
    myDirectory.mkdirs();

    assertThat(fileSystem.pathExistsAndIsFile(path(myDir))).isFalse();
  }

  @Test
  public void pathExistsAndIsFileReturnsTrueForFilePath() throws Exception {
    String fileName = "myFile";
    createEmptyFile(root, fileName);

    assertThat(fileSystem.pathExistsAndIsFile(path(fileName))).isTrue();
  }

  @Test
  public void pathExistsAndIsFileReturnsFalseForNonexistentPathPath() throws Exception {
    assertThat(fileSystem.pathExistsAndIsDirectory(path("myFile"))).isFalse();
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

  @Test(expected = IllegalArgumentException.class)
  public void filesFromThrowsExceptionForNonexistentDir() throws Exception {
    fileSystem.filesFrom(path("nonexistent"));
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

  // createInputStream()

  @Test
  public void createInputStream() throws Exception {
    String fileName = "fileName";
    String content = "file content";
    createFileContent(root, fileName, content);

    assertContent(fileSystem.createInputStream(path(fileName)), content);
  }

  @Test
  public void createInputStreamThrowsExceptionForNonexistentFile() throws Exception {
    Path path = path("nonexistent");
    try {
      fileSystem.createInputStream(path);
      fail("exception should be thrown");
    } catch (NoSuchFileException e) {
      // expected
    }
  }

  @Test
  public void createInputStreamThrowsExceptionForDirFile() throws Exception {
    String dirName = "dirName";
    createDir(root, dirName);
    Path path = path(dirName);
    try {
      fileSystem.createInputStream(path);
      fail("exception should be thrown");
    } catch (NoSuchFileException e) {
      // expected
    }
  }

  // createOutputStream()

  @Test
  public void createOutputStream() throws Exception {
    String fileName = "fileName";
    Path path = path(fileName);
    String content = "content";

    writeAndClose(fileSystem.createOutputStream(path), content);

    assertContent(root, fileName, content);
  }

  @Test
  public void createOutputStreamOverwritesExistentFile() throws Exception {
    String fileName = "fileName";
    Path path = path(fileName);
    String content = "content";
    createFileContent(root, fileName, "old content");

    writeAndClose(fileSystem.createOutputStream(path), content);

    assertContent(root, fileName, content);
  }

  @Test
  public void createOutputStreamThrowsExceptionForDirectory() throws Exception {
    String dirName = "dirname";
    Path path = path(dirName);
    createDir(root, dirName);

    try {
      fileSystem.createOutputStream(path);
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
