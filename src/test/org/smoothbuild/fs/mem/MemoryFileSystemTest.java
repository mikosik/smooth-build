package org.smoothbuild.fs.mem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.PathKind.DIR;
import static org.smoothbuild.fs.base.PathKind.FILE;
import static org.smoothbuild.fs.base.PathKind.NOTHING;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.fs.base.exc.NoSuchDirException;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.task.err.FileSystemError;

import com.google.common.io.LineReader;

public class MemoryFileSystemTest {
  MemoryFileSystem fileSystem = new MemoryFileSystem();

  @Test
  public void root() throws Exception {
    assertThat(fileSystem.root()).isEqualTo(Path.rootPath());
  }

  @Test
  public void rootPathIsADir() throws Exception {
    assertThat(fileSystem.pathKind(Path.rootPath())).isEqualTo(DIR);
  }

  @Test
  public void nonRootPathsAreInitiallyNothing() throws Exception {
    assertThat(fileSystem.pathKind(path("abc"))).isEqualTo(NOTHING);
    assertThat(fileSystem.pathKind(path("abc/def"))).isEqualTo(NOTHING);
  }

  @Test
  public void pathKindOfAFile() throws Exception {
    createEmptyFile("abc/def/ghi/file.txt");
    assertThat(fileSystem.pathKind(path("abc/def/ghi/file.txt"))).isEqualTo(FILE);
  }

  @Test
  public void pathKindOfADir() throws Exception {
    createEmptyFile("abc/file.txt");
    assertThat(fileSystem.pathKind(path("abc"))).isEqualTo(DIR);
  }

  @Test
  public void pathKindIsNothingWhenFirstPartOfItIsExistingFile() throws Exception {
    createEmptyFile("abc/def");
    assertThat(fileSystem.pathKind(path("abc/def/ghi"))).isEqualTo(NOTHING);
  }

  @Test
  public void creatingFileTwiceIsPossible() throws Exception {
    createEmptyFile("abc/def/ghi/text.txt");
    createEmptyFile("abc/def/ghi/text.txt");
  }

  @Test(expected = NoSuchDirException.class)
  public void childNamesThrowsExceptionWhenDirDoesNotExist() throws Exception {
    fileSystem.childNames(path("abc"));
  }

  @Test
  public void childNamesThatAreDirectories() throws Exception {
    createEmptyFile("abc/dir1/text.txt");
    createEmptyFile("abc/dir2/text.txt");
    createEmptyFile("abc/dir3/text.txt");
    assertThat(fileSystem.childNames(path("abc"))).containsOnly("dir1", "dir2", "dir3");
  }

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
    } catch (FileSystemError e) {
      // expected
    }
  }

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
    String line = "abcdefgh";
    Path path = path("a/b/file.txt");

    createFile(path, line);

    LineReader reader = new LineReader(new InputStreamReader(fileSystem.openInputStream(path)));

    assertThat(reader.readLine()).isEqualTo(line);
    assertThat(reader.readLine()).isNull();
  }

  @Test
  public void cannotCreateOutputStreamWhenFileIsADirectory() throws Exception {
    createEmptyFile("abc/def/file.txt");
    try {
      fileSystem.openOutputStream(path("abc/def"));
    } catch (FileSystemError e) {
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
    } catch (FileSystemError e) {
      // expected
    }
  }

  @Test(expected = NoSuchFileException.class)
  public void copyThrowsExceptionWhenSourceDoesNotExist() throws Exception {
    fileSystem.copy(path("abc"), path("def"));
  }

  @Test
  public void copying() throws Exception {
    String line = "abcdefgh";
    String source = "a/b/file1.txt";
    String destination = "d/e/file2.txt";
    Path sourcePath = path(source);
    Path destinationPath = path(destination);

    createFile(sourcePath, line);

    fileSystem.copy(sourcePath, destinationPath);

    LineReader reader = new LineReader(new InputStreamReader(
        fileSystem.openInputStream(destinationPath)));

    assertThat(reader.readLine()).isEqualTo(line);
    assertThat(reader.readLine()).isNull();
  }

  @Test
  public void cannotCopyFromADirectory() throws Exception {
    createEmptyFile("abc/def/file.txt");
    try {
      fileSystem.copy(path("abc/def"), path("xyz/output.txt"));
    } catch (FileSystemError e) {
      // expected
    }
  }

  @Test
  public void cannotCopyToADirectory() throws Exception {
    String sourceFileName = "abc/def/file.txt";
    createEmptyFile(sourceFileName);
    createEmptyFile("xyz/prs/file.txt");

    try {
      fileSystem.copy(path(sourceFileName), path("xyz"));
    } catch (FileSystemError e) {
      // expected
    }
  }

  @Test
  public void deleteDirectoryRecursively() throws Exception {
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
    fileSystem.deleteDirectoryRecursively(mainDir);

    // then
    assertThat(fileSystem.pathKind(fileOutsideMain)).isEqualTo(FILE);

    assertThat(fileSystem.pathKind(directFile)).isEqualTo(NOTHING);
    assertThat(fileSystem.pathKind(directDir)).isEqualTo(NOTHING);

    assertThat(fileSystem.pathKind(notDirectFile)).isEqualTo(NOTHING);
    assertThat(fileSystem.pathKind(notDirectDir)).isEqualTo(NOTHING);
  }

  @Test
  public void deleteDirectoryRecursivelyThrowsExceptionForNonDir() throws Exception {
    Path file = path("myFile");
    createEmptyFile(file);

    try {
      fileSystem.deleteDirectoryRecursively(file);
      fail("exception should be thrown");
    } catch (NoSuchDirException e) {
      // expected
    }
  }

  @Test
  public void deleteDirectoryRecursivelyThrowsExceptionForNonexistentDir() throws Exception {
    Path nonexistentPath = path("nonexistent");
    try {
      fileSystem.deleteDirectoryRecursively(nonexistentPath);
      fail("exception should be thrown");
    } catch (NoSuchDirException e) {
      // expected
    }
  }

  private void createEmptyFile(String path) throws IOException {
    createEmptyFile(path(path));
  }

  private void createEmptyFile(Path path) throws IOException {
    createFile(path, "");
  }

  private void createFile(Path path, String line) throws IOException {
    OutputStream outputStream = fileSystem.openOutputStream(path);
    writeAndClose(outputStream, line);
  }
}
