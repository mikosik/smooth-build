package org.smoothbuild.fs.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class StreamsTest {
  byte[] bytes = new byte[] { 1, 2, 3 };
  InputStream inputStream = new ByteArrayInputStream(bytes);
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  @Test
  public void bytes_are_copied_from_input_stream_to_output_stream() throws Exception {
    Streams.copy(inputStream, outputStream);
    assertThat(outputStream.toByteArray()).isEqualTo(bytes);
  }

  @Test
  public void input_stream_is_closed_by_copy() throws IOException {
    inputStream = Mockito.mock(InputStream.class);
    when(inputStream.read((byte[]) Matchers.any())).thenReturn(-1);
    Streams.copy(inputStream, outputStream);
    verify(inputStream).close();
  }

  @Test
  public void output_stream_is_closed_by_copy() throws IOException {
    outputStream = Mockito.mock(ByteArrayOutputStream.class);
    Streams.copy(inputStream, outputStream);
    verify(outputStream).close();
  }
}
