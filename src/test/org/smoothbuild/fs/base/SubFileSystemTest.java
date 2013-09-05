package org.smoothbuild.fs.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.plugin.api.Path;

import com.google.common.collect.ImmutableList;

public class SubFileSystemTest {
  FileSystem fileSystem = mock(FileSystem.class);
  Path root = path("my/root");
  Path path = path("my/path");
  Path absolutePath = root.append(path);

  SubFileSystem subFileSystem = new SubFileSystem(fileSystem, root);

  @Test
  public void root() throws Exception {
    Path parentRoot = path("parent/root");
    when(fileSystem.root()).thenReturn(parentRoot);
    assertThat(subFileSystem.root()).isEqualTo(parentRoot.append(root));
  }

  @Test
  public void pathExists() {
    when(fileSystem.pathExists(absolutePath)).thenReturn(true);
    assertThat(subFileSystem.pathExists(path)).isTrue();
  }

  @Test
  public void pathExistsAndIsDirectory() {
    when(fileSystem.pathExistsAndIsDirectory(absolutePath)).thenReturn(true);
    assertThat(subFileSystem.pathExistsAndIsDirectory(path)).isTrue();
  }

  @Test
  public void pathExistsAndIsFile() {
    when(fileSystem.pathExistsAndIsFile(absolutePath)).thenReturn(true);
    assertThat(subFileSystem.pathExistsAndIsFile(path)).isTrue();
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
  public void copy() {
    Path destination = path("destination/file");
    subFileSystem.copy(path, destination);
    verify(fileSystem).copy(absolutePath, root.append(destination));
  }

  @Test
  public void deleteDirectoryRecursively() {
    subFileSystem.deleteDirectoryRecursively(path);
    verify(fileSystem).deleteDirectoryRecursively(absolutePath);
  }

  @Test
  public void createInputStream() {
    InputStream inputStream = mock(InputStream.class);
    when(fileSystem.createInputStream(absolutePath)).thenReturn(inputStream);
    assertThat(subFileSystem.createInputStream(path)).isSameAs(inputStream);
  }

  @Test
  public void createOutputStream() {
    OutputStream outputStream = mock(OutputStream.class);
    when(fileSystem.createOutputStream(absolutePath)).thenReturn(outputStream);
    assertThat(subFileSystem.createOutputStream(path)).isSameAs(outputStream);
  }

}
