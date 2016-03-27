package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;

import javax.inject.Inject;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Conversions;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;

public class ImplicitConverter {
  private final Functions functions;

  @Inject
  public ImplicitConverter(Functions functions) {
    this.functions = functions;
  }

  public <T extends Value> Expression apply(Type destinationType, Expression source) {
    Type sourceType = source.type();
    if (sourceType == destinationType) {
      return source;
    }

    Name functionName = Conversions.convertFunctionName(sourceType, destinationType);
    Function function = functions.get(functionName);
    return function.createCallExpression(asList(source), true, source.codeLocation());
  }
}
