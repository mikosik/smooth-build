package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.CodeLocation;

public class Expressions {
  public static Expression callExpression(Function function, boolean isGenerated,
      CodeLocation codeLocation, List<? extends Expression> args) {
    if (function instanceof NativeFunction) {
      return new NativeCallExpression((NativeFunction) function, isGenerated, codeLocation, args);
    } else if (function instanceof DefinedFunction) {
      checkArgument(args.isEmpty());
      checkArgument(!isGenerated);

      return new DefinedCallExpression((DefinedFunction) function, codeLocation);
    }
    throw new RuntimeException("Unsupported instance of Function interface: " + function.getClass());
  }
}
