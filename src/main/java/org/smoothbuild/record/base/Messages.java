package org.smoothbuild.record.base;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.record.db.MessageStruct.messageSeverity;
import static org.smoothbuild.record.db.MessageStruct.messageText;

import org.smoothbuild.cli.console.Level;

import com.google.common.collect.ImmutableSet;

public class Messages {
  public static final String INFO = "INFO";
  public static final String WARNING = "WARNING";
  public static final String ERROR = "ERROR";
  private static final ImmutableSet<String> SEVERITIES = ImmutableSet.of(ERROR, WARNING, INFO);

  public static boolean containsErrors(Array messages) {
    return stream(messages.asIterable(Tuple.class))
        .anyMatch(m -> severity(m).equals(ERROR));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(Array messages) {
    return !messages.asIterable(Tuple.class).iterator().hasNext();
  }

  public static Level level(Record message) {
    return Level.valueOf(severity(message));
  }

  public static String severity(Record message) {
    return messageSeverity((Tuple) message).jValue();
  }

  public static String text(Record message) {
    return messageText((Tuple) message).jValue();
  }
}
