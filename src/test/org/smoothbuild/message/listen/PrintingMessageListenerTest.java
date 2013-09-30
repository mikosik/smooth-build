package org.smoothbuild.message.listen;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.message.message.MessageType.ERROR;

import java.io.PrintStream;

import org.junit.Test;
import org.smoothbuild.message.message.Message;

public class PrintingMessageListenerTest {
  PrintStream printStream = mock(PrintStream.class);
  PrintingMessageListener printingMessageListener = new PrintingMessageListener(printStream);

  @Test
  public void messageIsPrinted() throws Exception {
    String message = "message string";
    printingMessageListener.report(new MyMessage(message));
    verify(printStream).println(message);
  }

  public static class MyMessage extends Message {
    private final String toStrigValue;

    public MyMessage(String toStringValue) {
      super(ERROR, "");
      this.toStrigValue = toStringValue;
    }

    @Override
    public String toString() {
      return toStrigValue;
    }
  }
}
