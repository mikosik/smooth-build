package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsErrorOrAbove;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public record BOutput(Maybe<BValue> value, BArray storedLogs) {
  /**
   * Do not use. Use static factory methods instead which have proper checks.
   * Record constructors cannot have `throws` clause so we cannot put checks here.
   */
  @Deprecated
  public BOutput {}

  public static BOutput bOutput(BValue value, BArray storedLogs) throws BytecodeException {
    checkArgument(
        !containsErrorOrAbove(storedLogs),
        "Cannot create BOutput with a value and with logs with a problem.");
    return new BOutput(some(value), storedLogs);
  }

  public static BOutput bOutput(BArray storedLogs) throws BytecodeException {
    checkArgument(
        containsErrorOrAbove(storedLogs),
        "Cannot create BOutput without a value and with logs without problem.");
    return new BOutput(none(), storedLogs);
  }
}
