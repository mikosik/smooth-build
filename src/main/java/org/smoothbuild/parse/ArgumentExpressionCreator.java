package org.smoothbuild.parse;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.expr.ImplicitConverter;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.args.Argument;
import org.smoothbuild.lang.function.def.args.Mapper;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ArgumentExpressionCreator {
  private final ImplicitConverter implicitConverter;

  @Inject
  public ArgumentExpressionCreator(ImplicitConverter implicitConverter) {
    this.implicitConverter = implicitConverter;
  }

  public ImmutableMap<String, Expression<?>> createArgExprs(CodeLocation codeLocation,
      LoggedMessages messages, Function<?> function, Collection<Argument> arguments) {
    Mapper mapper = new Mapper(codeLocation, messages, function, arguments);
    Map<Param, Argument> paramToArgMap = mapper.detectMapping();
    messages.failIfContainsProblems();
    return convert(paramToArgMap);
  }

  private ImmutableMap<String, Expression<?>> convert(Map<Param, Argument> paramToArgMap) {
    Builder<String, Expression<?>> builder = ImmutableMap.builder();

    for (Map.Entry<Param, Argument> entry : paramToArgMap.entrySet()) {
      Param param = entry.getKey();
      Argument argument = entry.getValue();
      Expression<?> expression = implicitConverter.apply(param.type(), argument.expression());
      builder.put(param.name(), expression);
    }

    return builder.build();
  }
}
