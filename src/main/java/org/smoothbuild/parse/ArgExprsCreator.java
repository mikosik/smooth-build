package org.smoothbuild.parse;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.expr.ImplicitConverter;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.lang.function.def.args.ParamToArgMapper;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ArgExprsCreator {
  private final ImplicitConverter implicitConverter;

  @Inject
  public ArgExprsCreator(ImplicitConverter implicitConverter) {
    this.implicitConverter = implicitConverter;
  }

  public ImmutableMap<String, Expr<?>> createArgExprs(CodeLocation codeLocation,
      LoggedMessages messages, Function<?> function, Collection<Arg> args) {
    ParamToArgMapper mapper = new ParamToArgMapper(codeLocation, messages, function, args);
    Map<Param, Arg> paramToArgMap = mapper.detectMapping();
    messages.failIfContainsProblems();
    return convert(paramToArgMap);
  }

  private ImmutableMap<String, Expr<?>> convert(Map<Param, Arg> paramToArgMap) {
    Builder<String, Expr<?>> builder = ImmutableMap.builder();

    for (Map.Entry<Param, Arg> entry : paramToArgMap.entrySet()) {
      Param param = entry.getKey();
      Arg arg = entry.getValue();
      Expr<?> expr = implicitConverter.apply(param.type(), arg.expr());
      builder.put(param.name(), expr);
    }

    return builder.build();
  }
}
