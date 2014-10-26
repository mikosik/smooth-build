package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.Conversions.convertFunctionName;

import javax.inject.Inject;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.parse.Builtin;

import com.google.common.collect.ImmutableMap;

public class ImplicitConverter {
  private final Module builtinModule;

  @Inject
  public ImplicitConverter(@Builtin Module builtinModule) {
    this.builtinModule = builtinModule;
  }

  public <T extends Value> Expr<T> apply(SType<T> destinationType, Expr<?> source) {
    SType<?> sourceType = source.type();
    if (sourceType == destinationType) {
      /*
       * Cast is safe as 'if' above checked that source has proper type.
       */
      @SuppressWarnings("unchecked")
      Expr<T> expr = (Expr<T>) source;
      return expr;
    }

    Name functionName = convertFunctionName(sourceType, destinationType);

    /*
     * Cast is safe as we assume that STypes.convertFunctionName()
     * returns name of correct function.
     */
    @SuppressWarnings("unchecked")
    Function<T> function = (Function<T>) builtinModule.getFunction(functionName);

    String paramName = function.params().get(0).name();
    return new CallExpr<>(function, true, source.codeLocation(), ImmutableMap.of(paramName,
        source));
  }
}
