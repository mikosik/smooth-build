package org.smoothbuild.fs.mem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.Test;
import org.smoothbuild.fs.base.exc.FileSystemException;

import com.google.common.io.LineReader;

public class MemoryFileTest {
  MemoryDirectory parent = mock(MemoryDirectory.class);
  MemoryFile file = new MemoryFile(parent, "name");

  @Test
  public void name() {
    assertThat(file.name()).isEqualTo("name");
  }

  @Test
  public void parent() throws Exception {
    assertThat(file.parent()).isSameAs(parent);
  }

  @Test
  public void isFile() throws Exception {
    assertThat(file.isFile()).isTrue();
  }

  @Test
  public void isDirectory() throws Exception {
    assertThat(file.isDirectory()).isFalse();
  }

  @Test
  public void hasChildThrowsException() {
    try {
      file.hasChild("name");
      fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void childThrowsException() {
    try {
      file.child("name");
      fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void childNamesThrowsException() {
    try {
      file.childNames();
      fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void addChildThrowsException() {
    try {
      file.addChild(mock(MemoryElement.class));
      fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void readingFromNonexistentFileFails() throws Exception {
    try {
      file.createInputStream();
      fail("exception should be thrown");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void writingAndReading() throws Exception {
    String line = "abcdefgh";

    OutputStreamWriter writer = new OutputStreamWriter(file.createOutputStream());
    writer.write(line);
    writer.close();
    LineReader reader = new LineReader(new InputStreamReader(file.createInputStream()));

    assertThat(reader.readLine()).isEqualTo(line);
    assertThat(reader.readLine()).isNull();
  }
}
