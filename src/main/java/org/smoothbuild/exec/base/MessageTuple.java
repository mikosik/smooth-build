package org.smoothbuild.exec.base;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.Set;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;

public class MessageTuple {
  private static final Set<String> SEVERITIES = Set.of(ERROR.name(), WARNING.name(), INFO.name());
  private static final int TEXT_INDEX = 0;
  private static final int SEVERITY_INDEX = 1;

  public static boolean containsErrors(Array messages) {
    return stream(messages.elements(Tuple.class))
        .anyMatch(m -> severity(m).equals(ERROR.name()));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(Array messages) {
    return !messages.elements(Tuple.class).iterator().hasNext();
  }

  public static Level level(Obj message) {
    return Level.valueOf(severity(message));
  }

  public static String severity(Obj message) {
    return messageSeverity((Tuple) message).jValue();
  }

  public static String text(Obj message) {
    return messageText((Tuple) message).jValue();
  }

  public static Str messageText(Tuple message) {
    return (Str) message.get(TEXT_INDEX);
  }

  public static Str messageSeverity(Tuple message) {
    return (Str) message.get(SEVERITY_INDEX);
  }
}
