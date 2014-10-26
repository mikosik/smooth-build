package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableMap;

public class CallExpr<T extends Value> extends Expression<T> {
  private final Function<T> function;
  private final boolean isGenerated;
  private final ImmutableMap<String, ? extends Expression<?>> args;

  public CallExpr(Function<T> function, boolean isGenerated, CodeLocation codeLocation,
      ImmutableMap<String, ? extends Expression<?>> args) {
    super(function.type(), function.dependencies(args), codeLocation);
    this.function = function;
    this.isGenerated = isGenerated;
    this.args = args;
  }

  @Override
  public TaskWorker<T> createWorker() {
    return function.createWorker(args, isGenerated, codeLocation());
  }
}
