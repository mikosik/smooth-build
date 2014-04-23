package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CallExpr<T extends SValue> extends Expr<T> {
  private final Function<T> function;
  private final ImmutableMap<String, ? extends Expr<?>> args;

  public CallExpr(Function<T> function, CodeLocation codeLocation,
      ImmutableMap<String, ? extends Expr<?>> args) {
    super(function.type(), ImmutableList.copyOf(args.values()), codeLocation);
    this.function = function;
    this.args = args;
  }

  @Override
  public SType<T> type() {
    return function.type();
  }

  @Override
  public ImmutableList<? extends Expr<?>> dependencies() {
    return function.dependencies(args);
  }

  @Override
  public TaskWorker<T> createWorker() {
    return function.createWorker(args, codeLocation());
  }
}
