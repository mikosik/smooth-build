package org.smoothbuild.out.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.PrintWriter;
import org.junit.jupiter.api.Test;

public class ConsoleTest {
  @Test
  void println() {
    var printWriter = mock(PrintWriter.class);
    var console = new Console(printWriter);

    console.println("output message");

    verify(printWriter).println("output message");
  }
}
