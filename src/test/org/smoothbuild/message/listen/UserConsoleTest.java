package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.LineBuilder;

public class UserConsoleTest {
  String name = "GROUP NAME";
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream);
  UserConsole userConsole = new UserConsole(printStream);

  @Test
  public void reporting_message_group_with_error_message() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(name, messageGroup);

    LineBuilder builder = new LineBuilder();
    builder.addLine(" + GROUP NAME");
    builder.addLine("   + ERROR: message string");

    assertThat(outputStream.toString()).isEqualTo(builder.build());
  }

  @Test
  public void reporting_message_group_without_error_message() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(WARNING, "message string\nsecond line"));

    userConsole.report(name, messageGroup);

    LineBuilder builder = new LineBuilder();
    builder.addLine(" + GROUP NAME");
    builder.addLine("   + WARNING: message string");
    builder.addLine("     second line");
    assertThat(outputStream.toString()).isEqualTo(builder.build());
  }

  // isProblemReported()

  @Test
  public void isProblemReported_returns_false_when_only_info_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(INFO, "message string"));

    userConsole.report(name, messageGroup);
    assertThat(userConsole.isProblemReported()).isFalse();
  }

  @Test
  public void isProblemReported_returns_false_when_only_suggestion_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(SUGGESTION, "message string"));

    userConsole.report(name, messageGroup);
    assertThat(userConsole.isProblemReported()).isFalse();
  }

  @Test
  public void isProblemReported_returns_false_when_only_warning_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(WARNING, "message string"));

    userConsole.report(name, messageGroup);
    assertThat(userConsole.isProblemReported()).isFalse();
  }

  @Test
  public void isProblemReported_returns_true_when_error_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(name, messageGroup);

    assertThat(userConsole.isProblemReported()).isTrue();
  }

  @Test
  public void isProblemReported_returns_true_when_fatal_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(FATAL, "message string"));

    userConsole.report(name, messageGroup);

    assertThat(userConsole.isProblemReported()).isTrue();
  }

  // printFinalSummary()

  @Test
  public void final_summary_is_success_when_only_warning_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(WARNING, "message string"));

    userConsole.report(name, messageGroup);
    userConsole.printFinalSummary();

    LineBuilder builder = new LineBuilder();
    builder.addLine(" + GROUP NAME");
    builder.addLine("   + WARNING: message string");
    builder.addLine(" + SUCCESS :)");
    builder.addLine("   + 1 warning(s)");

    assertThat(outputStream.toString()).isEqualTo(builder.build());
  }

  @Test
  public void final_summary_is_failed_when_error_was_reported() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
    messageGroup.report(new Message(ERROR, "message string"));

    userConsole.report(name, messageGroup);
    userConsole.printFinalSummary();

    LineBuilder builder = new LineBuilder();
    builder.addLine(" + GROUP NAME");
    builder.addLine("   + ERROR: message string");
    builder.addLine(" + FAILED :(");
    builder.addLine("   + 1 error(s)");

    assertThat(outputStream.toString()).isEqualTo(builder.build());
  }

  @Test
  public void final_summary_contains_all_stats() throws Exception {
    MessageGroup messageGroup = new MessageGroup();
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
    for (int i = 0; i < 5; i++) {
      messageGroup.report(new Message(FATAL, "fatal string"));
    }

    userConsole.report(name, messageGroup);
    userConsole.printFinalSummary();

    LineBuilder builder = new LineBuilder();
    builder.addLine(" + GROUP NAME");
    builder.addLine("   + INFO: info string");
    for (int i = 0; i < 2; i++) {
      builder.addLine("   + SUGGESTION: suggestion string");
    }
    for (int i = 0; i < 3; i++) {
      builder.addLine("   + WARNING: warning string");
    }
    for (int i = 0; i < 4; i++) {
      builder.addLine("   + ERROR: error string");
    }
    for (int i = 0; i < 5; i++) {
      builder.addLine("   + FATAL: fatal string");
    }

    builder.addLine(" + FAILED :(");
    builder.addLine("   + 5 fatal(s)");
    builder.addLine("   + 4 error(s)");
    builder.addLine("   + 3 warning(s)");
    builder.addLine("   + 2 suggestion(s)");
    builder.addLine("   + 1 info(s)");

    assertThat(outputStream.toString()).isEqualTo(builder.build());
  }
}
