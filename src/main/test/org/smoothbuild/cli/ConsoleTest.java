package org.smoothbuild.cli;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.InfoMessage;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.WarningMessage;

public class ConsoleTest {
  String name = "GROUP NAME";
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream);
  Console console = new Console(printStream);

  @Test
  public void printing_messages_containing_error_message() throws Exception {
    console.print(name, asList(new ErrorMessage("message string")));
    String expected = " + GROUP NAME\n"
        + "   + ERROR: message string\n";
    assertEquals(expected, outputStream.toString());
  }

  @Test
  public void printing_messages_without_error_message() throws Exception {
    console.print(name, asList(new WarningMessage("message string\nsecond line")));

    String expected = " + GROUP NAME\n"
        + "   + WARNING: message string\n"
        + "     second line\n";
    assertEquals(expected, outputStream.toString());
  }

  // isErrorReported()

  @Test
  public void isErrorReported_returns_false_when_only_info_was_logged() throws Exception {
    console.print(name, asList(new InfoMessage("message string")));
    assertFalse(console.isErrorReported());
  }

  @Test
  public void isErrormReported_returns_false_when_only_warning_was_logged() throws Exception {
    console.print(name, asList(new WarningMessage("message string")));
    assertFalse(console.isErrorReported());
  }

  @Test
  public void isErrorReported_returns_true_when_error_was_logged() throws Exception {
    console.print(name, asList(new ErrorMessage("message string")));
    assertTrue(console.isErrorReported());
  }

  // printFinalSummary()

  @Test
  public void final_summary_is_failed_when_error_was_logged() throws Exception {
    console.print(name, asList(new ErrorMessage("message string")));
    console.printFinalSummary();

    String expected = " + GROUP NAME\n"
        + "   + ERROR: message string\n"
        + "   + 1 error(s)\n";
    assertEquals(expected, outputStream.toString());
  }

  @Test
  public void final_summary_contains_all_stats() throws Exception {
    List<Message> messages = new ArrayList<>();
    messages.add(new InfoMessage("info string"));
    for (int i = 0; i < 2; i++) {
      messages.add(new WarningMessage("warning string"));
    }
    for (int i = 0; i < 3; i++) {
      messages.add(new ErrorMessage("error string"));
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

    builder.append("   + 3 error(s)\n");
    builder.append("   + 2 warning(s)\n");
    builder.append("   + 1 info(s)\n");

    assertEquals(builder.toString(), outputStream.toString());
  }
}
