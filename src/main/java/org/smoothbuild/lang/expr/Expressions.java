package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunctionLegacy;
import org.smoothbuild.message.base.CodeLocation;

public class Expressions {
  public static Expression callExpression(Function function, boolean isGenerated,
      CodeLocation codeLocation, Map<String, ? extends Expression> args) {
    if (function instanceof NativeFunctionLegacy) {
      return new NativeCallExpression((NativeFunctionLegacy) function, isGenerated, codeLocation, args);
    } else if (function instanceof DefinedFunction) {
      checkArgument(args.isEmpty());
      checkArgument(!isGenerated);

      return new DefinedCallExpression((DefinedFunction) function, codeLocation);
    }
    throw new RuntimeException("Unsupported instance of Function interface: " + function.getClass());
  }
}
