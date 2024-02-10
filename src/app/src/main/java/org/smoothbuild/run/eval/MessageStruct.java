package org.smoothbuild.run.eval;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;

import java.util.Set;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class MessageStruct {
  private static final Set<String> SEVERITIES =
      Set.of(FATAL.name(), ERROR.name(), WARNING.name(), INFO.name());
  private static final int TEXT_IDX = 0;
  private static final int SEVERITY_IDX = 1;

  public static boolean containsErrorOrAbove(ArrayB messages) throws BytecodeException {
    return messages.elems(TupleB.class).anyMatches(MessageStruct::isErrorOrAbove);
  }

  private static boolean isErrorOrAbove(TupleB message) throws BytecodeException {
    String severity = severity(message);
    return severity.equals(ERROR.name()) || severity.equals(FATAL.name());
  }

  public static boolean containsFatal(ArrayB messages) throws BytecodeException {
    return messages.elems(TupleB.class).anyMatches(m -> severity(m).equals(FATAL.name()));
  }

  public static boolean isValidSeverity(String severity) {
    return SEVERITIES.contains(severity);
  }

  public static boolean isEmpty(ArrayB messages) throws BytecodeException {
    return !messages.elems(TupleB.class).iterator().hasNext();
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
