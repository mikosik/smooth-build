package org.smoothbuild.fs.mem;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.fs.base.FileSystemException;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.lang.type.PathTest;

import com.google.common.io.LineReader;

public class InMemoryFileSystemTest {
  InMemoryFileSystem fileSystem = new InMemoryFileSystem();

  @Test
  public void pathExistsThrowsExceptionForInvalidPath() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {

      try {
        fileSystem.pathExists(path(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void rootPathExists() {
    assertThat(fileSystem.pathExists(Path.rootPath())).isTrue();
  }

  @Test
  public void nonRootPatsDoNotExistInitially() throws Exception {
    assertThat(fileSystem.pathExists(path(" "))).isFalse();
    assertThat(fileSystem.pathExists(path("abc"))).isFalse();
    assertThat(fileSystem.pathExists(path("abc/def"))).isFalse();
    assertThat(fileSystem.pathExists(path("abc/def/ghi"))).isFalse();
  }

  @Test
  public void pathExistsAfterCreating() throws Exception {
    createFile("abc/def/ghi/text.txt");

    assertThat(fileSystem.pathExists(path("abc"))).isTrue();
    assertThat(fileSystem.pathExists(path("abc/def"))).isTrue();
    assertThat(fileSystem.pathExists(path("abc/def/ghi"))).isTrue();
    assertThat(fileSystem.pathExists(path("abc/def/ghi/text.txt"))).isTrue();
  }

  @Test
  public void creatingFileTwiceIsPossible() throws Exception {
    createFile("abc/def/ghi/text.txt");
    createFile("abc/def/ghi/text.txt");
  }

  @Test
  public void isDirectoryThrowsExceptionForInvalidPath() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {

      try {
        fileSystem.isDirectory(path(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void rootPathIsADirectory() throws Exception {
    assertThat(fileSystem.isDirectory(Path.rootPath())).isTrue();
  }

  @Test
  public void isDirectory() throws Exception {
    createFile("abc/def/ghi/text.txt");
    assertThat(fileSystem.isDirectory(path("abc"))).isTrue();
    assertThat(fileSystem.isDirectory(path("abc/def"))).isTrue();
    assertThat(fileSystem.isDirectory(path("abc/def/ghi"))).isTrue();
    assertThat(fileSystem.isDirectory(path("abc/def/ghi/text.txt"))).isFalse();
  }

  @Test
  public void isDirectoryThrowsExceptionWhenPathDoesNotExist() throws Exception {
    try {
      fileSystem.isDirectory(path("abc"));
      Assert.fail("exception expected");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void childNamesThrowsExceptionForInvalidPath() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {

      try {
        fileSystem.childNames(path(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void childNamesThatAreDirectories() throws Exception {
    createFile("abc/dir1/text.txt");
    createFile("abc/dir2/text.txt");
    createFile("abc/dir3/text.txt");
    assertThat(fileSystem.childNames(path("abc"))).containsOnly("dir1", "dir2", "dir3");
  }

  @Test
  public void childNamesThatAreFiles() throws Exception {
    createFile("abc/text1.txt");
    createFile("abc/text2.txt");
    createFile("abc/text3.txt");

    assertThat(fileSystem.childNames(path("abc"))).containsOnly("text1.txt", "text2.txt", "text3.txt");
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
  public void filesFromThrowsExceptionForInvalidPath() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {

      try {
        fileSystem.filesFrom(path(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void filesFrom() throws Exception {
    createFile("abc/text1.txt");
    createFile("abc/text2.txt");
    createFile("abc/def/text3.txt");
    assertThat(fileSystem.filesFrom(path("abc"))).containsOnly(path("text1.txt"),
        path("text2.txt"), path("def/text3.txt"));
  }

  @Test
  public void filesFromThrowsExceptionWhenPathDoesNotExist() throws Exception {
    try {
      fileSystem.filesFrom(path("abc")).iterator().hasNext();
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
    LineReader reader = new LineReader(new InputStreamReader(
        fileSystem.createInputStream(path(path))));

    assertThat(reader.readLine()).isEqualTo(line);
    assertThat(reader.readLine()).isNull();
  }

  @Test
  public void createOutputStreamThrowsExceptionForInvalidPath() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {

      try {
        fileSystem.createOutputStream(path(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void cannotCreateOutputStreamWhenFileIsADirectory() throws Exception {
    createFile("abc/def/file.txt");
    try {
      fileSystem.createOutputStream(path("abc/def"));
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void createInputStreamThrowsExceptionForInvalidPath() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {

      try {
        fileSystem.createInputStream(path(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void cannotCreateInputStreamWhenFileIsADirectory() throws Exception {
    createFile("abc/def/file.txt");
    try {
      fileSystem.createInputStream(path("abc/def"));
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void copyThrowsExceptionForInvalidSourcePath() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {

      try {
        fileSystem.copy(path(path), path("abc"));
        Assert.fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void copyThrowsExceptionForInvalidDestinationPath() throws Exception {
    for (String path : PathTest.listOfInvalidPaths()) {

      try {
        fileSystem.copy(path("abc"), path(path));
        Assert.fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void copying() throws Exception {
    String line = "abcdefgh";
    String source = "a/b/file1.txt";
    String destination = "d/e/file2.txt";

    createFile(source, line);

    fileSystem.copy(path(source), path(destination));

    LineReader reader = new LineReader(new InputStreamReader(
        fileSystem.createInputStream(path(destination))));

    assertThat(reader.readLine()).isEqualTo(line);
    assertThat(reader.readLine()).isNull();
  }

  @Test
  public void cannotCopyFromADirectory() throws Exception {
    createFile("abc/def/file.txt");
    try {
      fileSystem.copy(path("abc/def"), path("xyz/output.txt"));
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void cannotCopyToADirectory() throws Exception {
    createFile("abc/def/file.txt");
    createFile("xyz/prs/file.txt");

    try {
      fileSystem.copy(path("abc/def/file.txt"), path("xyz/"));
    } catch (FileSystemException e) {
      // expected
    }
  }

  private void createFile(String path, String line) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(fileSystem.createOutputStream(path(path)));
    writer.write(line);
    writer.close();
  }

  private void createFile(String path) throws IOException {
    fileSystem.createOutputStream(path(path)).close();
  }
}
