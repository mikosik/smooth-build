package org.smoothbuild.out.console;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;

public class ConsoleTest {
  @Test
  public void error() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    var console = new Console(new PrintWriter(outputStream, true));
    console.error("sth bad happened.");

    assertThat(outputStream.toString())
        .isEqualTo("smooth: error: sth bad happened.\n");
  }
}
