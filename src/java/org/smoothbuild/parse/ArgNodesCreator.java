package org.smoothbuild.parse;

import java.util.Collection;
import java.util.Map;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.lang.function.def.args.ParamToArgMapper;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ArgNodesCreator {

  public ImmutableMap<String, Node<?>> createArgumentNodes(CodeLocation codeLocation,
      LoggedMessages messages, Function<?> function, Collection<Arg> args) {
    ParamToArgMapper mapper = new ParamToArgMapper(codeLocation, messages, function, args);
    Map<Param, Arg> paramToArgMap = mapper.detectMapping();
    messages.failIfContainsProblems();
    return createArgumentNodes(paramToArgMap);
  }

  private ImmutableMap<String, Node<?>> createArgumentNodes(Map<Param, Arg> paramToArgMap) {
    Builder<String, Node<?>> builder = ImmutableMap.builder();

    for (Map.Entry<Param, Arg> entry : paramToArgMap.entrySet()) {
      Param param = entry.getKey();
      Arg arg = entry.getValue();
      Node<?> node = Convert.ifNeeded(param.type(), arg.node());
      builder.put(param.name(), node);
    }

    return builder.build();
  }
}
