package org.smoothbuild.io.fs.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.willReturn;

import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class SubFileSystemTest {
  FileSystem fileSystem = mock(FileSystem.class);
  Path root = path("my/root");
  Path path = path("my/path");
  Path absolutePath = root.append(path);

  SubFileSystem subFileSystem = new SubFileSystem(fileSystem, root);

  @Test
  public void pathKind() throws Exception {
    given(willReturn(FILE), fileSystem).pathState(absolutePath);
    assertThat(subFileSystem.pathState(path)).isEqualTo(FILE);
  }

  @Test
  public void childNames() {
    ImmutableList<String> strings = ImmutableList.of("abc");
    given(willReturn(strings), fileSystem).childNames(absolutePath);
    assertThat(subFileSystem.childNames(path)).isSameAs(strings);
  }

  @Test
  public void filesFrom() {
    ImmutableList<Path> pathList = ImmutableList.of(path("some/path"));
    given(willReturn(pathList), fileSystem).filesFrom(absolutePath);
    assertThat(subFileSystem.filesFrom(path)).isSameAs(pathList);
  }

  @Test
  public void deleteDirectoryRecursively() {
    subFileSystem.delete(path);
    thenCalled(fileSystem).delete(absolutePath);
  }

  @Test
  public void createInputStream() {
    InputStream inputStream = mock(InputStream.class);
    given(willReturn(inputStream), fileSystem).openInputStream(absolutePath);
    assertThat(subFileSystem.openInputStream(path)).isSameAs(inputStream);
  }

  @Test
  public void createOutputStream() {
    OutputStream outputStream = mock(OutputStream.class);
    given(willReturn(outputStream), fileSystem).openOutputStream(absolutePath);
    assertThat(subFileSystem.openOutputStream(path)).isSameAs(outputStream);
  }

  @Test
  public void createLink() {
    Path link = path("my/link");
    Path absoluteLink = root.append(link);

    subFileSystem.createLink(link, path);
    thenCalled(fileSystem).createLink(absoluteLink, absolutePath);
  }

  @Test
  public void createDir() {
    Path path = path("my/dir");
    Path absolutePath = root.append(path);

    subFileSystem.createDir(path);
    thenCalled(fileSystem).createDir(absolutePath);
  }
}
