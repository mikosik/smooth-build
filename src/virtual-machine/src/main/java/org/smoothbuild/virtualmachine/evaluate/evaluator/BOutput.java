package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsErrorOrAbove;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BOutput {
  private final Maybe<BValue> value;
  private final BArray storedLogs;

  private BOutput(Maybe<BValue> value, BArray storedLogs) {
    this.value = value;
    this.storedLogs = storedLogs;
  }

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

  public Maybe<BValue> value() {
    return value;
  }

  public BArray storedLogs() {
    return storedLogs;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    return obj instanceof BOutput that
        && Objects.equals(this.value, that.value)
        && Objects.equals(this.storedLogs, that.storedLogs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, storedLogs);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("BOutput")
        .addField("value", value)
        .addField("storedLogs", storedLogs)
        .toString();
  }
}
