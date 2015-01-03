package org.smoothbuild.lang.expr;

import java.util.Map;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.nativ.NativeFunctionLegacy;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.NativeCallWorker;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class NativeCallExpression extends Expression {
  private final Function function;
  private final boolean isGenerated;
  private final ImmutableMap<String, ? extends Expression> args;

  public NativeCallExpression(NativeFunctionLegacy function, boolean isGenerated,
      CodeLocation codeLocation, Map<String, ? extends Expression> args) {
    super(function.type(), ImmutableList.copyOf(args.values()), codeLocation);

    this.function = function;
    this.isGenerated = isGenerated;
    this.args = ImmutableMap.copyOf(args);
  }

  @Override
  public TaskWorker createWorker() {
    NativeFunctionLegacy nativeFunction = (NativeFunctionLegacy) function;
    ImmutableList<String> parameterNames = ImmutableList.copyOf(args.keySet());
    return new NativeCallWorker(nativeFunction, parameterNames, isGenerated, codeLocation());
  }
}
