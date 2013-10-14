package org.smoothbuild.message.listen;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.WARNING;

import java.io.PrintStream;

import org.junit.Test;
import org.smoothbuild.message.message.Message;

public class UserConsoleTest {
  PrintStream printStream = mock(PrintStream.class);
  UserConsole userConsole = new UserConsole(printStream);

  @Test
  public void reporting_message_group_with_error_message() throws Exception {
    String name = "GROUP NAME";
    MessageGroup messageGroup = new MessageGroup(name);
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(messageGroup);
    verify(printStream).println("[GROUP NAME] FAILED");
    verify(printStream).println("ERROR: message string");
  }

  @Test
  public void reporting_message_group_without_error_message() throws Exception {
    String name = "GROUP NAME";
    MessageGroup messageGroup = new MessageGroup(name);
    messageGroup.report(new Message(WARNING, "message string"));

    userConsole.report(messageGroup);
    verify(printStream).println("[GROUP NAME]");
    verify(printStream).println("WARNING: message string");
  }
}
