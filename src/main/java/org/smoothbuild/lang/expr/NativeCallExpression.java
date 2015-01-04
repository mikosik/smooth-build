package org.smoothbuild.lang.expr;

import java.util.Map;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.NativeCallWorker;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;

public class NativeCallExpression extends Expression {
  private final NativeFunction function;
  private final boolean isGenerated;

  public NativeCallExpression(NativeFunction function, boolean isGenerated,
      CodeLocation codeLocation, Map<String, ? extends Expression> args) {
    super(function.type(), ImmutableList.copyOf(args.values()), codeLocation);

    this.function = function;
    this.isGenerated = isGenerated;
  }

  @Override
  public TaskWorker createWorker() {
    return new NativeCallWorker(function, isGenerated, codeLocation());
  }
}
