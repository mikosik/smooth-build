package org.smoothbuild.testing.common;

import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class StreamTesterTest {
  private String content;
  private ByteArrayOutputStream outputStream;

  @Test
  public void write_and_close() throws IOException {
    given(content = "content");
    given(outputStream = new ByteArrayOutputStream());
    when(writeAndClose(outputStream, content));
    thenEqual(outputStream.toString(), content);
  }

  @Test
  public void write_and_close_empty() throws IOException {
    given(content = "");
    given(outputStream = new ByteArrayOutputStream());
    when(writeAndClose(outputStream, content));
    thenEqual(outputStream.toString(), content);
  }
}
