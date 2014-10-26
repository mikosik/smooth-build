package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.Conversions.convertFunctionName;

import javax.inject.Inject;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.parse.Builtin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ExprConverter {
  private final Module builtinModule;

  @Inject
  public ExprConverter(@Builtin Module builtinModule) {
    this.builtinModule = builtinModule;
  }

  public <T extends SValue> ImmutableList<Expr<T>> convertExprs(SType<T> type,
      Iterable<? extends Expr<?>> expressions) {
    ImmutableList.Builder<Expr<T>> builder = ImmutableList.builder();
    for (Expr<?> expr : expressions) {
      builder.add(convertExpr(type, expr));
    }
    return builder.build();
  }

  public <T extends SValue> Expr<T> convertExpr(SType<T> destinationType, Expr<?> source) {
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
