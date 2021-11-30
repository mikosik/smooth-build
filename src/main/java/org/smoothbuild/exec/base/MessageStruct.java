package org.smoothbuild.exec.base;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.Set;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;

public class MessageStruct {
  private static final Set<String> SEVERITIES = Set.of(ERROR.name(), WARNING.name(), INFO.name());
  private static final int TEXT_INDEX = 0;
  private static final int SEVERITY_INDEX = 1;

  public static boolean containsErrors(ArrayH messages) {
    return messages.elems(TupleH.class)
        .stream()
        .anyMatch(m -> severity(m).equals(ERROR.name()));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(ArrayH messages) {
    return !messages.elems(TupleH.class).iterator().hasNext();
  }

  public static Level level(ObjH message) {
    return Level.valueOf(severity(message));
  }

  public static String severity(ObjH message) {
    return messageSeverity((TupleH) message).toJ();
  }

  public static String text(ObjH message) {
    return messageText((TupleH) message).toJ();
  }

  public static StringH messageText(TupleH message) {
    return (StringH) message.get(TEXT_INDEX);
  }

  public static StringH messageSeverity(TupleH message) {
    return (StringH) message.get(SEVERITY_INDEX);
  }
}
