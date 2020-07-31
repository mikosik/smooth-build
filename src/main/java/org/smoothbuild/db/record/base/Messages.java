package org.smoothbuild.db.record.base;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.db.record.db.MessageStruct.messageSeverity;
import static org.smoothbuild.db.record.db.MessageStruct.messageText;

import java.util.Set;

import org.smoothbuild.cli.console.Level;

public class Messages {
  private static final Set<String> SEVERITIES = Set.of(ERROR.name(), WARNING.name(), INFO.name());

  public static boolean containsErrors(Array messages) {
    return stream(messages.asIterable(Tuple.class))
        .anyMatch(m -> severity(m).equals(ERROR.name()));
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
