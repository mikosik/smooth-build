package org.smoothbuild.io.fs.mem;

import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.OutputStreamWriter;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;

public class MemoryFileTest {
  private final MemoryDirectory parent = mock(MemoryDirectory.class);
  private final Path name = Path.path("some/path");
  private final Path otherName = Path.path("other/path");
  private MemoryFile file;
  private OutputStreamWriter writer;
  private String line;

  @Before
  public void before() {
    givenTest(this);
    given(file = new MemoryFile(parent, name));
  }

  @Test
  public void name() {
    given(file = new MemoryFile(parent, name));
    when(file.name());
    thenReturned(name);
  }

  @Test
  public void parent() throws Exception {
    given(file = new MemoryFile(parent, name));
    when(file.parent());
    thenReturned(sameInstance(parent));
  }

  @Test
  public void isFile() throws Exception {
    when(file).isFile();
    thenReturned(true);
  }

  @Test
  public void isDirectory() throws Exception {
    when(file).isDirectory();
    thenReturned(false);
  }

  @Test
  public void hasChildReturnsFalse() {
    given(file = new MemoryFile(parent, name));
    when(file).hasChild(otherName);
    thenReturned(false);
  }

  @Test
  public void childThrowsException() {
    given(file = new MemoryFile(parent, name));
    when(file).child(otherName);
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void childNamesThrowsException() {
    when(file).childNames();
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void addChildThrowsException() {
    when(file).addChild(mock(MemoryElement.class));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void readingFromNonexistentFileFails() throws Exception {
    when(file).openInputStream();
    thenThrown(FileSystemError.class);
  }

  @Test
  public void writingAndReading() throws Exception {
    given(writer = new OutputStreamWriter(file.openOutputStream()));
    given(writer).write(line);
    given(writer).close();
    thenEqual(line, inputStreamToString(file.openInputStream()));
  }
}
