/*
 * Copyright 2014 Marcin Mikosik
 * All rights reserved.
 */

package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.NativeCallWorker;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class NativeCallExpression extends Expression {
  private final Function function;
  private final boolean isGenerated;
  private final ImmutableMap<String, ? extends Expression> args;

  public NativeCallExpression(NativeFunction function, boolean isGenerated,
      CodeLocation codeLocation, ImmutableMap<String, ? extends Expression> args) {
    super(function.type(), ImmutableList.copyOf(args.values()), codeLocation);

    this.function = function;
    this.isGenerated = isGenerated;
    this.args = args;
  }

  @Override
  public TaskWorker createWorker() {
    NativeFunction nativeFunction = (NativeFunction) function;
    ImmutableList<String> parameterNames = ImmutableList.copyOf(args.keySet());
    return new NativeCallWorker(nativeFunction, parameterNames, isGenerated, codeLocation());
  }
}
