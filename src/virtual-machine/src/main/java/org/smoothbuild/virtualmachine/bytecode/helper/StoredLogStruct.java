package org.smoothbuild.virtualmachine.bytecode.helper;

import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import java.util.Set;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;

public class StoredLogStruct {
  private static final Set<String> LEVELS =
      Set.of(FATAL.name(), ERROR.name(), WARNING.name(), INFO.name());
  private static final int MESSAGE_IDX = 0;
  private static final int LEVEL_IDX = 1;

  public static boolean containsErrorOrAbove(ArrayB storedLogs) throws BytecodeException {
    return storedLogs.elements(TupleB.class).anyMatches(StoredLogStruct::isErrorOrAbove);
  }

  private static boolean isErrorOrAbove(TupleB storedLog) throws BytecodeException {
    String level = levelAsString(storedLog);
    return level.equals(ERROR.name()) || level.equals(FATAL.name());
  }

  public static boolean containsFatal(ArrayB storedLogs) throws BytecodeException {
    return storedLogs.elements(TupleB.class).anyMatches(m -> levelAsString(m).equals(FATAL.name()));
  }

  public static boolean isValidLevel(String level) {
    return LEVELS.contains(level);
  }

  public static boolean isEmpty(ArrayB storedLogs) throws BytecodeException {
    return !storedLogs.elements(TupleB.class).iterator().hasNext();
  }

  public static Level level(ExprB storedLogs) throws BytecodeException {
    return Level.valueOf(levelAsString(storedLogs));
  }

  public static String levelAsString(ExprB storedLog) throws BytecodeException {
    return storedLogLevel((TupleB) storedLog).toJavaString();
  }

  public static String message(ExprB storedLog) throws BytecodeException {
    return storedLogMessage((TupleB) storedLog).toJavaString();
  }

  public static StringB storedLogMessage(TupleB message) throws BytecodeException {
    return (StringB) message.get(MESSAGE_IDX);
  }

  public static StringB storedLogLevel(TupleB message) throws BytecodeException {
    return (StringB) message.get(LEVEL_IDX);
  }
}
