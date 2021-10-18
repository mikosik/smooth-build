package org.smoothbuild.exec.base;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.Set;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;

public class MessageRec {
  private static final Set<String> SEVERITIES = Set.of(ERROR.name(), WARNING.name(), INFO.name());
  private static final int TEXT_INDEX = 0;
  private static final int SEVERITY_INDEX = 1;

  public static boolean containsErrors(Array messages) {
    return messages.elements(Struc_.class)
        .stream()
        .anyMatch(m -> severity(m).equals(ERROR.name()));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(Array messages) {
    return !messages.elements(Struc_.class).iterator().hasNext();
  }

  public static Level level(Obj message) {
    return Level.valueOf(severity(message));
  }

  public static String severity(Obj message) {
    return messageSeverity((Struc_) message).jValue();
  }

  public static String text(Obj message) {
    return messageText((Struc_) message).jValue();
  }

  public static Str messageText(Struc_ message) {
    return (Str) message.get(TEXT_INDEX);
  }

  public static Str messageSeverity(Struc_ message) {
    return (Str) message.get(SEVERITY_INDEX);
  }
}
