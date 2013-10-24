package org.smoothbuild.util;

import static org.smoothbuild.testing.common.StreamTester.inputStreamWithContent;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.InputStream;

import org.junit.Test;

public class StreamsTest {
  String content = "content";
  InputStream inputStream;

  @Test
  public void testInputStreamToString() throws Exception {
    given(inputStream = inputStreamWithContent(content));
    when(Streams.inputStreamToString(inputStream));
    thenReturned(content);
  }

  @Test
  public void testEmptyInputStreamToString() throws Exception {
    given(content = "");
    given(inputStream = inputStreamWithContent(content));
    when(Streams.inputStreamToString(inputStream));
    thenReturned(content);
  }
}
