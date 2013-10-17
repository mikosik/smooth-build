package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.WARNING;

import java.io.PrintStream;

import org.junit.Test;
import org.mockito.InOrder;
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
    InOrder inOrder = inOrder(printStream);
    inOrder.verify(printStream).println("[GROUP NAME] FAILED");
    inOrder.verify(printStream).print("  ");
    inOrder.verify(printStream).println("ERROR: message string");
    inOrder.verify(printStream).println("");
    verifyNoMoreInteractions(printStream);
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

  @Test
  public void isErrorReported_returns_false_when_only_warning_was_reported() throws Exception {
    String name = "GROUP NAME";
    MessageGroup messageGroup = new MessageGroup(name);
    messageGroup.report(new Message(WARNING, "message string"));

    userConsole.report(messageGroup);
    assertThat(userConsole.isErrorReported()).isFalse();
  }

  @Test
  public void isErrorReported_returns_true_when_error_was_reported() throws Exception {
    String name = "GROUP NAME";
    MessageGroup messageGroup = new MessageGroup(name);
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(messageGroup);

    assertThat(userConsole.isErrorReported()).isTrue();
  }

  @Test
  public void final_summary_is_success_when_only_warning_was_reported() throws Exception {
    String name = "GROUP NAME";
    MessageGroup messageGroup = new MessageGroup(name);
    messageGroup.report(new Message(WARNING, "message string"));

    userConsole.report(messageGroup);
    userConsole.printFinalSummary();

    verify(printStream).println("*** SUCCESS ***");
  }

  @Test
  public void final_summary_is_failed_when_error_was_reported() throws Exception {
    String name = "GROUP NAME";
    MessageGroup messageGroup = new MessageGroup(name);
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(messageGroup);
    userConsole.printFinalSummary();

    verify(printStream).println("*** FAILED ***");
  }
}
