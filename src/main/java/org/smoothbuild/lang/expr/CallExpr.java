package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableMap;

public class CallExpr<T extends SValue> extends Expr<T> {
  private final Function<T> function;
  private final ImmutableMap<String, ? extends Expr<?>> args;

  public CallExpr(Function<T> function, CodeLocation codeLocation,
      ImmutableMap<String, ? extends Expr<?>> args) {
    super(function.type(), function.dependencies(args), codeLocation);
    this.function = function;
    this.args = args;
  }

  @Override
  public TaskWorker<T> createWorker() {
    return function.createWorker(args, codeLocation());
  }
}
