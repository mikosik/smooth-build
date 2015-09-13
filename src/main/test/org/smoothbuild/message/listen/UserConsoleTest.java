package org.smoothbuild.message.listen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.smoothbuild.message.base.Message;

public class UserConsoleTest {
  String name = "GROUP NAME";
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream);
  UserConsole userConsole = new UserConsole(printStream);

  @Test
  public void printing_messages_containing_error_message() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(ERROR, "message string"));

    userConsole.print(name, loggedMessages);

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + ERROR: message string\n");

    assertEquals(builder.toString(), outputStream.toString());
  }

  @Test
  public void printing_messages_without_error_message() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(WARNING, "message string\nsecond line"));

    userConsole.print(name, loggedMessages);

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + WARNING: message string\n");
    builder.append("     second line\n");
    assertEquals(builder.toString(), outputStream.toString());
  }

  // isProblemReported()

  @Test
  public void isProblemReported_returns_false_when_only_info_was_logged() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(INFO, "message string"));

    userConsole.print(name, loggedMessages);
    assertFalse(userConsole.isProblemReported());
  }

  @Test
  public void isProblemReported_returns_false_when_only_suggestion_was_logged() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(SUGGESTION, "message string"));

    userConsole.print(name, loggedMessages);
    assertFalse(userConsole.isProblemReported());
  }

  @Test
  public void isProblemReported_returns_false_when_only_warning_was_logged() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(WARNING, "message string"));

    userConsole.print(name, loggedMessages);
    assertFalse(userConsole.isProblemReported());
  }

  @Test
  public void isProblemReported_returns_true_when_error_was_logged() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(ERROR, "message string"));

    userConsole.print(name, loggedMessages);

    assertTrue(userConsole.isProblemReported());
  }

  @Test
  public void isProblemReported_returns_true_when_fatal_was_logged() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(FATAL, "message string"));

    userConsole.print(name, loggedMessages);

    assertTrue(userConsole.isProblemReported());
  }

  // printFinalSummary()

  @Test
  public void final_summary_is_success_when_only_warning_was_logged() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(WARNING, "message string"));

    userConsole.print(name, loggedMessages);
    userConsole.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + WARNING: message string\n");
    builder.append(" + SUCCESS :)\n");
    builder.append("   + 1 warning(s)\n");

    assertEquals(builder.toString(), outputStream.toString());
  }

  @Test
  public void final_summary_is_failed_when_error_was_logged() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(ERROR, "message string"));

    userConsole.print(name, loggedMessages);
    userConsole.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + ERROR: message string\n");
    builder.append(" + FAILED :(\n");
    builder.append("   + 1 error(s)\n");

    assertEquals(builder.toString(), outputStream.toString());
  }

  @Test
  public void final_summary_contains_all_stats() throws Exception {
    LoggedMessages loggedMessages = new LoggedMessages();
    loggedMessages.log(new Message(INFO, "info string"));
    for (int i = 0; i < 2; i++) {
      loggedMessages.log(new Message(SUGGESTION, "suggestion string"));
    }
    for (int i = 0; i < 3; i++) {
      loggedMessages.log(new Message(WARNING, "warning string"));
    }
    for (int i = 0; i < 4; i++) {
      loggedMessages.log(new Message(ERROR, "error string"));
    }
    for (int i = 0; i < 5; i++) {
      loggedMessages.log(new Message(FATAL, "fatal string"));
    }

    userConsole.print(name, loggedMessages);
    userConsole.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + INFO: info string\n");
    for (int i = 0; i < 2; i++) {
      builder.append("   + SUGGESTION: suggestion string\n");
    }
    for (int i = 0; i < 3; i++) {
      builder.append("   + WARNING: warning string\n");
    }
    for (int i = 0; i < 4; i++) {
      builder.append("   + ERROR: error string\n");
    }
    for (int i = 0; i < 5; i++) {
      builder.append("   + FATAL: fatal string\n");
    }

    builder.append(" + FAILED :(\n");
    builder.append("   + 5 fatal(s)\n");
    builder.append("   + 4 error(s)\n");
    builder.append("   + 3 warning(s)\n");
    builder.append("   + 2 suggestion(s)\n");
    builder.append("   + 1 info(s)\n");

    assertEquals(builder.toString(), outputStream.toString());
  }
}
