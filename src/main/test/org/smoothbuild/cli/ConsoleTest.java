package org.smoothbuild.cli;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.lang.message.CodeLocation.codeLocation;
import static org.smoothbuild.lang.message.MessageType.ERROR;
import static org.smoothbuild.lang.message.MessageType.INFO;
import static org.smoothbuild.lang.message.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.message.Message;

public class ConsoleTest {
  String name = "GROUP NAME";
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream);
  Console console = new Console(printStream);

  @Test
  public void printing_messages_containing_error_message() throws Exception {
    console.print(name, asList(new Message(ERROR, "message string")));

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + ERROR: message string\n");

    assertEquals(builder.toString(), outputStream.toString());
  }

  @Test
  public void printing_messages_without_error_message() throws Exception {
    console.print(name, asList(new Message(WARNING, "message string\nsecond line")));

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + WARNING: message string\n");
    builder.append("     second line\n");
    assertEquals(builder.toString(), outputStream.toString());
  }

  // isErrorReported()

  @Test
  public void isErrorReported_returns_false_when_only_info_was_logged() throws Exception {
    console.print(name, asList(new Message(INFO, "message string")));
    assertFalse(console.isErrorReported());
  }

  @Test
  public void isErrormReported_returns_false_when_only_warning_was_logged() throws Exception {
    console.print(name, asList(new Message(WARNING, "message string")));
    assertFalse(console.isErrorReported());
  }

  @Test
  public void isErrorReported_returns_true_when_error_was_logged() throws Exception {
    console.print(name, asList(new Message(ERROR, "message string")));
    assertTrue(console.isErrorReported());
  }

  // printFinalSummary()

  @Test
  public void final_summary_is_success_when_only_warning_was_logged() throws Exception {
    console.print(name, asList(new Message(WARNING, "message string")));
    console.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + WARNING: message string\n");
    builder.append(" + SUCCESS :)\n");
    builder.append("   + 1 warning(s)\n");

    assertEquals(builder.toString(), outputStream.toString());
  }

  @Test
  public void final_summary_is_failed_when_error_was_logged() throws Exception {
    console.print(name, asList(new Message(ERROR, "message string")));
    console.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + ERROR: message string\n");
    builder.append(" + FAILED :(\n");
    builder.append("   + 1 error(s)\n");

    assertEquals(builder.toString(), outputStream.toString());
  }

  @Test
  public void final_summary_is_failed_when_code_error_was_printed() throws Exception {
    given(outputStream = new ByteArrayOutputStream());
    given(console = new Console(new PrintStream(outputStream)));
    given(console).error(codeLocation(13), "some message");
    when(console).printFinalSummary();
    thenEqual(outputStream.toString(),
        "build.smooth:13: error: some message\n"
            + " + FAILED :(\n"
            + "   + 1 error(s)\n");
  }

  @Test
  public void final_summary_is_failed_when_error_was_printed() throws Exception {
    given(outputStream = new ByteArrayOutputStream());
    given(console = new Console(new PrintStream(outputStream)));
    given(console).error("some message");
    when(console).printFinalSummary();
    thenEqual(outputStream.toString(),
        "error: some message\n"
            + " + FAILED :(\n"
            + "   + 1 error(s)\n");
  }

  @Test
  public void final_summary_contains_all_stats() throws Exception {
    List<Message> messages = new ArrayList<>();
    messages.add(new Message(INFO, "info string"));
    for (int i = 0; i < 2; i++) {
      messages.add(new Message(WARNING, "warning string"));
    }
    for (int i = 0; i < 3; i++) {
      messages.add(new Message(ERROR, "error string"));
    }

    console.print(name, messages);
    console.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + INFO: info string\n");
    for (int i = 0; i < 2; i++) {
      builder.append("   + WARNING: warning string\n");
    }
    for (int i = 0; i < 3; i++) {
      builder.append("   + ERROR: error string\n");
    }

    builder.append(" + FAILED :(\n");
    builder.append("   + 3 error(s)\n");
    builder.append("   + 2 warning(s)\n");
    builder.append("   + 1 info(s)\n");

    assertEquals(builder.toString(), outputStream.toString());
  }
}
