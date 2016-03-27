package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Computer.nativeCallComputer;

import java.util.List;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.task.base.Computer;

public class NativeCallExpression extends Expression {
  private final NativeFunction function;
  private final boolean isGenerated;

  public NativeCallExpression(NativeFunction function, boolean isGenerated,
      CodeLocation codeLocation, List<Expression> args) {
    super(function.type(), args, codeLocation);

    this.function = function;
    this.isGenerated = isGenerated;
  }

  public Computer createComputer() {
    return nativeCallComputer(function, isGenerated, codeLocation());
  }
}
