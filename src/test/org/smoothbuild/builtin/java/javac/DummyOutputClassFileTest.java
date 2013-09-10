package org.smoothbuild.builtin.java.javac;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.testing.common.StreamTester;

public class DummyOutputClassFileTest {
  @Test
  public void canWriteToOutputStream() throws IOException {
    DummyOutputClassFile file = new DummyOutputClassFile("file/path");
    OutputStream outputStream = file.openOutputStream();
    StreamTester.writeAndClose(outputStream, "some content");
  }
}
