package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.INFO;
import static org.smoothbuild.message.message.MessageType.SUGGESTION;
import static org.smoothbuild.message.message.MessageType.WARNING;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.smoothbuild.message.message.Message;

public class UserConsoleTest {
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream);
  UserConsole userConsole = new UserConsole(printStream);

  @Test
  public void reporting_message_group_with_error_message() throws Exception {
    String name = "GROUP NAME";
    MessageGroup messageGroup = new MessageGroup(name);
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(messageGroup);

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + ERROR: message string\n");

    assertThat(outputStream.toString()).isEqualTo(builder.toString());
  }

  @Test
  public void reporting_message_group_without_error_message() throws Exception {
    String name = "GROUP NAME";
    MessageGroup messageGroup = new MessageGroup(name);
    messageGroup.report(new Message(WARNING, "message string"));

    userConsole.report(messageGroup);

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + WARNING: message string\n");
    assertThat(outputStream.toString()).isEqualTo(builder.toString());
  }

  @Test
  public void isErrorReported_returns_false_when_only_warning_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup("GROUP NAME");
    messageGroup.report(new Message(WARNING, "message string"));

    userConsole.report(messageGroup);
    assertThat(userConsole.isErrorReported()).isFalse();
  }

  @Test
  public void isErrorReported_returns_true_when_error_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup("GROUP NAME");
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(messageGroup);

    assertThat(userConsole.isErrorReported()).isTrue();
  }

  @Test
  public void final_summary_is_success_when_only_warning_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup("GROUP NAME");
    messageGroup.report(new Message(WARNING, "message string"));

    userConsole.report(messageGroup);
    userConsole.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + WARNING: message string\n");
    builder.append(" + SUCCESS :)\n");
    builder.append("   + 1 warnings\n");

    assertThat(outputStream.toString()).isEqualTo(builder.toString());
  }

  @Test
  public void final_summary_is_failed_when_error_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup("GROUP NAME");
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(messageGroup);
    userConsole.printFinalSummary();

    StringBuilder builder = new StringBuilder();
    builder.append(" + GROUP NAME\n");
    builder.append("   + ERROR: message string\n");
    builder.append(" + FAILED :(\n");
    builder.append("   + 1 errors\n");

    assertThat(outputStream.toString()).isEqualTo(builder.toString());
  }

  @Test
  public void final_summary_contains_all_stats() throws Exception {
    MessageGroup messageGroup = new MessageGroup("GROUP NAME");
    messageGroup.report(new Message(INFO, "info string"));
    for (int i = 0; i < 2; i++) {
      messageGroup.report(new Message(SUGGESTION, "suggestion string"));
    }
    for (int i = 0; i < 3; i++) {
      messageGroup.report(new Message(WARNING, "warning string"));
    }
    for (int i = 0; i < 4; i++) {
      messageGroup.report(new Message(ERROR, "error string"));
    }

    userConsole.report(messageGroup);
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

    builder.append(" + FAILED :(\n");
    builder.append("   + 4 errors\n");
    builder.append("   + 3 warnings\n");
    builder.append("   + 2 suggestions\n");
    builder.append("   + 1 infos\n");

    assertThat(outputStream.toString()).isEqualTo(builder.toString());
  }
}
