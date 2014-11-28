package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.NativeCallWorker;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.task.work.VirtualWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CallExpression<T extends Value> extends Expression<T> {
  private final Function<T> function;
  private final boolean isGenerated;
  private final ImmutableMap<String, ? extends Expression<?>> args;

  public CallExpression(Function<T> function, boolean isGenerated, CodeLocation codeLocation,
      ImmutableMap<String, ? extends Expression<?>> args) {
    super(function.type(), function.dependencies(args), codeLocation);

    if (function instanceof DefinedFunction<?>) {
      checkArgument(args.isEmpty());
      checkArgument(!isGenerated);
    }

    this.function = function;
    this.isGenerated = isGenerated;
    this.args = args;
  }

  @Override
  public TaskWorker<T> createWorker() {
    if (function instanceof NativeFunction<?>) {
      NativeFunction<T> nativeFunction = (NativeFunction<T>) function;
      ImmutableList<String> parameterNames = ImmutableList.copyOf(args.keySet());
      return new NativeCallWorker<>(nativeFunction, parameterNames, isGenerated, codeLocation());
    } else if (function instanceof DefinedFunction<?>) {
      return new VirtualWorker<T>((DefinedFunction<T>) function, codeLocation());
    }
    throw new RuntimeException("Unsupported instance of Function interface: " + function.getClass());
  }
}
