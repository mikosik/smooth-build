package org.smoothbuild.run.eval;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;

import java.util.Set;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.out.log.Level;

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
