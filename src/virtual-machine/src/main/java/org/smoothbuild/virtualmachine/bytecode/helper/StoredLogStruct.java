package org.smoothbuild.virtualmachine.bytecode.helper;

import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import java.util.Set;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;

public class StoredLogStruct {
  private static final Set<String> LEVELS =
      Set.of(FATAL.name(), ERROR.name(), WARNING.name(), INFO.name());
  private static final int MESSAGE_IDX = 0;
  private static final int LEVEL_IDX = 1;

  public static boolean containsErrorOrAbove(BArray storedLogs) throws BytecodeException {
    return storedLogs.elements(BTuple.class).anyMatches(StoredLogStruct::isErrorOrAbove);
  }

  private static boolean isErrorOrAbove(BTuple storedLog) throws BytecodeException {
    String level = levelAsString(storedLog);
    return level.equals(ERROR.name()) || level.equals(FATAL.name());
  }

  public static boolean containsFatal(BArray storedLogs) throws BytecodeException {
    return storedLogs.elements(BTuple.class).anyMatches(m -> levelAsString(m).equals(FATAL.name()));
  }

  public static boolean isValidLevel(String level) {
    return LEVELS.contains(level);
  }

  public static boolean isEmpty(BArray storedLogs) throws BytecodeException {
    return !storedLogs.elements(BTuple.class).iterator().hasNext();
  }

  public static Level level(BExpr storedLogs) throws BytecodeException {
    return Level.valueOf(levelAsString(storedLogs));
  }

  public static String levelAsString(BExpr storedLog) throws BytecodeException {
    return storedLogLevel((BTuple) storedLog).toJavaString();
  }

  public static String message(BExpr storedLog) throws BytecodeException {
    return storedLogMessage((BTuple) storedLog).toJavaString();
  }

  public static BString storedLogMessage(BTuple message) throws BytecodeException {
    return (BString) message.get(MESSAGE_IDX);
  }

  public static BString storedLogLevel(BTuple message) throws BytecodeException {
    return (BString) message.get(LEVEL_IDX);
  }
}
