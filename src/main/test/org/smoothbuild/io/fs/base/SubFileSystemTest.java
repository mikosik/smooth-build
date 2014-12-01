package org.smoothbuild.io.fs.base;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.Test;

public class SubFileSystemTest {
  FileSystem fileSystem = mock(FileSystem.class);
  Path root = path("my/root");
  Path path = path("my/path");
  Path absolutePath = root.append(path);
  List<String> strings;
  List<Path> pathList;
  InputStream inputStream;

  SubFileSystem subFileSystem = new SubFileSystem(fileSystem, root);

  @Test
  public void pathKind() throws Exception {
    given(willReturn(FILE), fileSystem).pathState(absolutePath);
    when(subFileSystem).pathState(path);
    thenReturned(FILE);
  }

  @Test
  public void childNames() {
    given(strings = asList("abc"));
    given(willReturn(strings), fileSystem).filesFrom(absolutePath);
    when(subFileSystem).filesFrom(path);
    thenReturned(sameInstance(strings));
  }

  @Test
  public void filesFrom() {
    given(pathList = asList(path("some/path")));
    given(willReturn(pathList), fileSystem).filesFromRecursive(absolutePath);
    when(subFileSystem).filesFromRecursive(path);
    thenReturned(sameInstance(pathList));
  }

  @Test
  public void deleteDirectoryRecursively() {
    when(subFileSystem).delete(path);
    thenCalled(fileSystem).delete(absolutePath);
  }

  @Test
  public void createInputStream() {
    given(inputStream = mock(InputStream.class));
    given(willReturn(inputStream), fileSystem).openInputStream(absolutePath);
    when(subFileSystem).openInputStream(path);
    thenReturned(sameInstance(inputStream));
  }

  OutputStream outputStream;

  @Test
  public void createOutputStream() {
    given(outputStream = mock(OutputStream.class));
    given(willReturn(outputStream), fileSystem).openOutputStream(absolutePath);
    when(subFileSystem).openOutputStream(path);
    thenReturned(sameInstance(outputStream));
  }

  @Test
  public void createLink() {
    Path link = path("my/link");
    Path absoluteLink = root.append(link);

    when(subFileSystem).createLink(link, path);
    thenCalled(fileSystem).createLink(absoluteLink, absolutePath);
  }

  @Test
  public void createDir() {
    Path path = path("my/dir");
    Path absolutePath = root.append(path);

    when(subFileSystem).createDir(path);
    thenCalled(fileSystem).createDir(absolutePath);
  }
}
