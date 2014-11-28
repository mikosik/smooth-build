/*
 * Copyright 2014 Marcin Mikosik
 * All rights reserved.
 */

package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;

import com.google.common.collect.ImmutableMap;

public class Expressions {
  public static <T extends Value> Expression<T> callExpression(Function<T> function,
      boolean isGenerated, CodeLocation codeLocation,
      ImmutableMap<String, ? extends Expression<?>> args) {
    if (function instanceof NativeFunction<?>) {
      return new NativeCallExpression<T>((NativeFunction<T>) function, isGenerated, codeLocation,
          args);
    } else if (function instanceof DefinedFunction<?>) {
      checkArgument(args.isEmpty());
      checkArgument(!isGenerated);

      return new DefinedCallExpression<T>((DefinedFunction<T>) function, codeLocation);
    }
    throw new RuntimeException("Unsupported instance of Function interface: " + function.getClass());
  }
}
