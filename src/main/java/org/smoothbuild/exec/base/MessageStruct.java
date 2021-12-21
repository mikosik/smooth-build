package org.smoothbuild.exec.base;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.Set;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.db.object.obj.val.TupleB;

public class MessageStruct {
  private static final Set<String> SEVERITIES = Set.of(ERROR.name(), WARNING.name(), INFO.name());
  private static final int TEXT_IDX = 0;
  private static final int SEVERITY_IDX = 1;

  public static boolean containsErrors(ArrayB messages) {
    return messages.elems(TupleB.class)
        .stream()
        .anyMatch(m -> severity(m).equals(ERROR.name()));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(ArrayB messages) {
    return !messages.elems(TupleB.class).iterator().hasNext();
  }

  public static Level level(ObjB message) {
    return Level.valueOf(severity(message));
  }

  public static String severity(ObjB message) {
    return messageSeverity((TupleB) message).toJ();
  }

  public static String text(ObjB message) {
    return messageText((TupleB) message).toJ();
  }

  public static StringB messageText(TupleB message) {
    return (StringB) message.get(TEXT_IDX);
  }

  public static StringB messageSeverity(TupleB message) {
    return (StringB) message.get(SEVERITY_IDX);
  }
}
