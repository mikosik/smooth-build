package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.expr.Expressions.callExpression;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Conversions;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;

public class ImplicitConverter {
  private final Map<Name, Function> builtinModule;

  @Inject
  public ImplicitConverter(Map<Name, Function> builtinModule) {
    this.builtinModule = builtinModule;
  }

  public <T extends Value> Expression apply(Type destinationType, Expression source) {
    Type sourceType = source.type();
    if (sourceType == destinationType) {
      return source;
    }

    Name functionName = Conversions.convertFunctionName(sourceType, destinationType);
    Function function = builtinModule.get(functionName);

    return callExpression(function, true, source.codeLocation(), asList(source));
  }
}
