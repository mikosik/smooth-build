package org.smoothbuild.lang.object.base;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.lang.object.db.MessageStruct.messageSeverity;
import static org.smoothbuild.lang.object.db.MessageStruct.messageText;

import org.smoothbuild.cli.console.Level;

import com.google.common.collect.ImmutableSet;

public class Messages {
  public static final String INFO = "INFO";
  public static final String WARNING = "WARNING";
  public static final String ERROR = "ERROR";
  private static final ImmutableSet<String> SEVERITIES = ImmutableSet.of(ERROR, WARNING, INFO);

  public static boolean containsErrors(Array messages) {
    return stream(messages.asIterable(Struct.class))
        .anyMatch(m -> severity(m).equals(ERROR));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(Array messages) {
    return !messages.asIterable(Struct.class).iterator().hasNext();
  }

  public static Level level(SObject message) {
    return Level.valueOf(severity(message));
  }

  public static String severity(SObject message) {
    return messageSeverity((Struct) message).jValue();
  }

  public static String text(SObject message) {
    return messageText((Struct) message).jValue();
  }
}
