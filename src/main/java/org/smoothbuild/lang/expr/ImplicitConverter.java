package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.expr.Expressions.callExpression;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.lang.type.Conversions;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.parse.Builtin;

import com.google.common.collect.ImmutableMap;

public class ImplicitConverter {
  private final Module builtinModule;

  @Inject
  public ImplicitConverter(@Builtin Module builtinModule) {
    this.builtinModule = builtinModule;
  }

  public <T extends Value> Expression apply(Type destinationType, Expression source) {
    Type sourceType = source.type();
    if (sourceType == destinationType) {
      return source;
    }

    Name functionName = Conversions.convertFunctionName(sourceType, destinationType);
    Function function = builtinModule.getFunction(functionName);

    String paramName = function.parameters().get(0).name();
    return callExpression(function, true, source.codeLocation(), ImmutableMap.of(paramName, source));
  }
}
