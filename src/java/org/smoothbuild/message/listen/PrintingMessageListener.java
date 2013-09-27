package org.smoothbuild.message.listen;

import java.io.PrintStream;

import org.smoothbuild.message.message.Message;

public class PrintingMessageListener implements MessageListener {
  private final PrintStream printStream;

  public PrintingMessageListener() {
    this(System.out);
  }

  public PrintingMessageListener(PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void report(Message message) {
    printStream.println(message.toString());
  }
}
