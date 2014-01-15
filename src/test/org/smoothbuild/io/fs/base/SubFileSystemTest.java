package org.smoothbuild.io.fs.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.PathState.FILE;

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
    when(fileSystem.pathState(absolutePath)).thenReturn(FILE);
    assertThat(subFileSystem.pathState(path)).isEqualTo(FILE);
  }

  @Test
  public void childNames() {
    ImmutableList<String> strings = ImmutableList.of("abc");
    when(fileSystem.childNames(absolutePath)).thenReturn(strings);
    assertThat(subFileSystem.childNames(path)).isSameAs(strings);
  }

  @Test
  public void filesFrom() {
    ImmutableList<Path> pathList = ImmutableList.of(path("some/path"));
    when(fileSystem.filesFrom(absolutePath)).thenReturn(pathList);
    assertThat(subFileSystem.filesFrom(path)).isSameAs(pathList);
  }

  @Test
  public void deleteDirectoryRecursively() {
    subFileSystem.delete(path);
    verify(fileSystem).delete(absolutePath);
  }

  @Test
  public void createInputStream() {
    InputStream inputStream = mock(InputStream.class);
    when(fileSystem.openInputStream(absolutePath)).thenReturn(inputStream);
    assertThat(subFileSystem.openInputStream(path)).isSameAs(inputStream);
  }

  @Test
  public void createOutputStream() {
    OutputStream outputStream = mock(OutputStream.class);
    when(fileSystem.openOutputStream(absolutePath)).thenReturn(outputStream);
    assertThat(subFileSystem.openOutputStream(path)).isSameAs(outputStream);
  }

  @Test
  public void createLink() {
    Path link = path("my/link");
    Path absoluteLink = root.append(link);

    subFileSystem.createLink(link, path);
    verify(fileSystem).createLink(absoluteLink, absolutePath);
  }

  @Test
  public void createDir() {
    Path path = path("my/dir");
    Path absolutePath = root.append(path);

    subFileSystem.createDir(path);
    verify(fileSystem).createDir(absolutePath);
  }
}
