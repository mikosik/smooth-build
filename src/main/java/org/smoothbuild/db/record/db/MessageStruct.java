package org.smoothbuild.db.record.db;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.Set;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.base.Tuple;

public class MessageStruct {
  private static final Set<String> SEVERITIES = Set.of(ERROR.name(), WARNING.name(), INFO.name());
  private static final int TEXT_INDEX = 0;
  private static final int SEVERITY_INDEX = 1;

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

  public static RString messageText(Tuple message) {
    return (RString) message.get(TEXT_INDEX);
  }

  public static RString messageSeverity(Tuple message) {
    return (RString) message.get(SEVERITY_INDEX);
  }
}
