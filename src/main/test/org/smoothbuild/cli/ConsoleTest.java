package org.smoothbuild.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.util.Lists.list;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.MessagesDb;
import org.smoothbuild.lang.message.TestingMessagesDb;

import com.google.common.base.Throwables;

public class ConsoleTest {
  private final String name = "GROUP NAME";
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream printStream = new PrintStream(outputStream);
  private final Console console = new Console(printStream);
  private final MessagesDb messagesDb = new TestingMessagesDb();

  @Test
  public void printing_messages_containing_error_message() throws Exception {
    console.print(name, list(messagesDb.error("message string")));
    String expected = " + GROUP NAME\n"
        + "   + ERROR: message string\n";
    assertEquals(expected, outputStream.toString());
  }

  @Test
  public void printing_messages_without_error_message() throws Exception {
    console.print(name, list(messagesDb.warning("message string\nsecond line")));

    String expected = " + GROUP NAME\n"
        + "   + WARNING: message string\n"
        + "     second line\n";
    assertEquals(expected, outputStream.toString());
  }

  // isProblemReported()

  @Test
  public void isProblemReported_returns_false_when_nothing_was_logged() throws Exception {
    console.print(name, list(messagesDb.info("message string")));
    assertFalse(console.isProblemReported());
  }

  @Test
  public void isProblemReported_returns_false_when_only_info_was_logged() throws Exception {
    console.print(name, list(messagesDb.info("message string")));
    assertFalse(console.isProblemReported());
  }

  @Test
  public void isProblemReported_returns_false_when_only_warning_was_logged() throws Exception {
    console.print(name, list(messagesDb.warning("message string")));
    assertFalse(console.isProblemReported());
  }

  @Test
  public void isProblemReported_returns_true_when_error_was_logged() throws Exception {
    console.print(name, list(messagesDb.error("message string")));
    assertTrue(console.isProblemReported());
  }

  @Test
  public void isProblemReported_returns_true_when_failure_was_logged() throws Exception {
    console.print(name, new RuntimeException("message string"));
    assertTrue(console.isProblemReported());
  }

  // printFinalSummary()

  @Test
  public void final_summary_is_failed_when_error_was_logged() throws Exception {
    console.print(name, list(messagesDb.error("message string")));
    console.printFinalSummary();

    String expected = " + GROUP NAME\n"
        + "   + ERROR: message string\n"
        + "   + 1 error(s)\n";
    assertEquals(expected, outputStream.toString());
  }

  @Test
  public void final_summary_contains_all_stats() throws Exception {
    List<Message> messages = new ArrayList<>();
    RuntimeException exception = new RuntimeException("failure message");
    console.print(name, exception);
    for (int i = 0; i < 2; i++) {
      messages.add(messagesDb.error("error string"));
    }
    for (int i = 0; i < 3; i++) {
      messages.add(messagesDb.warning("warning string"));
    }
    for (int i = 0; i < 4; i++) {
      messages.add(messagesDb.info("info string"));
    }

    console.print(name, messages);
    console.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append(Throwables.getStackTraceAsString(exception));
    builder.append(" + GROUP NAME\n");
    for (int i = 0; i < 2; i++) {
      builder.append("   + ERROR: error string\n");
    }
    for (int i = 0; i < 3; i++) {
      builder.append("   + WARNING: warning string\n");
    }
    for (int i = 0; i < 4; i++) {
      builder.append("   + INFO: info string\n");
    }

    builder.append("   + 1 failure(s)\n");
    builder.append("   + 2 error(s)\n");
    builder.append("   + 3 warning(s)\n");
    builder.append("   + 4 info(s)\n");

    assertEquals(builder.toString(), outputStream.toString());
  }
}
