package org.smoothbuild.lang.function.def;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CallNode<T extends SValue> extends Node<T> {
  private final Function<T> function;
  private final ImmutableMap<String, ? extends Node<?>> args;

  public CallNode(Function<T> function, CodeLocation codeLocation,
      ImmutableMap<String, ? extends Node<?>> args) {
    super(function.type(), ImmutableList.copyOf(args.values()), codeLocation);
    this.function = function;
    this.args = args;
  }

  @Override
  public SType<T> type() {
    return function.type();
  }

  @Override
  public ImmutableList<? extends Node<?>> dependencies() {
    return function.dependencies(args);
  }

  @Override
  public TaskWorker<T> createWorker() {
    return function.createWorker(args, codeLocation());
  }
}
