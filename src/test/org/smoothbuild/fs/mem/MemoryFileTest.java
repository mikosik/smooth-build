package org.smoothbuild.fs.mem;

import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.OutputStreamWriter;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.fs.base.exc.FileSystemException;

public class MemoryFileTest {
  MemoryDirectory parent = mock(MemoryDirectory.class);
  String name;
  String otherName;
  MemoryFile file;
  OutputStreamWriter writer;
  String line;

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
    when(file).createInputStream();
    thenThrown(FileSystemException.class);
  }

  @Test
  public void writingAndReading() throws Exception {
    given(writer = new OutputStreamWriter(file.createOutputStream()));
    given(writer).write(line);
    given(writer).close();
    thenEqual(line, inputStreamToString(file.createInputStream()));
  }
}
