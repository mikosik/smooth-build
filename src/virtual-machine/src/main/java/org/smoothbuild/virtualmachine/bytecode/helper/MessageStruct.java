package org.smoothbuild.virtualmachine.bytecode.helper;

import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Level.FATAL;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.common.log.Level.WARNING;

import java.util.Set;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;

public class MessageStruct {
  private static final Set<String> SEVERITIES =
      Set.of(FATAL.name(), ERROR.name(), WARNING.name(), INFO.name());
  private static final int TEXT_IDX = 0;
  private static final int SEVERITY_IDX = 1;

  public static boolean containsErrorOrAbove(ArrayB messages) throws BytecodeException {
    return messages.elements(TupleB.class).anyMatches(MessageStruct::isErrorOrAbove);
  }

  private static boolean isErrorOrAbove(TupleB message) throws BytecodeException {
    String severity = severity(message);
    return severity.equals(ERROR.name()) || severity.equals(FATAL.name());
  }

  public static boolean containsFatal(ArrayB messages) throws BytecodeException {
    return messages.elements(TupleB.class).anyMatches(m -> severity(m).equals(FATAL.name()));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(ArrayB messages) throws BytecodeException {
    return !messages.elements(TupleB.class).iterator().hasNext();
  }

  public static Level level(ExprB message) throws BytecodeException {
    return Level.valueOf(severity(message));
  }

  public static String severity(ExprB message) throws BytecodeException {
    return messageSeverity((TupleB) message).toJ();
  }

  public static String text(ExprB message) throws BytecodeException {
    return messageText((TupleB) message).toJ();
  }

  public static StringB messageText(TupleB message) throws BytecodeException {
    return (StringB) message.get(TEXT_IDX);
  }

  public static StringB messageSeverity(TupleB message) throws BytecodeException {
    return (StringB) message.get(SEVERITY_IDX);
  }
}