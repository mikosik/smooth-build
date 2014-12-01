/*
 * Copyright 2014 Marcin Mikosik
 * All rights reserved.
 */

package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.task.work.VirtualWorker;

public class DefinedCallExpression<T extends Value> extends Expression<T> {
  private final DefinedFunction<T> function;

  public DefinedCallExpression(DefinedFunction<T> function, CodeLocation codeLocation) {
    super(function.type(), asList(function.root()), codeLocation);
    this.function = function;
  }

  @Override
  public TaskWorker<T> createWorker() {
    return new VirtualWorker<T>(function, codeLocation());
  }
}
