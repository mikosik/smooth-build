package org.smoothbuild.fs.mem;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.PathUtils.WORKING_DIR;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.fs.base.FileSystemException;

import com.google.common.io.LineReader;

public class InMemoryFileSystemTest {
  InMemoryFileSystem fileSystem = new InMemoryFileSystem();

  @Test
  public void rootPathExists() {
    assertThat(fileSystem.pathExists(WORKING_DIR)).isTrue();
  }

  @Test
  public void nonRootPatsDoNotExistInitially() throws Exception {
    assertThat(fileSystem.pathExists(" ")).isFalse();
    assertThat(fileSystem.pathExists("abc")).isFalse();
    assertThat(fileSystem.pathExists("abc/def")).isFalse();
    assertThat(fileSystem.pathExists("abc/def/ghi")).isFalse();
  }

  @Test
  public void pathExistsAfterCreating() throws Exception {
    createFile("abc/def/ghi/text.txt");

    assertThat(fileSystem.pathExists("abc")).isTrue();
    assertThat(fileSystem.pathExists("abc/def")).isTrue();
    assertThat(fileSystem.pathExists("abc/def/ghi")).isTrue();
    assertThat(fileSystem.pathExists("abc/def/ghi/text.txt")).isTrue();
  }

  @Test
  public void creatingFileTwiceIsPossible() throws Exception {
    createFile("abc/def/ghi/text.txt");
    createFile("abc/def/ghi/text.txt");
  }

  @Test
  public void rootPathIsADirectory() throws Exception {
    assertThat(fileSystem.isDirectory(WORKING_DIR)).isTrue();
  }

  @Test
  public void isDirectory() throws Exception {
    createFile("abc/def/ghi/text.txt");
    assertThat(fileSystem.isDirectory("abc")).isTrue();
    assertThat(fileSystem.isDirectory("abc/def")).isTrue();
    assertThat(fileSystem.isDirectory("abc/def/ghi")).isTrue();
    assertThat(fileSystem.isDirectory("abc/def/ghi/text.txt")).isFalse();
  }

  @Test
  public void isDirectoryThrowsExceptionWhenPathDoesNotExist() throws Exception {
    try {
      fileSystem.isDirectory("abc");
      Assert.fail("exception expected");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void childNamesThatAreDirectories() throws Exception {
    createFile("abc/dir1/text.txt");
    createFile("abc/dir2/text.txt");
    createFile("abc/dir3/text.txt");
    assertThat(fileSystem.childNames("abc")).containsOnly("dir1", "dir2", "dir3");
  }

  @Test
  public void childNamesThatAreFiles() throws Exception {
    createFile("abc/text1.txt");
    createFile("abc/text2.txt");
    createFile("abc/text3.txt");

    assertThat(fileSystem.childNames("abc")).containsOnly("text1.txt", "text2.txt", "text3.txt");
  }

  @Test
  public void childNamesThrowsExceptionWhenPathDoesNotExist() throws Exception {
    try {
      fileSystem.childNames("abc");
      Assert.fail("exception expected");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void filesFrom() throws Exception {
    createFile("abc/text1.txt");
    createFile("abc/text2.txt");
    createFile("abc/def/text3.txt");
    assertThat(fileSystem.filesFrom("abc")).containsOnly("text1.txt", "text2.txt", "def/text3.txt");
  }

  @Test
  public void filesFromThrowsExceptionWhenPathDoesNotExist() throws Exception {
    try {
      fileSystem.filesFrom("abc").iterator().hasNext();
      Assert.fail("exception expected");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void writingAndReading() throws Exception {
    String line = "abcdefgh";
    String path = "a/b/file.txt";

    createFile(path, line);
    LineReader reader = new LineReader(new InputStreamReader(fileSystem.createInputStream(path)));

    assertThat(reader.readLine()).isEqualTo(line);
    assertThat(reader.readLine()).isNull();
  }

  @Test
  public void cannotCreateOutputStreamWhenFileIsADirectory() throws Exception {
    createFile("abc/def/file.txt");
    try {
      fileSystem.createOutputStream("abc/def");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void cannotCreateInputStreamWhenFileIsADirectory() throws Exception {
    createFile("abc/def/file.txt");
    try {
      fileSystem.createInputStream("abc/def");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void copying() throws Exception {
    String line = "abcdefgh";
    String source = "a/b/file1.txt";
    String destination = "d/e/file2.txt";

    createFile(source, line);

    fileSystem.copy(source, destination);

    LineReader reader = new LineReader(new InputStreamReader(
        fileSystem.createInputStream(destination)));

    assertThat(reader.readLine()).isEqualTo(line);
    assertThat(reader.readLine()).isNull();
  }

  @Test
  public void cannotCopyFromADirectory() throws Exception {
    createFile("abc/def/file.txt");
    try {
      fileSystem.copy("abc/def", "xyz/output.txt");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void cannotCopyToADirectory() throws Exception {
    createFile("abc/def/file.txt");
    createFile("xyz/prs/file.txt");

    try {
      fileSystem.copy("abc/def/file.txt", "xyz/");
    } catch (FileSystemException e) {
      // expected
    }
  }

  private void createFile(String path, String line) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(fileSystem.createOutputStream(path));
    writer.write(line);
    writer.close();
  }

  private void createFile(String path) throws IOException {
    fileSystem.createOutputStream(path).close();
  }
}
