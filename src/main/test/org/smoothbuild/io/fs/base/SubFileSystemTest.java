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
  private final FileSystem fileSystem = mock(FileSystem.class);
  private final Path root = path("my/root");
  private Path path = path("my/path");
  private final Path path2 = path("my/path2");
  private Path absolutePath = root.append(path);
  private final Path absolutePath2 = root.append(path2);
  private List<String> strings;
  private InputStream inputStream;

  private OutputStream outputStream;
  private final SubFileSystem subFileSystem = new SubFileSystem(fileSystem, root);
  private Path link;
  private Path absoluteLink;

  @Test
  public void path_state_is_forwarded() throws Exception {
    given(willReturn(FILE), fileSystem).pathState(absolutePath);
    when(subFileSystem).pathState(path);
    thenReturned(FILE);
  }

  @Test
  public void files_from_is_forwarded() {
    given(strings = asList("abc"));
    given(willReturn(strings), fileSystem).files(absolutePath);
    when(subFileSystem).files(path);
    thenReturned(sameInstance(strings));
  }

  @Test
  public void move_is_forwarded() {
    when(subFileSystem).move(path, path2);
    thenCalled(fileSystem).move(absolutePath, absolutePath2);
  }

  @Test
  public void delete_is_forwarded() {
    when(subFileSystem).delete(path);
    thenCalled(fileSystem).delete(absolutePath);
  }

  @Test
  public void open_input_stream_is_forwarded() {
    given(inputStream = mock(InputStream.class));
    given(willReturn(inputStream), fileSystem).openInputStream(absolutePath);
    when(subFileSystem).openInputStream(path);
    thenReturned(sameInstance(inputStream));
  }

  @Test
  public void open_output_stream_is_forwarded() {
    given(outputStream = mock(OutputStream.class));
    given(willReturn(outputStream), fileSystem).openOutputStream(absolutePath);
    when(subFileSystem).openOutputStream(path);
    thenReturned(sameInstance(outputStream));
  }

  @Test
  public void create_link_is_forwarded() {
    given(link = path("my/link"));
    given(absoluteLink = root.append(link));
    when(subFileSystem).createLink(link, path);
    thenCalled(fileSystem).createLink(absoluteLink, absolutePath);
  }

  @Test
  public void create_dir_is_forwarded() {
    given(path = path("my/dir"));
    given(absolutePath = root.append(path));
    when(subFileSystem).createDir(path);
    thenCalled(fileSystem).createDir(absolutePath);
  }
}
