package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.NativeCallWorker;
import org.smoothbuild.task.work.TaskWorker;

public class NativeCallExpression extends Expression {
  private final NativeFunction function;
  private final boolean isGenerated;

  public NativeCallExpression(NativeFunction function, boolean isGenerated,
      CodeLocation codeLocation, List<? extends Expression> args) {
    super(function.type(), args, codeLocation);

    this.function = function;
    this.isGenerated = isGenerated;
  }

  @Override
  public TaskWorker createWorker() {
    return new NativeCallWorker(function, isGenerated, codeLocation());
  }
}
